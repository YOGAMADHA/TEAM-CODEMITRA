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
public class PendencyReportDto {
    private Map<String, Long> stageCounts;
    private long totalPending;
    private long totalOverdue;
}
