package com.aicte.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * External AI Service stub for future integration with real OCR/LLM APIs.
 * API keys must be set in environment variables - never hard-coded.
 * Activated by setting ai.service.mode=external in application properties.
 */
@Service
@ConditionalOnProperty(name = "ai.service.mode", havingValue = "external")
@Slf4j
public class ExternalAiService implements AiService {

    @Value("${AI_API_KEY:}")
    private String apiKey;

    @Value("${AI_API_ENDPOINT:https://api.example.com/v1}")
    private String apiEndpoint;

    private static final String DISCLAIMER = "AI-assisted information only. Human officer confirmation is required. " +
            "This output is not an approval, rejection, inspection result, or statutory decision.";

    @Override
    public Map<String, Object> extractDocumentData(byte[] fileContent, String contentType) {
        validateApiKey();
        log.info("[ExternalAI] OCR extraction - endpoint: {}", apiEndpoint);
        return stubResponse("OCR_EXTRACTION", "External OCR API integration pending");
    }

    @Override
    public Map<String, Object> classifyDocument(byte[] fileContent, String contentType) {
        validateApiKey();
        log.info("[ExternalAI] Document classification - endpoint: {}", apiEndpoint);
        return stubResponse("DOCUMENT_CLASSIFICATION", "External classification API integration pending");
    }

    @Override
    public Map<String, Object> checkMissingDocuments(UUID applicationId) {
        validateApiKey();
        return stubResponse("MISSING_DOCUMENT_CHECK", "External missing document check API integration pending");
    }

    @Override
    public Map<String, Object> generateApplicationSummary(UUID applicationId) {
        validateApiKey();
        return stubResponse("APPLICATION_SUMMARY", "External summary API integration pending");
    }

    @Override
    public Map<String, Object> naturalLanguageSearch(String query) {
        validateApiKey();
        return stubResponse("NL_SEARCH", "External NL search API integration pending");
    }

    @Override
    public String getModelName() {
        return "ExternalAiService-stub";
    }

    private void validateApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("AI_API_KEY environment variable is not set. " +
                    "Set ai.service.mode=mock to use mock service without API keys.");
        }
    }

    private Map<String, Object> stubResponse(String taskType, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("disclaimer", DISCLAIMER);
        result.put("modelName", getModelName());
        result.put("taskType", taskType);
        result.put("status", "STUB");
        result.put("message", message);
        result.put("requiresHumanConfirmation", true);
        return result;
    }
}
