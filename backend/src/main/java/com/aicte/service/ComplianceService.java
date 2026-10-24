package com.aicte.service;

import com.aicte.dto.request.CreateComplianceItemRequest;
import com.aicte.dto.response.ComplianceItemDto;
import com.aicte.entity.Application;
import com.aicte.entity.ComplianceItem;
import com.aicte.entity.Document;
import com.aicte.entity.User;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.ComplianceItemRepository;
import com.aicte.repository.DocumentRepository;
import com.aicte.repository.UserRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceService {

    private final ComplianceItemRepository complianceItemRepository;
    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public ComplianceItemDto createComplianceItem(CreateComplianceItemRequest request, UUID userId, String userEmail) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + request.getApplicationId()));

        ComplianceItem item = ComplianceItem.builder()
                .application(application)
                .category(request.getCategory())
                .description(request.getDescription())
                .responsibleParty(request.getResponsibleParty())
                .requiredDocumentType(request.getRequiredDocumentType())
                .dueDate(request.getDueDate())
                .renewalRequired(request.isRenewalRequired())
                .escalationLevel(request.getEscalationLevel())
                .status("PENDING")
                .verified(false)
                .build();
        item.setCreatedBy(userEmail);

        ComplianceItem saved = complianceItemRepository.save(item);
        auditService.log("ComplianceItem", saved.getId(), "CREATE", userEmail, null, saved.getCategory(), null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ComplianceItemDto> getComplianceItems(UUID applicationId) {
        return complianceItemRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComplianceItemDto markFulfilled(UUID itemId, UUID documentId, UUID userId, String userEmail) {
        ComplianceItem item = complianceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance item not found with ID: " + itemId));

        User verifier = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Document doc = null;
        if (documentId != null) {
            doc = documentRepository.findById(documentId).orElse(null);
        }

        item.setCompletedDate(TimeService.now());
        item.setVerified(true);
        item.setVerifiedBy(verifier);
        item.setVerifiedAt(TimeService.now());
        item.setLinkedDocument(doc);
        item.setStatus("FULFILLED");
        item.setUpdatedBy(userEmail);

        ComplianceItem saved = complianceItemRepository.save(item);
        auditService.log("ComplianceItem", saved.getId(), "FULFILL", userEmail, null, "FULFILLED", null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ComplianceItemDto> getExpiringItems() {
        return complianceItemRepository.findByDueDateBeforeAndVerifiedFalse(TimeService.now().plusDays(7))
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ComplianceItemDto mapToDto(ComplianceItem c) {
        return ComplianceItemDto.builder()
                .id(c.getId())
                .applicationId(c.getApplication() != null ? c.getApplication().getId() : null)
                .category(c.getCategory())
                .description(c.getDescription())
                .responsibleParty(c.getResponsibleParty())
                .requiredDocumentType(c.getRequiredDocumentType())
                .dueDate(c.getDueDate())
                .completedDate(c.getCompletedDate())
                .verified(c.isVerified())
                .verifiedByName(c.getVerifiedBy() != null ? c.getVerifiedBy().getFirstName() + " " + c.getVerifiedBy().getLastName() : null)
                .verifiedAt(c.getVerifiedAt())
                .renewalRequired(c.isRenewalRequired())
                .renewalDate(c.getRenewalDate())
                .escalationLevel(c.getEscalationLevel())
                .linkedDocumentId(c.getLinkedDocument() != null ? c.getLinkedDocument().getId() : null)
                .status(c.getStatus())
                .build();
    }
}
