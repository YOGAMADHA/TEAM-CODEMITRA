package com.aicte.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {

    @NotNull(message = "Institution ID is required")
    private UUID institutionId;

    @NotBlank(message = "Application category is required")
    private String category;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    private String programName;
    private String programLevel;
    private Integer intakeSanctioned;
    private String remarks;
}
