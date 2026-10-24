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
public class ApplicationDto {

    private UUID id;
    private String applicationNumber;
    private UUID institutionId;
    private String institutionName;
    private String category;
    private String academicYear;
    private String programName;
    private String programLevel;
    private Integer intakeSanctioned;
    private String currentStage;
    private String status;
    private UUID applicantId;
    private String applicantName;
    private UUID assignedOfficerId;
    private String assignedOfficerName;
    private String remarks;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
