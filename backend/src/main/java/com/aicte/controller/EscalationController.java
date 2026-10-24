package com.aicte.controller;

import com.aicte.dto.request.CreateEscalationRequest;
import com.aicte.dto.request.ResolveEscalationRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.EscalationDto;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.EscalationService;
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
@RequestMapping("/api/escalations")
@RequiredArgsConstructor
@Tag(name = "Escalations", description = "Endpoints for overdue application escalations and hierarchical alerts")
public class EscalationController {

    private final EscalationService escalationService;

    @PostMapping
    @PreAuthorize("hasAuthority('ESCALATION_MANAGE')")
    @Operation(summary = "Raise a manual escalation")
    public ResponseEntity<ApiResponse<EscalationDto>> createEscalation(
            @Valid @RequestBody CreateEscalationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EscalationDto dto = escalationService.createEscalation(request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Escalation created", dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ESCALATION_MANAGE') or hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get all active escalations")
    public ResponseEntity<ApiResponse<List<EscalationDto>>> getActiveEscalations() {
        return ResponseEntity.ok(ApiResponse.success(escalationService.getActiveEscalations()));
    }

    @GetMapping("/my")
    @Operation(summary = "Get escalations assigned to the current user")
    public ResponseEntity<ApiResponse<List<EscalationDto>>> getMyEscalations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(escalationService.getMyEscalations(userDetails.getId())));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "Get escalations for an application")
    public ResponseEntity<ApiResponse<List<EscalationDto>>> getEscalationsByApplication(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(escalationService.getEscalationsByApplication(applicationId)));
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('ESCALATION_MANAGE')")
    @Operation(summary = "Resolve an escalation with remarks")
    public ResponseEntity<ApiResponse<EscalationDto>> resolveEscalation(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveEscalationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EscalationDto dto = escalationService.resolveEscalation(id, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Escalation resolved", dto));
    }
}
