package com.aicte.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mock AI Service for development and demonstration purposes.
 * Returns realistic mock responses without calling any external API.
 * Safe for use in demos — no API keys required.
 */
@Service
@ConditionalOnProperty(name = "ai.service.mode", havingValue = "mock", matchIfMissing = true)
@Slf4j
public class MockAiService implements AiService {

    private static final String DISCLAIMER = "AI-assisted information only. Human officer confirmation is required. " +
            "This output is not an approval, rejection, inspection result, or statutory decision.";

    @Override
    public Map<String, Object> extractDocumentData(byte[] fileContent, String contentType) {
        log.info("[MockAI] OCR extraction requested for content type: {}", contentType);
        Map<String, Object> result = new HashMap<>();
        result.put("disclaimer", DISCLAIMER);
        result.put("modelName", getModelName());
        result.put("taskType", "OCR_EXTRACTION");
        result.put("confidence", 0.78);
        result.put("data", Map.of(
                "institutionName", "ABC Engineering College (SUGGESTED - verify manually)",
                "certificateNumber", "CERT-2024-12345 (SUGGESTED - verify manually)",
                "issueDate", LocalDate.now().minusMonths(2).toString() + " (SUGGESTED - verify manually)",
                "expiryDate", LocalDate.now().plusYears(1).toString() + " (SUGGESTED - verify manually)",
                "address", "123 College Road, Mumbai, Maharashtra (SUGGESTED - verify manually)"
        ));
        result.put("requiresHumanConfirmation", true);
        return result;
    }

    @Override
    public Map<String, Object> classifyDocument(byte[] fileContent, String contentType) {
        log.info("[MockAI] Document classification requested");
        Map<String, Object> result = new HashMap<>();
        result.put("disclaimer", DISCLAIMER);
        result.put("modelName", getModelName());
        result.put("taskType", "DOCUMENT_CLASSIFICATION");
        result.put("confidence", 0.85);
        result.put("suggestedType", "FIRE_SAFETY_CERTIFICATE");
        result.put("alternativeTypes", List.of("NOC_FIRE_SAFETY", "BUILDING_SAFETY_CERTIFICATE"));
        result.put("reasoning", "Document appears to contain fire safety certification language and relevant authority stamps. Manual verification required.");
        result.put("requiresHumanConfirmation", true);
        return result;
    }

    @Override
    public Map<String, Object> checkMissingDocuments(UUID applicationId) {
        log.info("[MockAI] Missing document check for application: {}", applicationId);
        Map<String, Object> result = new HashMap<>();
        result.put("disclaimer", DISCLAIMER);
        result.put("modelName", getModelName());
        result.put("taskType", "MISSING_DOCUMENT_CHECK");
        result.put("applicationId", applicationId.toString());
        result.put("possiblyMissing", List.of(
                Map.of("requirementName", "Fire Safety Certificate", "reason", "Not found in uploaded documents"),
                Map.of("requirementName", "Building Completion Certificate", "reason", "Not found in uploaded documents")
        ));
        result.put("note", "These are AI suggestions based on document names. Officer must verify actual document content.");
        result.put("requiresHumanConfirmation", true);
        return result;
    }

    @Override
    public Map<String, Object> generateApplicationSummary(UUID applicationId) {
        log.info("[MockAI] Application summary requested for: {}", applicationId);
        Map<String, Object> result = new HashMap<>();
        result.put("disclaimer", DISCLAIMER);
        result.put("modelName", getModelName());
        result.put("taskType", "APPLICATION_SUMMARY");
        result.put("applicationId", applicationId.toString());
        result.put("summary", Map.of(
                "overview", "Application is currently under scrutiny. 3 documents verified, 2 pending verification, 1 deficiency open.",
                "institutionDetails", "Institution details appear complete. Address and contact information present.",
                "documentStatus", "5 of 7 required documents uploaded. 3 verified, 2 under review.",
                "openDeficiencies", "1 open deficiency: Fire safety certificate missing or expired.",
                "deadlineStatus", "Scrutiny deadline is overdue by 1 day. Immediate action required.",
                "evcStatus", "Not yet scheduled.",
                "recommendation", "AI cannot make recommendations on approval or rejection. Human decision required."
        ));
        result.put("requiresHumanConfirmation", true);
        return result;
    }

    @Override
    public Map<String, Object> naturalLanguageSearch(String query) {
        log.info("[MockAI] Natural language search: {}", query);
        Map<String, Object> result = new HashMap<>();
        result.put("disclaimer", DISCLAIMER);
        result.put("modelName", getModelName());
        result.put("taskType", "NL_SEARCH");
        result.put("originalQuery", query);

        Map<String, Object> structuredFilters = new HashMap<>();
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("pending scrutiny")) {
            structuredFilters.put("stage", "SCRUTINY");
            structuredFilters.put("status", "IN_PROGRESS");
            if (lowerQuery.contains("15 days") || lowerQuery.contains("15days")) {
                structuredFilters.put("daysInStageMin", 15);
            }
        } else if (lowerQuery.contains("deficiency")) {
            structuredFilters.put("stage", "DEFICIENCY_RAISED");
        } else if (lowerQuery.contains("evc report pending")) {
            structuredFilters.put("stage", "EVC_REPORT_PENDING");
        } else if (lowerQuery.contains("overdue")) {
            structuredFilters.put("hasOverdueDeadline", true);
        } else if (lowerQuery.contains("expiring")) {
            structuredFilters.put("documentsExpiringDays", 30);
        }

        result.put("structuredFilters", structuredFilters);
        result.put("note", "Structured filters generated from natural language. Review before applying. Raw SQL is never used.");
        result.put("requiresHumanConfirmation", false);
        return result;
    }

    @Override
    public String getModelName() {
        return "MockAiService-v1.0-demo";
    }
}
