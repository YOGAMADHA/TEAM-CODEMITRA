package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private UUID id;
    private String entityType;
    private UUID entityId;
    private UUID userId;
    private String action;
    private String performedBy;
    private String userAgent;
    private LocalDateTime performedAt;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String remarks;
    private String previousHash;
    private String currentHash;
}
