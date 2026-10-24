package com.aicte.dto.response;

import com.aicte.enums.EvcStatus;
import com.aicte.enums.MeetingMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvcVisitDto {
    private UUID id;
    private UUID applicationId;
    private String applicationNumber;
    private EvcStatus status;
    private MeetingMode meetingMode;
    private LocalDateTime proposedDate;
    private LocalDateTime confirmedDate;
    private String venue;
    private String reschedulingReason;
    private LocalDateTime visitCompletedAt;
    private String reportObjectStorageKey;
    private LocalDateTime reportUploadedAt;
    private String reportUploadedByName;
    private boolean reportVerified;
    private String reportVerifiedByName;
    private LocalDateTime reportVerifiedAt;
    private String reportRemarks;
    private String historyNotes;
    private List<EvcMemberDto> members;
}
