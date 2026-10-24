package com.aicte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequirement extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "application_category")
    private String applicationCategory;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory;

    @Column(name = "allowed_file_types")
    private String allowedFileTypes;

    @Column(name = "max_file_size_mb")
    private Integer maxFileSizeMb;

    @Column(name = "requires_expiry_date", nullable = false)
    private boolean requiresExpiryDate;

    @Column(name = "requires_human_verification", nullable = false)
    private boolean requiresHumanVerification;

    @Column(name = "reminder_config")
    private String reminderConfig;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private boolean active = true;
}
