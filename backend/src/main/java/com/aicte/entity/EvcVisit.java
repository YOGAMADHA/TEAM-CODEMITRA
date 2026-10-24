package com.aicte.entity;

import com.aicte.enums.EvcStatus;
import com.aicte.enums.MeetingMode;
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
@Table(name = "evc_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvcVisit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EvcStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_mode")
    private MeetingMode meetingMode;

    @Column(name = "proposed_date")
    private LocalDateTime proposedDate;

    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDate;

    @Column(name = "venue")
    private String venue;

    @Column(name = "rescheduling_reason")
    private String reschedulingReason;

    @Column(name = "visit_completed_at")
    private LocalDateTime visitCompletedAt;

    @Column(name = "report_object_storage_key")
    private String reportObjectStorageKey;

    @Column(name = "report_uploaded_at")
    private LocalDateTime reportUploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_uploaded_by_id")
    private User reportUploadedBy;

    @Builder.Default
    @Column(name = "report_verified", nullable = false)
    private boolean reportVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_verified_by_id")
    private User reportVerifiedBy;

    @Column(name = "report_verified_at")
    private LocalDateTime reportVerifiedAt;

    @Column(name = "report_remarks")
    private String reportRemarks;

    @Column(name = "history_notes", columnDefinition = "TEXT")
    private String historyNotes;

    @Version
    @Column(name = "version")
    private Long version;
}
