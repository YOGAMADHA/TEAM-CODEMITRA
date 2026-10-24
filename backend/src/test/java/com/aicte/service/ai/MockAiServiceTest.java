package com.aicte.service.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mock AI Service Tests")
class MockAiServiceTest {

    private MockAiService mockAiService;

    private static final String EXPECTED_DISCLAIMER = "AI-assisted information only. Human officer confirmation is required. " +
            "This output is not an approval, rejection, inspection result, or statutory decision.";

    @BeforeEach
    void setUp() {
        mockAiService = new MockAiService();
    }

    @Test
    @DisplayName("OCR extraction must include disclaimer")
    void ocrExtractionMustIncludeDisclaimer() {
        byte[] content = "test".getBytes();
        Map<String, Object> result = mockAiService.extractDocumentData(content, "application/pdf");
        assertNotNull(result.get("disclaimer"), "AI response must include disclaimer");
        assertEquals(EXPECTED_DISCLAIMER, result.get("disclaimer"));
        assertTrue((boolean) result.get("requiresHumanConfirmation"),
                "OCR result must require human confirmation");
    }

    @Test
    @DisplayName("Document classification must include disclaimer and require human confirmation")
    void classificationMustRequireHumanConfirmation() {
        byte[] content = "test".getBytes();
        Map<String, Object> result = mockAiService.classifyDocument(content, "image/jpeg");
        assertNotNull(result.get("disclaimer"));
        assertTrue((boolean) result.get("requiresHumanConfirmation"));
        assertNotNull(result.get("suggestedType"), "Must suggest a document type");
    }

    @Test
    @DisplayName("AI must never return approval or rejection decision")
    void aiMustNeverReturnDecision() {
        UUID appId = UUID.randomUUID();
        Map<String, Object> summary = mockAiService.generateApplicationSummary(appId);
        Map<?, ?> summaryData = (Map<?, ?>) summary.get("summary");
        
        assertNotNull(summaryData);
        String recommendation = (String) summaryData.get("recommendation");
        assertNotNull(recommendation);
        assertTrue(recommendation.toLowerCase().contains("cannot") ||
                   recommendation.toLowerCase().contains("not") ||
                   recommendation.toLowerCase().contains("human"),
                "AI must not provide approval/rejection recommendation");
    }

    @Test
    @DisplayName("Natural language search must return structured filters, not raw SQL")
    void nlSearchMustReturnStructuredFilters() {
        Map<String, Object> result = mockAiService.naturalLanguageSearch(
                "Show applications pending scrutiny for more than 15 days");
        
        assertNotNull(result.get("structuredFilters"), "Must return structured filters");
        assertFalse(result.containsKey("sql"), "Must NEVER return raw SQL");
        assertFalse(result.containsKey("rawQuery"), "Must NEVER return raw query");
        
        Map<?, ?> filters = (Map<?, ?>) result.get("structuredFilters");
        assertNotNull(filters);
    }

    @Test
    @DisplayName("Model name must be identifiable")
    void modelNameMustBeIdentifiable() {
        String modelName = mockAiService.getModelName();
        assertNotNull(modelName);
        assertFalse(modelName.isBlank());
        assertTrue(modelName.contains("Mock") || modelName.contains("demo"),
                "Mock AI service name should identify itself as mock/demo");
    }

    @Test
    @DisplayName("Missing document check must require human confirmation")
    void missingDocumentCheckMustRequireHumanConfirmation() {
        Map<String, Object> result = mockAiService.checkMissingDocuments(UUID.randomUUID());
        assertTrue((boolean) result.get("requiresHumanConfirmation"),
                "Missing document check must require human confirmation");
        assertNotNull(result.get("possiblyMissing"), "Must return list of possibly missing documents");
    }
}
