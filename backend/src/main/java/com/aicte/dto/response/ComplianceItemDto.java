package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceItemDto {
    private UUID id;
    private UUID applicationId;
    private String category;
    private String description;
    private String responsibleParty;
    private String requiredDocumentType;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private boolean verified;
    private String verifiedByName;
    private LocalDateTime verifiedAt;
    private boolean renewalRequired;
    private LocalDate renewalDate;
    private String escalationLevel;
    private UUID linkedDocumentId;
    private String status;
}
