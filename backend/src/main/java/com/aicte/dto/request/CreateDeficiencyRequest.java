package com.aicte.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateDeficiencyRequest {
    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    private String correctiveAction;

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    private UUID supportingDocumentRequirementId;
}
