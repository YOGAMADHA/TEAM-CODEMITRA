package com.aicte.dto.response;

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
public class DeficiencyResponseDto {
    private UUID id;
    private UUID deficiencyId;
    private String respondedByName;
    private String responseText;
    private UUID supportingDocumentId;
    private String supportingDocumentName;
    private LocalDateTime respondedAt;
    private String reviewedByName;
    private String reviewDecision;
    private String reviewRemarks;
    private LocalDateTime reviewedAt;
}
