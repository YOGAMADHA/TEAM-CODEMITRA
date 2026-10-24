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
public class ApplicationStageHistoryDto {
    private UUID id;
    private UUID applicationId;
    private ApplicationStage stage;
    private LocalDateTime stageEnteredAt;
    private LocalDateTime stageExitedAt;
    private String performedByName;
    private String remarks;
    private String reminderRules;
    private boolean reminderSent;
}
