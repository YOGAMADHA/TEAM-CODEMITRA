package com.aicte.controller;

import com.aicte.dto.request.CreateDeficiencyRequest;
import com.aicte.dto.request.CreateDeficiencyResponseRequest;
import com.aicte.dto.request.ReviewDeficiencyRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.DeficiencyDto;
import com.aicte.dto.response.DeficiencyResponseDto;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.DeficiencyService;
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
@RequestMapping("/api/deficiencies")
@RequiredArgsConstructor
@Tag(name = "Deficiencies", description = "Endpoints for raising deficiencies, submitting applicant responses, and officer reviews")
public class DeficiencyController {

    private final DeficiencyService deficiencyService;

    @PostMapping
    @PreAuthorize("hasAuthority('DEFICIENCY_CREATE')")
    @Operation(summary = "Raise a deficiency on an application")
    public ResponseEntity<ApiResponse<DeficiencyDto>> raiseDeficiency(
            @Valid @RequestBody CreateDeficiencyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeficiencyDto dto = deficiencyService.raiseDeficiency(request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Deficiency raised successfully", dto));
    }

    @PostMapping("/{id}/respond")
    @PreAuthorize("hasAuthority('DEFICIENCY_RESPOND')")
    @Operation(summary = "Submit an applicant response to a deficiency")
    public ResponseEntity<ApiResponse<DeficiencyResponseDto>> respondToDeficiency(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDeficiencyResponseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeficiencyResponseDto dto = deficiencyService.respondToDeficiency(id, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Response submitted", dto));
    }

    @PostMapping("/response/{responseId}/review")
    @PreAuthorize("hasAuthority('DEFICIENCY_CREATE') or hasAuthority('RESCRUTINY_PERFORM')")
    @Operation(summary = "Officer review of deficiency response (Accept/Reject)")
    public ResponseEntity<ApiResponse<DeficiencyResponseDto>> reviewResponse(
            @PathVariable UUID responseId,
            @Valid @RequestBody ReviewDeficiencyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeficiencyResponseDto dto = deficiencyService.reviewResponse(responseId, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Review recorded", dto));
    }

    @PostMapping("/{id}/reopen")
    @PreAuthorize("hasAuthority('DEFICIENCY_CREATE')")
    @Operation(summary = "Reopen a deficiency")
    public ResponseEntity<ApiResponse<DeficiencyDto>> reopenDeficiency(
            @PathVariable UUID id,
            @RequestParam("reason") String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DeficiencyDto dto = deficiencyService.reopenDeficiency(id, reason, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Deficiency reopened", dto));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "Get all deficiencies for an application")
    public ResponseEntity<ApiResponse<List<DeficiencyDto>>> getDeficienciesByApplication(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(deficiencyService.getDeficienciesByApplication(applicationId)));
    }
}
