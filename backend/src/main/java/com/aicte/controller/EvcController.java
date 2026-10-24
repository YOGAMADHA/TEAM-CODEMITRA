package com.aicte.controller;

import com.aicte.dto.request.AssignEvcMemberRequest;
import com.aicte.dto.request.CreateEvcVisitRequest;
import com.aicte.dto.request.ProposeDateRequest;
import com.aicte.dto.request.RescheduleEvcRequest;
import com.aicte.dto.request.VerifyEvcReportRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.EvcMemberDto;
import com.aicte.dto.response.EvcVisitDto;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.EvcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evc")
@RequiredArgsConstructor
@Tag(name = "EVC Management", description = "Endpoints for Expert Visiting Committee scheduling, member assignment, and report tracking")
public class EvcController {

    private final EvcService evcService;

    @PostMapping
    @PreAuthorize("hasAuthority('EVC_SCHEDULE')")
    @Operation(summary = "Initiate an EVC visit record for an application")
    public ResponseEntity<ApiResponse<EvcVisitDto>> createEvcVisit(
            @Valid @RequestBody CreateEvcVisitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EvcVisitDto dto = evcService.createEvcVisit(request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("EVC visit initiated", dto));
    }

    @PostMapping("/{id}/assign-member")
    @PreAuthorize("hasAuthority('EVC_SCHEDULE')")
    @Operation(summary = "Assign a committee member with conflict detection")
    public ResponseEntity<ApiResponse<EvcMemberDto>> assignMember(
            @PathVariable UUID id,
            @Valid @RequestBody AssignEvcMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EvcMemberDto dto = evcService.assignMember(id, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Member assigned", dto));
    }

    @PostMapping("/{id}/propose-date")
    @PreAuthorize("hasAuthority('EVC_SCHEDULE')")
    @Operation(summary = "Propose visit date and venue")
    public ResponseEntity<ApiResponse<EvcVisitDto>> proposeDate(
            @PathVariable UUID id,
            @Valid @RequestBody ProposeDateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EvcVisitDto dto = evcService.proposeDate(id, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Visit date proposed", dto));
    }

    @PostMapping("/{id}/confirm-date")
    @PreAuthorize("hasAuthority('EVC_SCHEDULE')")
    @Operation(summary = "Confirm visit date")
    public ResponseEntity<ApiResponse<EvcVisitDto>> confirmDate(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EvcVisitDto dto = evcService.confirmDate(id, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Visit date confirmed", dto));
    }

    @PostMapping("/{id}/reschedule")
    @PreAuthorize("hasAuthority('EVC_SCHEDULE')")
    @Operation(summary = "Reschedule visit with mandatory reason")
    public ResponseEntity<ApiResponse<EvcVisitDto>> rescheduleVisit(
            @PathVariable UUID id,
            @Valid @RequestBody RescheduleEvcRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EvcVisitDto dto = evcService.rescheduleVisit(id, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Visit rescheduled", dto));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('EVC_SCHEDULE')")
    @Operation(summary = "Mark visit as completed")
    public ResponseEntity<ApiResponse<EvcVisitDto>> completeVisit(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EvcVisitDto dto = evcService.completeVisit(id, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Visit marked completed", dto));
    }

    @PostMapping(value = "/{id}/upload-report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('EVC_REPORT_UPLOAD')")
    @Operation(summary = "Upload EVC inspection report")
    public ResponseEntity<ApiResponse<EvcVisitDto>> uploadReport(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        EvcVisitDto dto = evcService.uploadReport(id, file, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Report uploaded successfully", dto));
    }

    @PostMapping("/{id}/verify-report")
    @PreAuthorize("hasAuthority('APPLICATION_ROUTE') or hasAuthority('FINAL_DECISION_RECORD')")
    @Operation(summary = "Human verification of EVC report")
    public ResponseEntity<ApiResponse<EvcVisitDto>> verifyReport(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyEvcReportRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EvcVisitDto dto = evcService.verifyReport(id, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Report verification recorded", dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get EVC visit by ID")
    public ResponseEntity<ApiResponse<EvcVisitDto>> getEvcVisit(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(evcService.getEvcVisit(id)));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "Get EVC visits for an application")
    public ResponseEntity<ApiResponse<List<EvcVisitDto>>> getEvcVisitsByApplication(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(evcService.getEvcVisitsByApplication(applicationId)));
    }
}
