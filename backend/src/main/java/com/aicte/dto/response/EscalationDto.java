package com.aicte.dto.response;

import com.aicte.enums.EscalationLevel;
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
public class EscalationDto {
    private UUID id;
    private UUID applicationId;
    private String applicationNumber;
    private UUID deadlineId;
    private EscalationLevel level;
    private String reason;
    private UUID escalatedToId;
    private String escalatedToName;
    private boolean resolved;
    private String resolvedByName;
    private LocalDateTime resolvedAt;
    private String resolutionRemarks;
    private LocalDateTime createdAt;
}
