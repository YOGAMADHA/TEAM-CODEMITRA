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
public class VerifyDocumentRequest {

    @NotNull(message = "Verification status is required")
    @NotBlank(message = "Verification status is required")
    private String verificationStatus;

    private String remarks;

    public String getStatus() {
        return verificationStatus;
    }
}
