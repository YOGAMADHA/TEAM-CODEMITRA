package com.aicte.controller;

import com.aicte.dto.response.ApiResponse;
import com.aicte.entity.AiAssistance;
import com.aicte.repository.AiAssistanceRepository;
import com.aicte.service.AuditService;
import com.aicte.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiService aiService;
    private final AiAssistanceRepository aiAssistanceRepository;
    private final AuditService auditService;

    private static final String DISCLAIMER = "AI-assisted information only. Human officer confirmation is required. " +
            "This output is not an approval, rejection, inspection result, or statutory decision.";

    @PostMapping("/ocr/{documentId}")
    @PreAuthorize("hasRole('SCRUTINY_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> extractDocumentData(
            @PathVariable UUID documentId,
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {

        Map<String, Object> result = aiService.extractDocumentData(file.getBytes(), file.getContentType());
        saveAiRecord(documentId, null, "OCR_EXTRACTION", result, auth != null ? auth.getName() : "system");
        auditService.log("AI_ASSISTANCE", documentId, "AI_OCR_REQUESTED", auth != null ? auth.getName() : "system", null, "OCR extraction requested", null);
        result.put("disclaimer", DISCLAIMER);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/classify")
    @PreAuthorize("hasRole('SCRUTINY_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> classifyDocument(
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {

        Map<String, Object> result = aiService.classifyDocument(file.getBytes(), file.getContentType());
        saveAiRecord(null, null, "DOCUMENT_CLASSIFICATION", result, auth != null ? auth.getName() : "system");
        result.put("disclaimer", DISCLAIMER);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/missing-documents/{applicationId}")
    @PreAuthorize("hasRole('SCRUTINY_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkMissingDocuments(
            @PathVariable UUID applicationId, Authentication auth) {

        Map<String, Object> result = aiService.checkMissingDocuments(applicationId);
        saveAiRecord(null, applicationId, "MISSING_DOCUMENT_CHECK", result, auth != null ? auth.getName() : "system");
        result.put("disclaimer", DISCLAIMER);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/summary/{applicationId}")
    @PreAuthorize("hasRole('SCRUTINY_OFFICER') or hasRole('ADMIN') or hasRole('DIRECTOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationSummary(
            @PathVariable UUID applicationId, Authentication auth) {

        Map<String, Object> result = aiService.generateApplicationSummary(applicationId);
        saveAiRecord(null, applicationId, "APPLICATION_SUMMARY", result, auth != null ? auth.getName() : "system");
        result.put("disclaimer", DISCLAIMER);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> naturalLanguageSearch(
            @RequestParam String query, Authentication auth) {

        Map<String, Object> result = aiService.naturalLanguageSearch(query);
        result.put("disclaimer", DISCLAIMER);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/confirm/{aiAssistanceId}")
    @PreAuthorize("hasRole('SCRUTINY_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> confirmAiOutput(
            @PathVariable UUID aiAssistanceId,
            @RequestParam(required = false) String remarks,
            Authentication auth) {

        AiAssistance assistance = aiAssistanceRepository.findById(aiAssistanceId)
                .orElseThrow(() -> new IllegalArgumentException("AI assistance record not found"));

        assistance.setHumanConfirmed(true);
        assistance.setHumanConfirmationRemarks(remarks);
        assistance.setHumanConfirmedAt(LocalDateTime.now());
        assistance.setHumanConfirmedBy(auth != null ? auth.getName() : "system");
        aiAssistanceRepository.save(assistance);

        auditService.log("AI_ASSISTANCE", aiAssistanceId, "AI_OUTPUT_CONFIRMED", auth != null ? auth.getName() : "system", null, "AI output confirmed by human officer", null);
        return ResponseEntity.ok(ApiResponse.success("AI output confirmed by human officer"));
    }

    private void saveAiRecord(UUID documentId, UUID applicationId, String taskType,
                              Map<String, Object> result, String requestedBy) {
        try {
            AiAssistance record = AiAssistance.builder()
                    .documentId(documentId)
                    .applicationId(applicationId)
                    .requestedBy(requestedBy)
                    .taskType(taskType)
                    .modelName(aiService.getModelName())
                    .output(result.toString())
                    .confidence(result.containsKey("confidence") ? (Double) result.get("confidence") : null)
                    .humanConfirmed(false)
                    .build();
            aiAssistanceRepository.save(record);
        } catch (Exception e) {
            log.warn("Failed to save AI assistance record: {}", e.getMessage());
        }
    }
}
