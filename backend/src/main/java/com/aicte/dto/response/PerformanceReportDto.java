package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReportDto {
    private UUID officerId;
    private String officerName;
    private long totalAssigned;
    private long totalCompleted;
    private long totalPending;
    private double averageResolutionDays;
}
