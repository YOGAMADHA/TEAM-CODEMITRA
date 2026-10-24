package com.aicte.controller;

import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.ApplicationDto;
import com.aicte.dto.response.ApplicationSummaryDto;
import com.aicte.dto.response.PendencyReportDto;
import com.aicte.dto.response.PerformanceReportDto;
import com.aicte.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports & Analytics", description = "Endpoints for pendency metrics, officer performance, and application summary reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/pendency")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get pendency breakdown by application stage")
    public ResponseEntity<ApiResponse<PendencyReportDto>> getPendencyReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getPendencyReport()));
    }

    @GetMapping("/performance/{officerId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get processing performance report for an officer")
    public ResponseEntity<ApiResponse<PerformanceReportDto>> getPerformanceReport(@PathVariable UUID officerId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getPerformanceReport(officerId)));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get overall applications summary")
    public ResponseEntity<ApiResponse<ApplicationSummaryDto>> getApplicationSummary() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getApplicationSummary()));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get overdue applications list")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getOverdueApplications() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getOverdueApplications()));
    }
}
