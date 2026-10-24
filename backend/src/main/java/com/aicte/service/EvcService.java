package com.aicte.service;

import com.aicte.dto.request.AssignEvcMemberRequest;
import com.aicte.dto.request.CreateEvcVisitRequest;
import com.aicte.dto.request.ProposeDateRequest;
import com.aicte.dto.request.RescheduleEvcRequest;
import com.aicte.dto.request.VerifyEvcReportRequest;
import com.aicte.dto.response.EvcMemberDto;
import com.aicte.dto.response.EvcVisitDto;
import com.aicte.entity.Application;
import com.aicte.entity.EvcMember;
import com.aicte.entity.EvcVisit;
import com.aicte.entity.User;
import com.aicte.enums.ApplicationStage;
import com.aicte.enums.EvcStatus;
import com.aicte.enums.NotificationType;
import com.aicte.exception.BadRequestException;
import com.aicte.exception.DuplicateResourceException;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.EvcMemberRepository;
import com.aicte.repository.EvcVisitRepository;
import com.aicte.repository.UserRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvcService {

    private final EvcVisitRepository evcVisitRepository;
    private final EvcMemberRepository evcMemberRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Transactional
    public EvcVisitDto createEvcVisit(CreateEvcVisitRequest request, UUID userId, String userEmail) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + request.getApplicationId()));

        EvcVisit visit = EvcVisit.builder()
                .application(application)
                .status(EvcStatus.EVC_REQUIRED)
                .meetingMode(request.getMeetingMode())
                .build();
        visit.setCreatedBy(userEmail);

        EvcVisit saved = evcVisitRepository.save(visit);

        application.setCurrentStage(ApplicationStage.EVC_REQUIRED);
        applicationRepository.save(application);

        auditService.log("EvcVisit", saved.getId(), "CREATE", userEmail, null, saved.getStatus().name(), null);
        return mapToDto(saved);
    }

    @Transactional
    public EvcMemberDto assignMember(UUID visitId, AssignEvcMemberRequest request, UUID assignedByUserId, String userEmail) {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        // Conflict check: is member already assigned to another EVC visit on the confirmed date?
        if (visit.getConfirmedDate() != null) {
            List<EvcMember> conflicts = evcMemberRepository.findConflictingMembers(request.getUserId(), visit.getConfirmedDate());
            if (!conflicts.isEmpty()) {
                throw new DuplicateResourceException(
                        String.format("User %s is already assigned to another EVC visit on %s", user.getEmail(), visit.getConfirmedDate())
                );
            }
        }

        EvcMember member = EvcMember.builder()
                .evcVisit(visit)
                .user(user)
                .role(request.getRole())
                .assignedAt(TimeService.now())
                .available(true)
                .build();
        member.setCreatedBy(userEmail);

        EvcMember saved = evcMemberRepository.save(member);

        visit.setStatus(EvcStatus.MEMBERS_ASSIGNED);
        evcVisitRepository.save(visit);

        auditService.log("EvcMember", saved.getId(), "ASSIGN", userEmail, null, user.getEmail(), null);
        return mapMemberToDto(saved);
    }

    @Transactional
    public EvcVisitDto proposeDate(UUID visitId, ProposeDateRequest request, UUID userId, String userEmail) {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));

        visit.setProposedDate(request.getProposedDate());
        visit.setVenue(request.getVenue());
        visit.setStatus(EvcStatus.DATE_PROPOSED);
        visit.setUpdatedBy(userEmail);

        EvcVisit saved = evcVisitRepository.save(visit);
        auditService.log("EvcVisit", saved.getId(), "PROPOSE_DATE", userEmail, null, request.getProposedDate().toString(), null);
        return mapToDto(saved);
    }

    @Transactional
    public EvcVisitDto confirmDate(UUID visitId, UUID userId, String userEmail) {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));

        if (visit.getProposedDate() == null) {
            throw new BadRequestException("Cannot confirm visit date: no proposed date set");
        }

        visit.setConfirmedDate(visit.getProposedDate());
        visit.setStatus(EvcStatus.DATE_CONFIRMED);
        visit.setUpdatedBy(userEmail);

        EvcVisit saved = evcVisitRepository.save(visit);

        Application app = visit.getApplication();
        app.setCurrentStage(ApplicationStage.EVC_SCHEDULING);
        applicationRepository.save(app);

        auditService.log("EvcVisit", saved.getId(), "CONFIRM_DATE", userEmail, null, visit.getConfirmedDate().toString(), null);

        notificationService.sendTemplatedNotification(
                NotificationType.EVC_SCHEDULED,
                Map.of(
                        "applicationNumber", app.getApplicationNumber(),
                        "date", visit.getConfirmedDate().toString(),
                        "venue", visit.getVenue() != null ? visit.getVenue() : "Virtual"
                ),
                userId,
                app.getId()
        );

        return mapToDto(saved);
    }

    @Transactional
    public EvcVisitDto rescheduleVisit(UUID visitId, RescheduleEvcRequest request, UUID userId, String userEmail) {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));

        visit.setProposedDate(request.getNewDate());
        visit.setConfirmedDate(null);
        visit.setReschedulingReason(request.getReason());
        visit.setStatus(EvcStatus.RESCHEDULED);
        visit.setUpdatedBy(userEmail);

        EvcVisit saved = evcVisitRepository.save(visit);
        auditService.log("EvcVisit", saved.getId(), "RESCHEDULE", userEmail, null, request.getReason(), null);
        return mapToDto(saved);
    }

    @Transactional
    public EvcVisitDto completeVisit(UUID visitId, UUID userId, String userEmail) {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));

        LocalDateTime now = TimeService.now();
        visit.setVisitCompletedAt(now);
        visit.setStatus(EvcStatus.VISIT_COMPLETED);
        visit.setUpdatedBy(userEmail);

        EvcVisit saved = evcVisitRepository.save(visit);

        Application app = visit.getApplication();
        app.setCurrentStage(ApplicationStage.EVC_CONDUCTED);
        applicationRepository.save(app);

        auditService.log("EvcVisit", saved.getId(), "COMPLETE", userEmail, null, "VISIT_COMPLETED", null);
        return mapToDto(saved);
    }

    @Transactional
    public EvcVisitDto uploadReport(UUID visitId, MultipartFile file, UUID userId, String userEmail) throws IOException {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        String storageKey = String.format("apps/%s/evc/%s_%s", visit.getApplication().getId(), UUID.randomUUID(), file.getOriginalFilename());
        storageService.upload(storageKey, file.getBytes(), file.getContentType());

        LocalDateTime now = TimeService.now();
        visit.setReportObjectStorageKey(storageKey);
        visit.setReportUploadedAt(now);
        visit.setReportUploadedBy(user);
        visit.setStatus(EvcStatus.REPORT_UPLOADED);
        visit.setUpdatedBy(userEmail);

        EvcVisit saved = evcVisitRepository.save(visit);

        Application app = visit.getApplication();
        app.setCurrentStage(ApplicationStage.EVC_REPORT_UPLOADED);
        applicationRepository.save(app);

        auditService.log("EvcVisit", saved.getId(), "UPLOAD_REPORT", userEmail, null, storageKey, null);
        return mapToDto(saved);
    }

    @Transactional
    public EvcVisitDto verifyReport(UUID visitId, VerifyEvcReportRequest request, UUID verifierId, String verifierEmail) {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));

        User verifier = userRepository.findById(verifierId)
                .orElseThrow(() -> new ResourceNotFoundException("Verifier not found with ID: " + verifierId));

        LocalDateTime now = TimeService.now();
        visit.setReportVerified(request.isApproved());
        visit.setReportVerifiedBy(verifier);
        visit.setReportVerifiedAt(now);
        visit.setReportRemarks(request.getRemarks());
        visit.setStatus(request.isApproved() ? EvcStatus.REPORT_VERIFIED : EvcStatus.REPORT_PENDING);
        visit.setUpdatedBy(verifierEmail);

        EvcVisit saved = evcVisitRepository.save(visit);
        auditService.log("EvcVisit", saved.getId(), "VERIFY_REPORT", verifierEmail, null, String.valueOf(request.isApproved()), null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public EvcVisitDto getEvcVisit(UUID visitId) {
        EvcVisit visit = evcVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("EVC Visit not found with ID: " + visitId));
        return mapToDto(visit);
    }

    @Transactional(readOnly = true)
    public List<EvcVisitDto> getEvcVisitsByApplication(UUID applicationId) {
        return evcVisitRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EvcVisitDto mapToDto(EvcVisit v) {
        List<EvcMemberDto> members = evcMemberRepository.findByEvcVisitId(v.getId())
                .stream()
                .map(this::mapMemberToDto)
                .collect(Collectors.toList());

        return EvcVisitDto.builder()
                .id(v.getId())
                .applicationId(v.getApplication() != null ? v.getApplication().getId() : null)
                .applicationNumber(v.getApplication() != null ? v.getApplication().getApplicationNumber() : null)
                .status(v.getStatus())
                .meetingMode(v.getMeetingMode())
                .proposedDate(v.getProposedDate())
                .confirmedDate(v.getConfirmedDate())
                .venue(v.getVenue())
                .reschedulingReason(v.getReschedulingReason())
                .visitCompletedAt(v.getVisitCompletedAt())
                .reportObjectStorageKey(v.getReportObjectStorageKey())
                .reportUploadedAt(v.getReportUploadedAt())
                .reportUploadedByName(v.getReportUploadedBy() != null ? v.getReportUploadedBy().getFirstName() + " " + v.getReportUploadedBy().getLastName() : null)
                .reportVerified(v.isReportVerified())
                .reportVerifiedByName(v.getReportVerifiedBy() != null ? v.getReportVerifiedBy().getFirstName() + " " + v.getReportVerifiedBy().getLastName() : null)
                .reportVerifiedAt(v.getReportVerifiedAt())
                .reportRemarks(v.getReportRemarks())
                .historyNotes(v.getHistoryNotes())
                .members(members)
                .build();
    }

    public EvcMemberDto mapMemberToDto(EvcMember m) {
        return EvcMemberDto.builder()
                .id(m.getId())
                .userId(m.getUser() != null ? m.getUser().getId() : null)
                .userName(m.getUser() != null ? m.getUser().getFirstName() + " " + m.getUser().getLastName() : null)
                .userEmail(m.getUser() != null ? m.getUser().getEmail() : null)
                .role(m.getRole())
                .assignedAt(m.getAssignedAt())
                .available(m.getAvailable())
                .build();
    }
}
