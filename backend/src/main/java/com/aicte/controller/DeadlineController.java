package com.aicte.controller;

import com.aicte.dto.request.CreateDeadlineRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.DeadlineDto;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.DeadlineService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deadlines")
@RequiredArgsConstructor
@Tag(name = "Deadlines", description = "Endpoints for configuring statutory deadlines, tracking overdue stages, and reminder triggers")
public class DeadlineController {

    private final DeadlineService deadlineService;

    @PostMapping
    @PreAuthorize("hasAuthority('DEADLINE_CONFIGURE')")
    @Operation(summary = "Create a deadline for an application")
    public ResponseEntity<ApiResponse<DeadlineDto>> createDeadline(
            @Valid @RequestBody CreateDeadlineRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeadlineDto dto = deadlineService.createDeadline(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Deadline created", dto));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "Get deadlines for an application")
    public ResponseEntity<ApiResponse<List<DeadlineDto>>> getDeadlinesByApplication(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(deadlineService.getDeadlinesByApplication(applicationId)));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasAuthority('ESCALATION_MANAGE')")
    @Operation(summary = "Get all overdue deadlines")
    public ResponseEntity<ApiResponse<List<DeadlineDto>>> getOverdueDeadlines() {
        return ResponseEntity.ok(ApiResponse.success(deadlineService.getOverdueDeadlines()));
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('DEADLINE_CONFIGURE') or hasAuthority('APPLICATION_ROUTE')")
    @Operation(summary = "Resolve a deadline")
    public ResponseEntity<ApiResponse<DeadlineDto>> resolveDeadline(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Deadline resolved", deadlineService.resolveDeadline(id, userDetails.getUsername())));
    }
}
