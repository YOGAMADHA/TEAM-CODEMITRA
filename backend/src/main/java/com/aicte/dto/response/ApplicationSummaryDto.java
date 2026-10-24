package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSummaryDto {
    private long totalApplications;
    private long totalApproved;
    private long totalRejected;
    private long totalUnderReview;
    private Map<String, Long> categoryCounts;
}
