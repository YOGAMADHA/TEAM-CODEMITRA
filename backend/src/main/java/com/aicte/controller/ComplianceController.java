package com.aicte.controller;

import com.aicte.dto.request.CreateComplianceItemRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.ComplianceItemDto;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.ComplianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
@Tag(name = "Compliance Management", description = "Endpoints for tracking undertakings, statutory compliance, and expiration monitoring")
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping
    @PreAuthorize("hasAuthority('COMPLIANCE_MANAGE')")
    @Operation(summary = "Create a compliance requirement item")
    public ResponseEntity<ApiResponse<ComplianceItemDto>> createComplianceItem(
            @Valid @RequestBody CreateComplianceItemRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ComplianceItemDto dto = complianceService.createComplianceItem(request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Compliance item created", dto));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "Get compliance checklist for an application")
    public ResponseEntity<ApiResponse<List<ComplianceItemDto>>> getComplianceItems(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getComplianceItems(applicationId)));
    }

    @PostMapping("/{id}/fulfill")
    @PreAuthorize("hasAuthority('COMPLIANCE_MANAGE') or hasAuthority('DOCUMENT_VERIFY')")
    @Operation(summary = "Mark compliance item fulfilled by linking a verified document")
    public ResponseEntity<ApiResponse<ComplianceItemDto>> markFulfilled(
            @PathVariable UUID id,
            @RequestParam(value = "documentId", required = false) UUID documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ComplianceItemDto dto = complianceService.markFulfilled(id, documentId, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Compliance marked fulfilled", dto));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasAuthority('COMPLIANCE_MANAGE')")
    @Operation(summary = "Get compliance items due within 7 days")
    public ResponseEntity<ApiResponse<List<ComplianceItemDto>>> getExpiringItems() {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getExpiringItems()));
    }
}
