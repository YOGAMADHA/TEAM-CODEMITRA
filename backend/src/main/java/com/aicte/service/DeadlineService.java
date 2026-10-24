package com.aicte.service;

import com.aicte.dto.request.CreateDeadlineRequest;
import com.aicte.dto.response.DeadlineDto;
import com.aicte.entity.Application;
import com.aicte.entity.ApplicationStageHistory;
import com.aicte.entity.Deadline;
import com.aicte.entity.Notification;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.ApplicationStageHistoryRepository;
import com.aicte.repository.DeadlineRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadlineService {

    private final DeadlineRepository deadlineRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationStageHistoryRepository stageHistoryRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Transactional
    public DeadlineDto createDeadline(CreateDeadlineRequest request, String userEmail) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + request.getApplicationId()));

        Deadline deadline = Deadline.builder()
                .application(application)
                .stage(request.getStage())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .reminderRules(request.getReminderRules())
                .resolved(false)
                .escalated(false)
                .build();
        deadline.setCreatedBy(userEmail);

        Deadline saved = deadlineRepository.save(deadline);
        auditService.log("Deadline", saved.getId(), "CREATE", userEmail, null, saved.getDescription(), null);
        return mapToDto(saved);
    }

    @Transactional
    public DeadlineDto resolveDeadline(UUID deadlineId, String userEmail) {
        Deadline deadline = deadlineRepository.findById(deadlineId)
                .orElseThrow(() -> new ResourceNotFoundException("Deadline not found with ID: " + deadlineId));

        deadline.setResolved(true);
        deadline.setResolvedAt(TimeService.now());
        deadline.setUpdatedBy(userEmail);

        Deadline saved = deadlineRepository.save(deadline);
        auditService.log("Deadline", saved.getId(), "RESOLVE", userEmail, null, "RESOLVED", null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DeadlineDto> getDeadlinesByApplication(UUID applicationId) {
        return deadlineRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeadlineDto> getOverdueDeadlines() {
        return deadlineRepository.findByResolvedFalseAndDueDateBefore(TimeService.now())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Periodic reminder processor: checks pending stage history and deadline records,
     * uses TimeService.now() for time simulation support, and dispatches notifications idempotently.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processDeadlineReminders() {
        LocalDateTime now = TimeService.now();
        List<ApplicationStageHistory> pendingHistory = stageHistoryRepository.findByReminderSentFalse();

        for (ApplicationStageHistory history : pendingHistory) {
            String rulesCsv = history.getReminderRules();
            if (rulesCsv == null || rulesCsv.isBlank()) continue;

            String[] tokens = rulesCsv.split(",");
            for (String token : tokens) {
                try {
                    int offset = Integer.parseInt(token.trim());
                    LocalDateTime trigger = history.getStageEnteredAt().plusDays(offset);
                    if (!trigger.isAfter(now)) {
                        if (history.getAssignedOfficerId() != null) {
                            Notification notif = Notification.builder()
                                    .type("DEADLINE_REMINDER")
                                    .applicationId(history.getApplicationId())
                                    .recipientId(history.getAssignedOfficerId())
                                    .payload(String.format("Reminder: Stage '%s' requires action. Entered on %s.",
                                            history.getStage(), history.getStageEnteredAt()))
                                    .createdAt(now)
                                    .build();
                            notificationService.sendNotification(notif);
                        }
                        history.setReminderSent(true);
                        stageHistoryRepository.save(history);
                        break;
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid reminder rule token '{}' in stage history {}", token, history.getId());
                }
            }
        }
    }

    public DeadlineDto mapToDto(Deadline d) {
        return DeadlineDto.builder()
                .id(d.getId())
                .applicationId(d.getApplication() != null ? d.getApplication().getId() : null)
                .stage(d.getStage())
                .description(d.getDescription())
                .dueDate(d.getDueDate())
                .reminderRules(d.getReminderRules())
                .lastReminderSentAt(d.getLastReminderSentAt())
                .resolved(d.isResolved())
                .resolvedAt(d.getResolvedAt())
                .escalated(d.isEscalated())
                .build();
    }
}
