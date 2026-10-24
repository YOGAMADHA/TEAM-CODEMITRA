package com.aicte.controller;

import com.aicte.dto.request.AssignOfficerRequest;
import com.aicte.dto.request.CreateApplicationRequest;
import com.aicte.dto.request.StageTransitionRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.ApplicationDto;
import com.aicte.dto.response.ApplicationStageHistoryDto;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Endpoints for creating, viewing, assigning, and routing institutional approval applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasAuthority('APPLICATION_CREATE')")
    @Operation(summary = "Create a draft application")
    public ResponseEntity<ApiResponse<ApplicationDto>> createApplication(
            @Valid @RequestBody CreateApplicationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ApplicationDto dto = applicationService.createApplication(request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Application draft created", dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('APPLICATION_VIEW_ASSIGNED') or hasAuthority('REPORT_VIEW') or hasAuthority('AUDIT_VIEW')")
    @Operation(summary = "List all applications with pagination")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> getAllApplications(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getAllApplications(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application details by ID")
    public ResponseEntity<ApiResponse<ApplicationDto>> getApplicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplicationById(id)));
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get applications for a specific institution")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> getByInstitution(
            @PathVariable UUID institutionId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplicationsByInstitution(institutionId, pageable)));
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAuthority('APPLICATION_VIEW_ASSIGNED')")
    @Operation(summary = "Get applications assigned to current officer")
    public ResponseEntity<ApiResponse<Page<ApplicationDto>>> getAssignedApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getAssignedApplications(userDetails.getId(), pageable)));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('APPLICATION_SUBMIT')")
    @Operation(summary = "Submit an application from DRAFT stage")
    public ResponseEntity<ApiResponse<ApplicationDto>> submitApplication(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully",
                applicationService.submitApplication(id, userDetails.getId(), userDetails.getUsername())));
    }

    @PostMapping("/{id}/transition")
    @PreAuthorize("hasAuthority('APPLICATION_ROUTE')")
    @Operation(summary = "Transition application stage with human supervision")
    public ResponseEntity<ApiResponse<ApplicationDto>> transitionStage(
            @PathVariable UUID id,
            @Valid @RequestBody StageTransitionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Application stage updated",
                applicationService.transitionStage(id, request, userDetails.getId(), userDetails.getUsername())));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('APPLICATION_ASSIGN')")
    @Operation(summary = "Assign application to an officer")
    public ResponseEntity<ApiResponse<ApplicationDto>> assignOfficer(
            @PathVariable UUID id,
            @Valid @RequestBody AssignOfficerRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Officer assigned",
                applicationService.assignOfficer(id, request, userDetails.getId(), userDetails.getUsername())));
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAuthority('APPLICATION_CREATE')")
    @Operation(summary = "Withdraw application")
    public ResponseEntity<ApiResponse<ApplicationDto>> withdrawApplication(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn",
                applicationService.withdrawApplication(id, userDetails.getId(), userDetails.getUsername())));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Get stage history timeline for application")
    public ResponseEntity<ApiResponse<List<ApplicationStageHistoryDto>>> getTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplicationTimeline(id)));
    }
}
