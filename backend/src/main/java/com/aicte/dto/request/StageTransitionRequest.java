package com.aicte.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageTransitionRequest {

    @NotNull(message = "Target stage is required")
    @NotBlank(message = "Target stage is required")
    private String targetStage;

    private String remarks;

    private String decision;
}
