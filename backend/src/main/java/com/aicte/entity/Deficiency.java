package com.aicte.entity;

import com.aicte.enums.DeficiencyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "deficiencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deficiency extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(name = "category")
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "corrective_action", columnDefinition = "TEXT")
    private String correctiveAction;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeficiencyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supporting_document_requirement_id")
    private DocumentRequirement supportingDocumentRequirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id")
    private User raisedBy;

    @Column(name = "raised_at")
    private LocalDateTime raisedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    @Version
    @Column(name = "version")
    private Long version;
}
