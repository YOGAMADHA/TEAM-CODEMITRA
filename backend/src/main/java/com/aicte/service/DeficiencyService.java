package com.aicte.service;

import com.aicte.dto.request.CreateDeficiencyRequest;
import com.aicte.dto.request.CreateDeficiencyResponseRequest;
import com.aicte.dto.request.ReviewDeficiencyRequest;
import com.aicte.dto.response.DeficiencyDto;
import com.aicte.dto.response.DeficiencyResponseDto;
import com.aicte.entity.Application;
import com.aicte.entity.Deficiency;
import com.aicte.entity.DeficiencyResponse;
import com.aicte.entity.Document;
import com.aicte.entity.DocumentRequirement;
import com.aicte.entity.User;
import com.aicte.enums.ApplicationStage;
import com.aicte.enums.DeficiencyStatus;
import com.aicte.enums.NotificationType;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.DeficiencyRepository;
import com.aicte.repository.DeficiencyResponseRepository;
import com.aicte.repository.DocumentRepository;
import com.aicte.repository.DocumentRequirementRepository;
import com.aicte.repository.UserRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeficiencyService {

    private final DeficiencyRepository deficiencyRepository;
    private final DeficiencyResponseRepository responseRepository;
    private final ApplicationRepository applicationRepository;
    private final DocumentRequirementRepository requirementRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Transactional
    public DeficiencyDto raiseDeficiency(CreateDeficiencyRequest request, UUID officerId, String officerEmail) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + request.getApplicationId()));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with ID: " + officerId));

        DocumentRequirement requirement = null;
        if (request.getSupportingDocumentRequirementId() != null) {
            requirement = requirementRepository.findById(request.getSupportingDocumentRequirementId()).orElse(null);
        }

        Deficiency deficiency = Deficiency.builder()
                .application(application)
                .category(request.getCategory())
                .description(request.getDescription())
                .correctiveAction(request.getCorrectiveAction())
                .dueDate(request.getDueDate())
                .status(DeficiencyStatus.OPEN)
                .supportingDocumentRequirement(requirement)
                .raisedBy(officer)
                .raisedAt(TimeService.now())
                .build();
        deficiency.setCreatedBy(officerEmail);

        Deficiency saved = deficiencyRepository.save(deficiency);

        // Update application stage to DEFICIENCY_RAISED if scrutiny/rescrutiny
        application.setCurrentStage(ApplicationStage.DEFICIENCY_RAISED);
        applicationRepository.save(application);

        auditService.log("Deficiency", saved.getId(), "RAISE", officerEmail, null, saved.getCategory(), null);

        // Notify applicant
        notificationService.sendTemplatedNotification(
                NotificationType.DEFICIENCY_RAISED,
                Map.of(
                        "applicationNumber", application.getApplicationNumber(),
                        "category", saved.getCategory(),
                        "description", saved.getDescription(),
                        "dueDate", saved.getDueDate() != null ? saved.getDueDate().toString() : ""
                ),
                officerId, // fallback or applicant
                application.getId()
        );

        return mapToDto(saved);
    }

    @Transactional
    public DeficiencyResponseDto respondToDeficiency(UUID deficiencyId, CreateDeficiencyResponseRequest request, UUID userId, String userEmail) {
        Deficiency deficiency = deficiencyRepository.findById(deficiencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Deficiency not found with ID: " + deficiencyId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Document supportingDoc = null;
        if (request.getSupportingDocumentId() != null) {
            supportingDoc = documentRepository.findById(request.getSupportingDocumentId()).orElse(null);
        }

        DeficiencyResponse response = DeficiencyResponse.builder()
                .deficiency(deficiency)
                .respondedBy(user)
                .responseText(request.getResponseText())
                .supportingDocument(supportingDoc)
                .respondedAt(TimeService.now())
                .build();
        response.setCreatedBy(userEmail);

        DeficiencyResponse saved = responseRepository.save(response);

        deficiency.setStatus(DeficiencyStatus.RESPONSE_SUBMITTED);
        deficiencyRepository.save(deficiency);

        Application app = deficiency.getApplication();
        app.setCurrentStage(ApplicationStage.APPLICANT_RESPONSE);
        applicationRepository.save(app);

        auditService.log("DeficiencyResponse", saved.getId(), "RESPOND", userEmail, null, deficiencyId.toString(), null);

        notificationService.sendTemplatedNotification(
                NotificationType.DEFICIENCY_RESPONSE,
                Map.of("applicationNumber", app.getApplicationNumber()),
                deficiency.getRaisedBy().getId(),
                app.getId()
        );

        return mapResponseToDto(saved);
    }

    @Transactional
    public DeficiencyResponseDto reviewResponse(UUID responseId, ReviewDeficiencyRequest request, UUID officerId, String officerEmail) {
        DeficiencyResponse response = responseRepository.findById(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("Deficiency response not found with ID: " + responseId));

        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with ID: " + officerId));

        response.setReviewedBy(officer);
        response.setReviewDecision(request.getDecision());
        response.setReviewRemarks(request.getRemarks());
        response.setReviewedAt(TimeService.now());
        response.setUpdatedBy(officerEmail);

        DeficiencyResponse saved = responseRepository.save(response);

        Deficiency deficiency = response.getDeficiency();
        if ("ACCEPTED".equalsIgnoreCase(request.getDecision())) {
            deficiency.setStatus(DeficiencyStatus.RESOLVED);
            deficiency.setResolvedAt(TimeService.now());
            deficiency.setResolvedBy(officer);
        } else {
            deficiency.setStatus(DeficiencyStatus.REJECTED);
        }
        deficiencyRepository.save(deficiency);

        auditService.log("DeficiencyResponse", saved.getId(), "REVIEW", officerEmail, null, request.getDecision(), null);

        return mapResponseToDto(saved);
    }

    @Transactional
    public DeficiencyDto reopenDeficiency(UUID deficiencyId, String reason, UUID officerId, String officerEmail) {
        Deficiency deficiency = deficiencyRepository.findById(deficiencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Deficiency not found with ID: " + deficiencyId));

        deficiency.setStatus(DeficiencyStatus.REOPENED);
        deficiency.setUpdatedBy(officerEmail);
        Deficiency saved = deficiencyRepository.save(deficiency);

        auditService.log("Deficiency", saved.getId(), "REOPEN", officerEmail, null, reason, null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DeficiencyDto> getDeficienciesByApplication(UUID applicationId) {
        return deficiencyRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DeficiencyDto mapToDto(Deficiency d) {
        List<DeficiencyResponseDto> responses = responseRepository.findByDeficiencyIdOrderByRespondedAtDesc(d.getId())
                .stream()
                .map(this::mapResponseToDto)
                .collect(Collectors.toList());

        return DeficiencyDto.builder()
                .id(d.getId())
                .applicationId(d.getApplication() != null ? d.getApplication().getId() : null)
                .category(d.getCategory())
                .description(d.getDescription())
                .correctiveAction(d.getCorrectiveAction())
                .dueDate(d.getDueDate())
                .status(d.getStatus())
                .supportingDocumentRequirementId(d.getSupportingDocumentRequirement() != null ? d.getSupportingDocumentRequirement().getId() : null)
                .raisedByName(d.getRaisedBy() != null ? d.getRaisedBy().getFirstName() + " " + d.getRaisedBy().getLastName() : null)
                .raisedAt(d.getRaisedAt())
                .resolvedAt(d.getResolvedAt())
                .resolvedByName(d.getResolvedBy() != null ? d.getResolvedBy().getFirstName() + " " + d.getResolvedBy().getLastName() : null)
                .responses(responses)
                .build();
    }

    public DeficiencyResponseDto mapResponseToDto(DeficiencyResponse r) {
        return DeficiencyResponseDto.builder()
                .id(r.getId())
                .deficiencyId(r.getDeficiency() != null ? r.getDeficiency().getId() : null)
                .respondedByName(r.getRespondedBy() != null ? r.getRespondedBy().getFirstName() + " " + r.getRespondedBy().getLastName() : null)
                .responseText(r.getResponseText())
                .supportingDocumentId(r.getSupportingDocument() != null ? r.getSupportingDocument().getId() : null)
                .supportingDocumentName(r.getSupportingDocument() != null ? r.getSupportingDocument().getFileName() : null)
                .respondedAt(r.getRespondedAt())
                .reviewedByName(r.getReviewedBy() != null ? r.getReviewedBy().getFirstName() + " " + r.getReviewedBy().getLastName() : null)
                .reviewDecision(r.getReviewDecision())
                .reviewRemarks(r.getReviewRemarks())
                .reviewedAt(r.getReviewedAt())
                .build();
    }
}
