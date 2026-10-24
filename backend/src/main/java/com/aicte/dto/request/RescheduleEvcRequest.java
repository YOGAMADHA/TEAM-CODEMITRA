package com.aicte.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class RescheduleEvcRequest {
    @NotNull(message = "New date is required")
    private LocalDateTime newDate;

    @NotBlank(message = "Rescheduling reason is required")
    private String reason;
}
