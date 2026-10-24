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
public class AssignEvcMemberRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Role is required (e.g. CHAIR, MEMBER, OBSERVER)")
    private String role;
}
