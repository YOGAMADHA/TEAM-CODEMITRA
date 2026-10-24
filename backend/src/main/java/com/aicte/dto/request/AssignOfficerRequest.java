package com.aicte.dto.request;

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
public class AssignOfficerRequest {
    @NotNull(message = "Officer ID is required")
    private UUID officerId;

    private String remarks;
}
