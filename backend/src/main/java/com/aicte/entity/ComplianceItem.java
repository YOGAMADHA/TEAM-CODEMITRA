package com.aicte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(name = "category")
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "responsible_party")
    private String responsibleParty;

    @Column(name = "required_document_type")
    private String requiredDocumentType;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Builder.Default
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id")
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Builder.Default
    @Column(name = "renewal_required", nullable = false)
    private boolean renewalRequired = false;

    @Column(name = "renewal_date")
    private LocalDate renewalDate;

    @Column(name = "escalation_level")
    private String escalationLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_document_id")
    private Document linkedDocument;

    @Builder.Default
    @Column(name = "status")
    private String status = "PENDING";

    @Version
    @Column(name = "version")
    private Long version;
}
