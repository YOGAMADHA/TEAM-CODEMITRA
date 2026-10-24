package com.aicte.service;

import com.aicte.dto.response.AuditLogDto;
import com.aicte.dto.response.HashChainVerificationResult;
import com.aicte.entity.AuditLog;
import com.aicte.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String entityType, UUID entityId, String action, String performedBy,
                    String oldValue, String newValue, String ipAddress) {
        log(entityType, entityId, null, action, performedBy, null, null, oldValue, newValue, ipAddress);
    }

    @Transactional
    public void log(String entityType, UUID entityId, UUID userId, String action, String performedBy,
                    String userAgent, String remarks, String oldValue, String newValue, String ipAddress) {

        String previousHash = auditLogRepository.findTopByOrderByPerformedAtDesc()
                .map(AuditLog::getCurrentHash)
                .orElse("GENESIS");

        LocalDateTime now = LocalDateTime.now();
        String rawData = (previousHash == null ? "GENESIS" : previousHash)
                + "|" + (entityType != null ? entityType : "")
                + "|" + (entityId != null ? entityId.toString() : "")
                + "|" + (userId != null ? userId.toString() : "")
                + "|" + (action != null ? action : "")
                + "|" + (performedBy != null ? performedBy : "")
                + "|" + now.toString();

        String currentHash = computeSha256(rawData);

        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .action(action)
                .performedBy(performedBy)
                .userAgent(userAgent)
                .performedAt(now)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(ipAddress)
                .remarks(remarks)
                .previousHash(previousHash)
                .currentHash(currentHash)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log recorded: {} {} by {} | hash={}", action, entityType, performedBy,
                currentHash != null ? currentHash.substring(0, 8) + "..." : "null");
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLogs(String entityType, UUID entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByPerformedAtDesc(entityType, entityId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByPerformedAtDesc(pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> searchAuditLogs(String entityType, String action, String performedBy,
                                              LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(entityType, action, performedBy, from, to, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public HashChainVerificationResult verifyHashChain() {
        List<AuditLog> allLogs = auditLogRepository.findAllByOrderByPerformedAtAsc();
        List<String> brokenLinks = new ArrayList<>();
        int verified = 0;

        String expectedPreviousHash = "GENESIS";

        for (AuditLog logItem : allLogs) {
            String actualPreviousHash = logItem.getPreviousHash();
            if (!expectedPreviousHash.equals(actualPreviousHash)) {
                brokenLinks.add("Log ID " + logItem.getId() + " has broken chain: expected previousHash="
                        + expectedPreviousHash + " but found=" + actualPreviousHash);
            } else {
                String rawData = (logItem.getPreviousHash() != null ? logItem.getPreviousHash() : "GENESIS")
                        + "|" + (logItem.getEntityType() != null ? logItem.getEntityType() : "")
                        + "|" + (logItem.getEntityId() != null ? logItem.getEntityId().toString() : "")
                        + "|" + (logItem.getUserId() != null ? logItem.getUserId().toString() : "")
                        + "|" + (logItem.getAction() != null ? logItem.getAction() : "")
                        + "|" + (logItem.getPerformedBy() != null ? logItem.getPerformedBy() : "")
                        + "|" + logItem.getPerformedAt().toString();
                String recomputed = computeSha256(rawData);
                if (!recomputed.equals(logItem.getCurrentHash())) {
                    brokenLinks.add("Log ID " + logItem.getId() + " has tampered data: hash mismatch");
                } else {
                    verified++;
                }
            }
            expectedPreviousHash = logItem.getCurrentHash();
        }

        return HashChainVerificationResult.builder()
                .totalRecords(allLogs.size())
                .verifiedRecords(verified)
                .brokenLinks(brokenLinks)
                .chainIntact(brokenLinks.isEmpty())
                .verifiedAt(LocalDateTime.now())
                .build();
    }

    public List<AuditLogDto> exportAuditLogs(String entityType, String action, String performedBy,
                                              LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.searchAuditLogsUnpaged(entityType, action, performedBy, from, to)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private String computeSha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 not available", e);
            return "ERROR_HASH_UNAVAILABLE";
        }
    }

    private AuditLogDto mapToDto(AuditLog logItem) {
        return AuditLogDto.builder()
                .id(logItem.getId())
                .entityType(logItem.getEntityType())
                .entityId(logItem.getEntityId())
                .userId(logItem.getUserId())
                .action(logItem.getAction())
                .performedBy(logItem.getPerformedBy())
                .userAgent(logItem.getUserAgent())
                .performedAt(logItem.getPerformedAt())
                .oldValue(logItem.getOldValue())
                .newValue(logItem.getNewValue())
                .ipAddress(logItem.getIpAddress())
                .remarks(logItem.getRemarks())
                .previousHash(logItem.getPreviousHash())
                .currentHash(logItem.getCurrentHash())
                .build();
    }
}
