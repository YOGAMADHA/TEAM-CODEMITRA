package com.aicte.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_assistance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAssistance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "application_id")
    private UUID applicationId;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "task_type", nullable = false)
    private String taskType;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "human_confirmed")
    private Boolean humanConfirmed;

    @Column(name = "human_confirmation_remarks", columnDefinition = "TEXT")
    private String humanConfirmationRemarks;

    @Column(name = "human_confirmed_at")
    private LocalDateTime humanConfirmedAt;

    @Column(name = "human_confirmed_by")
    private String humanConfirmedBy;

    @PrePersist
    protected void onCreate() {
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
        if (this.humanConfirmed == null) {
            this.humanConfirmed = false;
        }
    }
}
