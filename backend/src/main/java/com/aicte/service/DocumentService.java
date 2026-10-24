package com.aicte.service;

import com.aicte.dto.request.VerifyDocumentRequest;
import com.aicte.dto.response.DocumentChecklistDto;
import com.aicte.dto.response.DocumentDto;
import com.aicte.entity.Application;
import com.aicte.entity.Document;
import com.aicte.entity.DocumentRequirement;
import com.aicte.entity.User;
import com.aicte.enums.DocumentVerificationStatus;
import com.aicte.enums.NotificationType;
import com.aicte.exception.BadRequestException;
import com.aicte.exception.DuplicateResourceException;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.DocumentRepository;
import com.aicte.repository.DocumentRequirementRepository;
import com.aicte.repository.UserRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentRequirementRepository requirementRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Value("${app.document.allowed-extensions:pdf,jpg,jpeg,png,docx}")
    private String allowedExtensions;

    @Value("${app.document.max-file-size-mb:10}")
    private int maxFileSizeMb;

    @Transactional
    public DocumentDto uploadDocument(UUID applicationId, UUID requirementId, MultipartFile file,
                                      LocalDate issueDate, LocalDate expiryDate, UUID userId, String userEmail) throws IOException {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        DocumentRequirement requirement = null;
        if (requirementId != null) {
            requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(() -> new ResourceNotFoundException("Requirement not found with ID: " + requirementId));
        }

        // Validate file presence
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        // Validate file size
        long maxBytes = (long) maxFileSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BadRequestException("File size exceeds maximum allowed size of " + maxFileSizeMb + " MB");
        }

        // Validate extension
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        List<String> allowedList = Arrays.asList(allowedExtensions.toLowerCase().split(","));
        if (!allowedList.contains(extension.toLowerCase())) {
            throw new BadRequestException("File extension '" + extension + "' is not permitted. Allowed: " + allowedExtensions);
        }

        // Compute SHA-256 Checksum
        byte[] fileBytes = file.getBytes();
        String checksum = calculateChecksum(fileBytes);

        // Check for duplicate file within this application
        Optional<Document> duplicate = documentRepository.findByChecksum(checksum);
        if (duplicate.isPresent() && duplicate.get().getApplication().getId().equals(applicationId) && !duplicate.get().isDeleted()) {
            throw new DuplicateResourceException("An identical document has already been uploaded for this application.");
        }

        // Determine version number and handle replacement
        int versionNumber = 1;
        Document replacedDocument = null;
        if (requirement != null) {
            List<Document> existing = documentRepository.findByApplicationIdAndDocumentRequirementIdAndDeletedFalse(applicationId, requirementId);
            if (!existing.isEmpty()) {
                // Find latest
                Document latest = existing.get(0);
                versionNumber = latest.getVersionNumber() + 1;
                latest.setVerificationStatus(DocumentVerificationStatus.REPLACEMENT_REQUIRED);
                documentRepository.save(latest);
                replacedDocument = latest;
            }
        }

        // Store file in object storage
        String storageKey = String.format("apps/%s/docs/%s_%s", applicationId, UUID.randomUUID(), originalFilename);
        storageService.upload(storageKey, fileBytes, file.getContentType());

        Document doc = Document.builder()
                .application(application)
                .documentRequirement(requirement)
                .fileName(originalFilename)
                .originalFileName(originalFilename)
                .objectStorageKey(storageKey)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .checksum(checksum)
                .versionNumber(versionNumber)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .verificationStatus(DocumentVerificationStatus.NOT_REVIEWED)
                .uploadedBy(user)
                .uploadedAt(TimeService.now())
                .deleted(false)
                .replacedDocument(replacedDocument)
                .build();
        doc.setCreatedBy(userEmail);

        Document saved = documentRepository.save(doc);

        auditService.log("Document", saved.getId(), "UPLOAD", userEmail, null, saved.getFileName(), null);

        notificationService.sendTemplatedNotification(
                NotificationType.DOCUMENT_UPLOADED,
                Map.of("documentName", saved.getFileName(), "applicationNumber", application.getApplicationNumber()),
                userId,
                applicationId
        );

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public byte[] downloadDocument(UUID documentId, String userEmail) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        if (document.isDeleted()) {
            throw new BadRequestException("Document has been deleted");
        }

        auditService.log("Document", document.getId(), "DOWNLOAD", userEmail, null, document.getFileName(), null);
        return storageService.download(document.getObjectStorageKey());
    }

    @Transactional(readOnly = true)
    public Document getDocumentEntity(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));
    }

    @Transactional
    public DocumentDto verifyDocument(UUID documentId, VerifyDocumentRequest request, UUID verifierId, String verifierEmail) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        User verifier = userRepository.findById(verifierId)
                .orElseThrow(() -> new ResourceNotFoundException("Verifier not found with ID: " + verifierId));

        // Important: Manual human verification
        DocumentVerificationStatus newStatus = DocumentVerificationStatus.valueOf(request.getStatus());
        document.setVerificationStatus(newStatus);
        document.setVerificationRemarks(request.getRemarks());
        document.setVerifiedBy(verifier);
        document.setVerifiedAt(TimeService.now());
        document.setUpdatedBy(verifierEmail);

        Document saved = documentRepository.save(document);
        auditService.log("Document", saved.getId(), "VERIFY", verifierEmail, null, newStatus.name(), null);

        NotificationType notifType = newStatus == DocumentVerificationStatus.VERIFIED
                ? NotificationType.DOCUMENT_VERIFIED
                : NotificationType.DOCUMENT_REJECTED;

        notificationService.sendTemplatedNotification(
                notifType,
                Map.of("documentName", saved.getFileName(), "applicationNumber", saved.getApplication().getApplicationNumber(), "remarks", request.getRemarks() != null ? request.getRemarks() : ""),
                saved.getUploadedBy().getId(),
                saved.getApplication().getId()
        );

        return mapToDto(saved);
    }

    @Transactional
    public void softDeleteDocument(UUID documentId, UUID userId, String userEmail) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        document.setDeleted(true);
        document.setUpdatedBy(userEmail);
        documentRepository.save(document);

        auditService.log("Document", document.getId(), "DELETE", userEmail, null, document.getFileName(), null);
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> getDocumentsByApplication(UUID applicationId) {
        return documentRepository.findByApplicationIdAndDeletedFalse(applicationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentChecklistDto> getDocumentChecklist(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        List<DocumentRequirement> requirements = requirementRepository.findByApplicationCategoryAndActiveTrue(application.getApplicationType());
        List<Document> uploadedDocs = documentRepository.findByApplicationIdAndDeletedFalse(applicationId);

        List<DocumentChecklistDto> checklist = new ArrayList<>();
        LocalDate today = TimeService.today();

        for (DocumentRequirement req : requirements) {
            List<Document> matchingDocs = uploadedDocs.stream()
                    .filter(d -> d.getDocumentRequirement() != null && d.getDocumentRequirement().getId().equals(req.getId()))
                    .collect(Collectors.toList());

            boolean uploaded = !matchingDocs.isEmpty();
            boolean verified = matchingDocs.stream().anyMatch(d -> d.getVerificationStatus() == DocumentVerificationStatus.VERIFIED);
            boolean rejected = matchingDocs.stream().anyMatch(d -> d.getVerificationStatus() == DocumentVerificationStatus.REJECTED);
            boolean expired = matchingDocs.stream().anyMatch(d -> d.getExpiryDate() != null && d.getExpiryDate().isBefore(today));

            checklist.add(DocumentChecklistDto.builder()
                    .requirementId(req.getId())
                    .requirementName(req.getName())
                    .description(req.getDescription())
                    .mandatory(req.isMandatory())
                    .allowedFileTypes(req.getAllowedFileTypes())
                    .maxFileSizeMb(req.getMaxFileSizeMb())
                    .requiresExpiryDate(req.isRequiresExpiryDate())
                    .uploaded(uploaded)
                    .verified(verified)
                    .rejected(rejected)
                    .expired(expired)
                    .documents(matchingDocs.stream().map(this::mapToDto).collect(Collectors.toList()))
                    .build());
        }

        return checklist;
    }

    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public DocumentDto mapToDto(Document doc) {
        return DocumentDto.builder()
                .id(doc.getId())
                .applicationId(doc.getApplication() != null ? doc.getApplication().getId() : null)
                .documentRequirementId(doc.getDocumentRequirement() != null ? doc.getDocumentRequirement().getId() : null)
                .requirementName(doc.getDocumentRequirement() != null ? doc.getDocumentRequirement().getName() : null)
                .fileName(doc.getFileName())
                .originalFileName(doc.getOriginalFileName())
                .contentType(doc.getContentType())
                .fileSize(doc.getFileSize())
                .checksum(doc.getChecksum())
                .versionNumber(doc.getVersionNumber())
                .issueDate(doc.getIssueDate())
                .expiryDate(doc.getExpiryDate())
                .verificationStatus(doc.getVerificationStatus())
                .verificationRemarks(doc.getVerificationRemarks())
                .verifiedByName(doc.getVerifiedBy() != null ? doc.getVerifiedBy().getFirstName() + " " + doc.getVerifiedBy().getLastName() : null)
                .verifiedAt(doc.getVerifiedAt())
                .uploadedByName(doc.getUploadedBy() != null ? doc.getUploadedBy().getFirstName() + " " + doc.getUploadedBy().getLastName() : null)
                .uploadedAt(doc.getUploadedAt())
                .deleted(doc.isDeleted())
                .build();
    }
}
