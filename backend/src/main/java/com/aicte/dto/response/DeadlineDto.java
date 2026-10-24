package com.aicte.dto.response;

import com.aicte.enums.ApplicationStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadlineDto {
    private UUID id;
    private UUID applicationId;
    private ApplicationStage stage;
    private String description;
    private LocalDateTime dueDate;
    private String reminderRules;
    private LocalDateTime lastReminderSentAt;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private boolean escalated;
}
