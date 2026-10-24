package com.aicte.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDeficiencyRequest {
    @NotBlank(message = "Decision is required (ACCEPTED or REJECTED)")
    private String decision;

    private String remarks;
}
