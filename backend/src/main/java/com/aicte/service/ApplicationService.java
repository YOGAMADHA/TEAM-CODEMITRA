package com.aicte.service;

import com.aicte.dto.request.AssignOfficerRequest;
import com.aicte.dto.request.CreateApplicationRequest;
import com.aicte.dto.request.StageTransitionRequest;
import com.aicte.dto.response.ApplicationDto;
import com.aicte.dto.response.ApplicationStageHistoryDto;
import com.aicte.entity.Application;
import com.aicte.entity.ApplicationAssignment;
import com.aicte.entity.ApplicationStageHistory;
import com.aicte.entity.Institution;
import com.aicte.entity.User;
import com.aicte.entity.WorkflowConfiguration;
import com.aicte.enums.ApplicationStage;
import com.aicte.enums.ApplicationStatus;
import com.aicte.enums.NotificationType;
import com.aicte.enums.Priority;
import com.aicte.exception.BadRequestException;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationAssignmentRepository;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.ApplicationStageHistoryRepository;
import com.aicte.repository.InstitutionRepository;
import com.aicte.repository.UserRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final ApplicationStageHistoryRepository stageHistoryRepository;
    private final ApplicationAssignmentRepository assignmentRepository;
    private final WorkflowService workflowService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Transactional
    public ApplicationDto createApplication(CreateApplicationRequest request, UUID userId, String userEmail) {
        Institution institution = institutionRepository.findById(request.getInstitutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with ID: " + request.getInstitutionId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        String appNumber = generateApplicationNumber();

        Application app = Application.builder()
                .applicationNumber(appNumber)
                .institution(institution)
                .applicationType(request.getCategory())
                .academicYear(request.getAcademicYear())
                .currentStage(ApplicationStage.DRAFT)
                .status(ApplicationStatus.ACTIVE)
                .priority(Priority.MEDIUM)
                .remarks(request.getRemarks())
                .build();
        app.setCreatedBy(userEmail);

        Application saved = applicationRepository.save(app);

        // Record initial stage history
        ApplicationStageHistory history = ApplicationStageHistory.builder()
                .application(saved)
                .applicationId(saved.getId())
                .stage(ApplicationStage.DRAFT)
                .stageEnteredAt(TimeService.now())
                .performedBy(user)
                .remarks("Application draft created")
                .reminderSent(false)
                .build();
        stageHistoryRepository.save(history);

        auditService.log("Application", saved.getId(), "CREATE", userEmail, null, saved.getApplicationNumber(), null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public ApplicationDto getApplicationById(UUID id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
        return mapToDto(app);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationDto> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationDto> getApplicationsByInstitution(UUID institutionId, Pageable pageable) {
        return applicationRepository.findByInstitutionId(institutionId, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationDto> getAssignedApplications(UUID officerId, Pageable pageable) {
        return applicationRepository.findByAssignedOfficerId(officerId, pageable).map(this::mapToDto);
    }

    @Transactional
    public ApplicationDto submitApplication(UUID id, UUID userId, String userEmail) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));

        if (app.getCurrentStage() != ApplicationStage.DRAFT) {
            throw new BadRequestException("Only applications in DRAFT stage can be submitted");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        LocalDateTime now = TimeService.now();
        app.setCurrentStage(ApplicationStage.SUBMITTED);
        app.setSubmittedAt(now);
        app.setUpdatedBy(userEmail);

        Application saved = applicationRepository.save(app);

        // Update previous history record
        stageHistoryRepository.findByApplicationIdOrderByStageEnteredAtDesc(id).stream().findFirst().ifPresent(h -> {
            h.setStageExitedAt(now);
            stageHistoryRepository.save(h);
        });

        // Record new stage history
        ApplicationStageHistory history = ApplicationStageHistory.builder()
                .application(saved)
                .applicationId(saved.getId())
                .stage(ApplicationStage.SUBMITTED)
                .stageEnteredAt(now)
                .performedBy(user)
                .remarks("Application submitted by applicant")
                .reminderRules("-7,-3,0,3")
                .reminderSent(false)
                .build();
        stageHistoryRepository.save(history);

        auditService.log("Application", saved.getId(), "SUBMIT", userEmail, "DRAFT", "SUBMITTED", null);

        // Notify
        notificationService.sendTemplatedNotification(
                NotificationType.APPLICATION_SUBMITTED,
                Map.of("applicationNumber", saved.getApplicationNumber()),
                userId,
                saved.getId()
        );

        return mapToDto(saved);
    }

    @Transactional
    public ApplicationDto transitionStage(UUID id, StageTransitionRequest request, UUID userId, String userEmail) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));

        ApplicationStage targetStage = ApplicationStage.valueOf(request.getTargetStage());
        ApplicationStage currentStage = app.getCurrentStage();

        // Validate human-supervised transition against configurable workflow rules
        WorkflowConfiguration config = workflowService.validateTransition(app.getApplicationType(), currentStage, targetStage);

        LocalDateTime now = TimeService.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        app.setCurrentStage(targetStage);
        if (config.getSlaHours() != null) {
            app.setDueDate(now.plusHours(config.getSlaHours()));
        }
        app.setUpdatedBy(userEmail);

        if (targetStage == ApplicationStage.APPROVED || targetStage == ApplicationStage.REJECTED) {
            app.setStatus(ApplicationStatus.CLOSED);
        }

        Application saved = applicationRepository.save(app);

        // Update previous stage history
        stageHistoryRepository.findByApplicationIdOrderByStageEnteredAtDesc(id).stream().findFirst().ifPresent(h -> {
            h.setStageExitedAt(now);
            stageHistoryRepository.save(h);
        });

        // Record new stage history
        ApplicationStageHistory history = ApplicationStageHistory.builder()
                .application(saved)
                .applicationId(saved.getId())
                .stage(targetStage)
                .stageEnteredAt(now)
                .performedBy(user)
                .remarks(request.getRemarks())
                .reminderRules("-7,-3,0,3")
                .reminderSent(false)
                .build();
        stageHistoryRepository.save(history);

        auditService.log("Application", saved.getId(), "STAGE_TRANSITION", userEmail, currentStage.name(), targetStage.name(), null);

        return mapToDto(saved);
    }

    @Transactional
    public ApplicationDto assignOfficer(UUID id, AssignOfficerRequest request, UUID assignedByUserId, String userEmail) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with ID: " + request.getOfficerId()));

        User assignedBy = userRepository.findById(assignedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Assigner not found with ID: " + assignedByUserId));

        app.setAssignedOfficer(officer);
        app.setUpdatedBy(userEmail);
        Application saved = applicationRepository.save(app);

        // Deactivate previous assignments
        assignmentRepository.findByApplicationIdAndActiveTrue(id).ifPresent(a -> {
            a.setActive(false);
            assignmentRepository.save(a);
        });

        ApplicationAssignment assignment = ApplicationAssignment.builder()
                .application(saved)
                .assignedTo(officer)
                .assignedBy(assignedBy)
                .assignedAt(TimeService.now())
                .active(true)
                .remarks(request.getRemarks())
                .build();
        assignmentRepository.save(assignment);

        auditService.log("Application", saved.getId(), "ASSIGN_OFFICER", userEmail, null, officer.getEmail(), null);

        notificationService.sendTemplatedNotification(
                NotificationType.ASSIGNMENT_NOTIFICATION,
                Map.of("applicationNumber", saved.getApplicationNumber()),
                officer.getId(),
                saved.getId()
        );

        return mapToDto(saved);
    }

    @Transactional
    public ApplicationDto withdrawApplication(UUID id, UUID userId, String userEmail) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));

        app.setStatus(ApplicationStatus.WITHDRAWN);
        app.setCurrentStage(ApplicationStage.WITHDRAWN);
        app.setUpdatedBy(userEmail);
        Application saved = applicationRepository.save(app);

        auditService.log("Application", saved.getId(), "WITHDRAW", userEmail, null, "WITHDRAWN", null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationStageHistoryDto> getApplicationTimeline(UUID applicationId) {
        return stageHistoryRepository.findByApplicationIdOrderByStageEnteredAtDesc(applicationId)
                .stream()
                .map(h -> ApplicationStageHistoryDto.builder()
                        .id(h.getId())
                        .applicationId(h.getApplicationId())
                        .stage(h.getStage())
                        .stageEnteredAt(h.getStageEnteredAt())
                        .stageExitedAt(h.getStageExitedAt())
                        .performedByName(h.getPerformedBy() != null ? h.getPerformedBy().getFirstName() + " " + h.getPerformedBy().getLastName() : "System")
                        .remarks(h.getRemarks())
                        .reminderRules(h.getReminderRules())
                        .reminderSent(h.isReminderSent())
                        .build())
                .collect(Collectors.toList());
    }

    public synchronized String generateApplicationNumber() {
        int year = TimeService.now().getYear();
        long count = applicationRepository.count() + 1;
        return String.format("AICTE/%d/%05d", year, count);
    }

    public ApplicationDto mapToDto(Application app) {
        return ApplicationDto.builder()
                .id(app.getId())
                .applicationNumber(app.getApplicationNumber())
                .institutionId(app.getInstitution() != null ? app.getInstitution().getId() : null)
                .institutionName(app.getInstitution() != null ? app.getInstitution().getName() : null)
                .category(app.getApplicationType())
                .academicYear(app.getAcademicYear())
                .currentStage(app.getCurrentStage() != null ? app.getCurrentStage().name() : null)
                .status(app.getStatus() != null ? app.getStatus().name() : null)
                .assignedOfficerId(app.getAssignedOfficer() != null ? app.getAssignedOfficer().getId() : null)
                .assignedOfficerName(app.getAssignedOfficer() != null ? app.getAssignedOfficer().getFirstName() + " " + app.getAssignedOfficer().getLastName() : null)
                .remarks(app.getRemarks())
                .submittedAt(app.getSubmittedAt())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
