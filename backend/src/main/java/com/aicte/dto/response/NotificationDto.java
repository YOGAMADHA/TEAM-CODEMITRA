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
public class NotificationDto {
    private UUID id;
    private String type;
    private UUID applicationId;
    private UUID recipientId;
    private String payload;
    private boolean read;
    private LocalDateTime readAt;
    private boolean sentViaEmail;
    private LocalDateTime createdAt;
}
