package com.aicte.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * No-operation virus scanner for development and demonstration.
 * WARNING: This implementation ALWAYS returns CLEAN and does NOT scan files.
 * Replace with ClamAV integration (e.g., clamav-client library) in production.
 */
@Service
@Slf4j
public class NoOpVirusScanService implements VirusScanService {

    @Override
    public ScanResult scan(byte[] content, String fileName) {
        log.warn("[VirusScan] NoOp scanner used for file: {}. " +
                "This is a development placeholder. " +
                "Integrate ClamAV or enterprise antivirus before production deployment.", fileName);
        return ScanResult.CLEAN;
    }
}
