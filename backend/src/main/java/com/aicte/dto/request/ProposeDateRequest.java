package com.aicte.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposeDateRequest {
    @NotNull(message = "Proposed date is required")
    private LocalDateTime proposedDate;

    private String venue;
}
