package com.aicte.service.ai;

import java.util.Map;
import java.util.UUID;

/**
 * AI Service interface for assistive features.
 * All implementations must follow these rules:
 * 1. AI must NEVER automatically verify, approve, or reject documents.
 * 2. All outputs require human officer confirmation.
 * 3. Confidence scores must be provided where available.
 * 4. All AI activity must be logged via AuditService.
 */
public interface AiService {

    /**
     * Extract document metadata using OCR assistance.
     * Returns a map with keys: institutionName, certificateNumber, issueDate, expiryDate, address
     * IMPORTANT: This is OCR assistance only. Human verification is required.
     */
    Map<String, Object> extractDocumentData(byte[] fileContent, String contentType);

    /**
     * Suggest document type based on content analysis.
     * IMPORTANT: Never auto-classifies or auto-verifies. Suggestion only.
     */
    Map<String, Object> classifyDocument(byte[] fileContent, String contentType);

    /**
     * Compare uploaded documents with configured requirements.
     * Returns list of potentially missing documents.
     * IMPORTANT: Officer must confirm before any action is taken.
     */
    Map<String, Object> checkMissingDocuments(UUID applicationId);

    /**
     * Generate a structured summary of application status.
     * Summarises institution details, documents, deficiencies, deadlines, EVC status.
     */
    Map<String, Object> generateApplicationSummary(UUID applicationId);

    /**
     * Translate natural language query to structured search parameters.
     * IMPORTANT: Returns structured filters only. NEVER generates raw SQL.
     */
    Map<String, Object> naturalLanguageSearch(String query);

    /**
     * Returns the model name identifier used by this implementation.
     */
    String getModelName();
}
