package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChecklistDto {
    private UUID requirementId;
    private String requirementName;
    private String description;
    private boolean mandatory;
    private String allowedFileTypes;
    private Integer maxFileSizeMb;
    private boolean requiresExpiryDate;
    private boolean uploaded;
    private boolean verified;
    private boolean rejected;
    private boolean expired;
    private List<DocumentDto> documents;
}
