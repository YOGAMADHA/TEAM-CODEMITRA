package com.aicte.controller;

import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.AuditLogDto;
import com.aicte.dto.response.HashChainVerificationResult;
import com.aicte.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasRole('AUDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLogDto>>> getAllAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String performedBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {

        Page<AuditLogDto> logs = (entityType != null || action != null || performedBy != null || from != null || to != null)
                ? auditService.searchAuditLogs(entityType, action, performedBy, from, to, pageable)
                : auditService.getAllAuditLogs(pageable);

        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('AUDITOR') or hasRole('ADMIN') or hasRole('SCRUTINY_OFFICER')")
    public ResponseEntity<ApiResponse<List<AuditLogDto>>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return ResponseEntity.ok(ApiResponse.success(auditService.getAuditLogs(entityType, entityId)));
    }

    @GetMapping("/verify-chain")
    @PreAuthorize("hasRole('AUDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HashChainVerificationResult>> verifyHashChain() {
        return ResponseEntity.ok(ApiResponse.success(auditService.verifyHashChain()));
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('AUDITOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogDto>>> exportAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String performedBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponse.success(
                auditService.exportAuditLogs(entityType, action, performedBy, from, to)));
    }
}
