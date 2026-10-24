package com.aicte.controller;

import com.aicte.dto.request.VerifyDocumentRequest;
import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.DocumentChecklistDto;
import com.aicte.dto.response.DocumentDto;
import com.aicte.entity.Document;
import com.aicte.security.CustomUserDetails;
import com.aicte.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "Endpoints for document upload, manual verification, checksum validation, and checklists")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('DOCUMENT_UPLOAD')")
    @Operation(summary = "Upload a document for an application")
    public ResponseEntity<ApiResponse<DocumentDto>> uploadDocument(
            @RequestParam("applicationId") UUID applicationId,
            @RequestParam(value = "requirementId", required = false) UUID requirementId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "issueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(value = "expiryDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        DocumentDto dto = documentService.uploadDocument(applicationId, requirementId, file, issueDate, expiryDate, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Document uploaded successfully", dto));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAuthority('DOCUMENT_VIEW')")
    @Operation(summary = "Download a document by ID")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        Document document = documentService.getDocumentEntity(id);
        byte[] data = documentService.downloadDocument(id, userDetails.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType() != null ? document.getContentType() : "application/octet-stream"))
                .body(data);
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAuthority('DOCUMENT_VERIFY')")
    @Operation(summary = "Manually verify or reject a document (Human-supervised)")
    public ResponseEntity<ApiResponse<DocumentDto>> verifyDocument(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyDocumentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DocumentDto dto = documentService.verifyDocument(id, request, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Document verification updated", dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENT_UPLOAD') or hasAuthority('USER_MANAGE')")
    @Operation(summary = "Soft delete a document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        documentService.softDeleteDocument(id, userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Document deleted", null));
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAuthority('DOCUMENT_VIEW')")
    @Operation(summary = "Get all documents for an application")
    public ResponseEntity<ApiResponse<List<DocumentDto>>> getDocumentsByApplication(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(documentService.getDocumentsByApplication(applicationId)));
    }

    @GetMapping("/application/{applicationId}/checklist")
    @PreAuthorize("hasAuthority('DOCUMENT_VIEW')")
    @Operation(summary = "Get document requirement checklist progress for an application")
    public ResponseEntity<ApiResponse<List<DocumentChecklistDto>>> getDocumentChecklist(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(ApiResponse.success(documentService.getDocumentChecklist(applicationId)));
    }
}
