package com.aicte.dto.response;

import com.aicte.enums.DeficiencyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeficiencyDto {
    private UUID id;
    private UUID applicationId;
    private String category;
    private String description;
    private String correctiveAction;
    private LocalDateTime dueDate;
    private DeficiencyStatus status;
    private UUID supportingDocumentRequirementId;
    private String raisedByName;
    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;
    private String resolvedByName;
    private List<DeficiencyResponseDto> responses;
}
