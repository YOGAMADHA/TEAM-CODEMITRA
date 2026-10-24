package com.aicte.dto.response;

import com.aicte.enums.DocumentVerificationStatus;
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
public class DocumentDto {
    private UUID id;
    private UUID applicationId;
    private UUID documentRequirementId;
    private String requirementName;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String checksum;
    private Integer versionNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private DocumentVerificationStatus verificationStatus;
    private String verificationRemarks;
    private String verifiedByName;
    private LocalDateTime verifiedAt;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
    private boolean deleted;
}
