package com.aicte.service;

import com.aicte.dto.request.CreateEscalationRequest;
import com.aicte.dto.request.ResolveEscalationRequest;
import com.aicte.dto.response.EscalationDto;
import com.aicte.entity.Application;
import com.aicte.entity.Deadline;
import com.aicte.entity.Escalation;
import com.aicte.entity.User;
import com.aicte.enums.EscalationLevel;
import com.aicte.enums.NotificationType;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.DeadlineRepository;
import com.aicte.repository.EscalationRepository;
import com.aicte.repository.UserRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
public class EscalationService {

    private final EscalationRepository escalationRepository;
    private final ApplicationRepository applicationRepository;
    private final DeadlineRepository deadlineRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Transactional
    public EscalationDto createEscalation(CreateEscalationRequest request, UUID userId, String userEmail) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + request.getApplicationId()));

        Deadline deadline = null;
        if (request.getDeadlineId() != null) {
            deadline = deadlineRepository.findById(request.getDeadlineId()).orElse(null);
        }

        User escalatedTo = null;
        if (request.getEscalatedToId() != null) {
            escalatedTo = userRepository.findById(request.getEscalatedToId()).orElse(null);
        }

        Escalation escalation = Escalation.builder()
                .application(application)
                .deadline(deadline)
                .level(request.getLevel())
                .reason(request.getReason())
                .escalatedTo(escalatedTo)
                .resolved(false)
                .build();
        escalation.setCreatedBy(userEmail);

        Escalation saved = escalationRepository.save(escalation);
        auditService.log("Escalation", saved.getId(), "CREATE", userEmail, null, saved.getLevel().name(), null);

        if (escalatedTo != null) {
            notificationService.sendTemplatedNotification(
                    NotificationType.ESCALATION_ALERT,
                    Map.of(
                            "applicationNumber", application.getApplicationNumber(),
                            "level", saved.getLevel().name(),
                            "reason", saved.getReason()
                    ),
                    escalatedTo.getId(),
                    application.getId()
            );
        }

        return mapToDto(saved);
    }

    @Transactional
    public EscalationDto resolveEscalation(UUID escalationId, ResolveEscalationRequest request, UUID userId, String userEmail) {
        Escalation escalation = escalationRepository.findById(escalationId)
                .orElseThrow(() -> new ResourceNotFoundException("Escalation not found with ID: " + escalationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        escalation.setResolved(true);
        escalation.setResolvedBy(user);
        escalation.setResolvedAt(TimeService.now());
        escalation.setResolutionRemarks(request.getRemarks());
        escalation.setUpdatedBy(userEmail);

        Escalation saved = escalationRepository.save(escalation);
        auditService.log("Escalation", saved.getId(), "RESOLVE", userEmail, null, request.getRemarks(), null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EscalationDto> getActiveEscalations() {
        return escalationRepository.findByResolvedFalse()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EscalationDto> getEscalationsByApplication(UUID applicationId) {
        return escalationRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EscalationDto> getMyEscalations(UUID userId) {
        return escalationRepository.findByEscalatedToIdAndResolvedFalse(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Automatic escalation check for overdue deadlines.
     */
    @Scheduled(fixedDelay = 120000)
    @Transactional
    public void processOverdueDeadlines() {
        LocalDateTime now = TimeService.now();
        List<Deadline> overdue = deadlineRepository.findByResolvedFalseAndDueDateBefore(now);

        for (Deadline deadline : overdue) {
            if (!deadline.isEscalated()) {
                Escalation escalation = Escalation.builder()
                        .application(deadline.getApplication())
                        .deadline(deadline)
                        .level(EscalationLevel.LEVEL_1)
                        .reason("Deadline overdue: " + deadline.getDescription() + " (due: " + deadline.getDueDate() + ")")
                        .resolved(false)
                        .build();
                escalation.setCreatedBy("System");
                escalationRepository.save(escalation);

                deadline.setEscalated(true);
                deadlineRepository.save(deadline);
                log.info("Auto-escalated overdue deadline {} for app {}", deadline.getId(), deadline.getApplication().getApplicationNumber());
            }
        }
    }

    public EscalationDto mapToDto(Escalation e) {
        return EscalationDto.builder()
                .id(e.getId())
                .applicationId(e.getApplication() != null ? e.getApplication().getId() : null)
                .applicationNumber(e.getApplication() != null ? e.getApplication().getApplicationNumber() : null)
                .deadlineId(e.getDeadline() != null ? e.getDeadline().getId() : null)
                .level(e.getLevel())
                .reason(e.getReason())
                .escalatedToId(e.getEscalatedTo() != null ? e.getEscalatedTo().getId() : null)
                .escalatedToName(e.getEscalatedTo() != null ? e.getEscalatedTo().getFirstName() + " " + e.getEscalatedTo().getLastName() : null)
                .resolved(e.isResolved())
                .resolvedByName(e.getResolvedBy() != null ? e.getResolvedBy().getFirstName() + " " + e.getResolvedBy().getLastName() : null)
                .resolvedAt(e.getResolvedAt())
                .resolutionRemarks(e.getResolutionRemarks())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
