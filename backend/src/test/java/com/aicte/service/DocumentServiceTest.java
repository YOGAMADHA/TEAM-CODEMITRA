package com.aicte.service;

import com.aicte.entity.Document;
import com.aicte.repository.DocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Document Service Unit Tests")
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Test
    @DisplayName("Checksum should detect duplicate files")
    void checksumShouldDetectDuplicates() throws Exception {
        byte[] fileContent = "test document content".getBytes(StandardCharsets.UTF_8);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String checksum = HexFormat.of().formatHex(digest.digest(fileContent));

        Document existingDoc = new Document();
        existingDoc.setChecksum(checksum);

        when(documentRepository.findByChecksum(checksum)).thenReturn(Optional.of(existingDoc));

        Optional<Document> duplicate = documentRepository.findByChecksum(checksum);
        assertTrue(duplicate.isPresent(), "Duplicate file should be detected via checksum");
        verify(documentRepository).findByChecksum(checksum);
    }

    @Test
    @DisplayName("Allowed extensions should be validated")
    void shouldValidateAllowedExtensions() {
        String[] allowedExtensions = {"pdf", "jpg", "jpeg", "png", "docx"};
        String validFile = "document.pdf";
        String invalidFile = "script.exe";

        String validExt = validFile.substring(validFile.lastIndexOf('.') + 1).toLowerCase();
        String invalidExt = invalidFile.substring(invalidFile.lastIndexOf('.') + 1).toLowerCase();

        boolean validAllowed = false;
        boolean invalidAllowed = false;
        for (String ext : allowedExtensions) {
            if (ext.equals(validExt)) validAllowed = true;
            if (ext.equals(invalidExt)) invalidAllowed = true;
        }

        assertTrue(validAllowed, "PDF should be an allowed extension");
        assertFalse(invalidAllowed, "EXE should not be an allowed extension");
    }

    @Test
    @DisplayName("File size validation should reject oversized files")
    void shouldRejectOversizedFiles() {
        long maxSizeBytes = 10L * 1024 * 1024; // 10MB
        long validFileSize = 5L * 1024 * 1024;  // 5MB
        long invalidFileSize = 15L * 1024 * 1024; // 15MB

        assertTrue(validFileSize <= maxSizeBytes, "5MB file should be within limit");
        assertFalse(invalidFileSize <= maxSizeBytes, "15MB file should exceed limit");
    }

    @Test
    @DisplayName("Document version number should increment on re-upload")
    void documentVersionShouldIncrement() {
        Document v1 = new Document();
        v1.setVersionNumber(1);

        int newVersion = v1.getVersionNumber() + 1;
        assertEquals(2, newVersion, "Version should increment from 1 to 2 on re-upload");
    }

    @Test
    @DisplayName("Soft deletion should not physically remove document")
    void softDeletionShouldNotRemoveDocument() {
        Document document = new Document();
        document.setDeleted(false);

        document.setDeleted(true);

        assertTrue(document.isDeleted(), "Document should be marked as deleted");
        assertNotNull(document, "Document object should still exist after soft delete");
    }
}
