package com.aicte.service;

import com.aicte.dto.response.NotificationDto;
import com.aicte.entity.Notification;
import com.aicte.entity.NotificationTemplate;
import com.aicte.entity.User;
import com.aicte.enums.NotificationType;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.NotificationRepository;
import com.aicte.repository.NotificationTemplateRepository;
import com.aicte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        log.info("Saved notification for recipient: {}, type: {}", notification.getRecipientId(), notification.getType());

        try {
            User user = userRepository.findById(notification.getRecipientId()).orElse(null);
            if (user != null && user.getEmail() != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("AICTE System Notification: " + notification.getType());
                message.setText(notification.getPayload() != null ? notification.getPayload() : "");
                mailSender.send(message);
                notification.setSentViaEmail(true);
                notificationRepository.save(notification);
                log.info("Sent email notification to: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Could not dispatch email notification (mail server may be offline): {}", e.getMessage());
        }
    }

    @Transactional
    public void sendTemplatedNotification(NotificationType type, Map<String, String> variables, UUID recipientId, UUID applicationId) {
        String subject = "AICTE Notification: " + type.name();
        String body = "Notification for application " + applicationId;

        var templateOpt = templateRepository.findByType(type.name());
        if (templateOpt.isPresent()) {
            NotificationTemplate template = templateOpt.get();
            subject = template.getSubjectTemplate();
            body = template.getBodyTemplate();

            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String val = entry.getValue() != null ? entry.getValue() : "";
                if (subject != null) subject = subject.replace(placeholder, val);
                if (body != null) body = body.replace(placeholder, val);
            }
        }

        Notification notification = Notification.builder()
                .type(type.name())
                .applicationId(applicationId)
                .recipientId(recipientId)
                .payload(body)
                .sentViaEmail(false)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        sendNotification(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(UUID recipientId) {
        return notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications(UUID recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID recipientId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));
        if (notification.getRecipientId().equals(recipientId)) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllAsRead(UUID recipientId) {
        List<Notification> unread = notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(recipientId);
        LocalDateTime now = LocalDateTime.now();
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(now);
        });
        notificationRepository.saveAll(unread);
    }

    private NotificationDto mapToDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType())
                .applicationId(n.getApplicationId())
                .recipientId(n.getRecipientId())
                .payload(n.getPayload())
                .read(n.isRead())
                .readAt(n.getReadAt())
                .sentViaEmail(n.isSentViaEmail())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
