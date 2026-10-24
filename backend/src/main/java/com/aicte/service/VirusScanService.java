package com.aicte.service;

/**
 * Virus scan abstraction interface.
 * Current implementation: NoOpVirusScanService (always passes, logs warning).
 * Production: Implement with ClamAV or enterprise antivirus solution.
 */
public interface VirusScanService {
    /**
     * Scan file content for viruses/malware.
     * @param content File content as byte array
     * @param fileName Original file name
     * @return ScanResult indicating CLEAN, INFECTED, or SCAN_FAILED
     */
    ScanResult scan(byte[] content, String fileName);

    enum ScanResult {
        CLEAN,
        INFECTED,
        SCAN_FAILED
    }
}
