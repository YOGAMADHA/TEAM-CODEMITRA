package com.aicte.dto.request;

import com.aicte.enums.EscalationLevel;
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
public class CreateEscalationRequest {
    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    private UUID deadlineId;

    @NotNull(message = "Escalation level is required")
    private EscalationLevel level;

    @NotBlank(message = "Reason is required")
    private String reason;

    private UUID escalatedToId;
}
