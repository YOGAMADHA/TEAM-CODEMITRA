package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashChainVerificationResult {
    private int totalRecords;
    private int verifiedRecords;
    private List<String> brokenLinks;
    private boolean chainIntact;
    private LocalDateTime verifiedAt;
}
