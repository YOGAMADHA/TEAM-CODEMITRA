package com.aicte.controller;

import com.aicte.dto.request.CreateInstitutionRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.InstitutionDto;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.InstitutionService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
@Tag(name = "Institution Management", description = "Endpoints for institutional registration, profile, and verification")
public class InstitutionController {

    private final InstitutionService institutionService;

    @PostMapping
    @PreAuthorize("hasAuthority('APPLICATION_CREATE') or hasAuthority('USER_MANAGE')")
    @Operation(summary = "Register or create a new institution")
    public ResponseEntity<ApiResponse<InstitutionDto>> createInstitution(
            @Valid @RequestBody CreateInstitutionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        InstitutionDto dto = institutionService.createInstitution(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Institution created", dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get institution details by ID")
    public ResponseEntity<ApiResponse<InstitutionDto>> getInstitutionById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(institutionService.getInstitutionById(id)));
    }

    @GetMapping
    @Operation(summary = "List all institutions with pagination")
    public ResponseEntity<ApiResponse<Page<InstitutionDto>>> getAllInstitutions(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(institutionService.getAllInstitutions(pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasAuthority('APPLICATION_CREATE')")
    @Operation(summary = "Update institution profile")
    public ResponseEntity<ApiResponse<InstitutionDto>> updateInstitution(
            @PathVariable UUID id,
            @Valid @RequestBody CreateInstitutionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Institution updated",
                institutionService.updateInstitution(id, request, userDetails.getUsername())));
    }
}
