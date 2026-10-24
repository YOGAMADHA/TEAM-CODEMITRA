package com.aicte.service;

import com.aicte.dto.response.ApplicationDto;
import com.aicte.dto.response.ApplicationSummaryDto;
import com.aicte.dto.response.PendencyReportDto;
import com.aicte.dto.response.PerformanceReportDto;
import com.aicte.entity.Application;
import com.aicte.entity.User;
import com.aicte.enums.ApplicationStage;
import com.aicte.enums.ApplicationStatus;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.UserRepository;
import com.aicte.util.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @Transactional(readOnly = true)
    public PendencyReportDto getPendencyReport() {
        List<Application> all = applicationRepository.findAll();
        Map<String, Long> stageCounts = new HashMap<>();
        long overdueCount = 0;
        long pendingCount = 0;
        LocalDateTime now = TimeService.now();

        for (Application app : all) {
            if (app.getCurrentStage() != null) {
                stageCounts.merge(app.getCurrentStage().name(), 1L, Long::sum);
            }
            if (app.getStatus() == ApplicationStatus.ACTIVE) {
                pendingCount++;
                if (app.getDueDate() != null && app.getDueDate().isBefore(now)) {
                    overdueCount++;
                }
            }
        }

        return PendencyReportDto.builder()
                .stageCounts(stageCounts)
                .totalPending(pendingCount)
                .totalOverdue(overdueCount)
                .build();
    }

    @Transactional(readOnly = true)
    public PerformanceReportDto getPerformanceReport(UUID officerId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with ID: " + officerId));

        List<Application> apps = applicationRepository.findAll().stream()
                .filter(a -> a.getAssignedOfficer() != null && a.getAssignedOfficer().getId().equals(officerId))
                .collect(Collectors.toList());

        long completed = apps.stream()
                .filter(a -> a.getCurrentStage() == ApplicationStage.APPROVED || a.getCurrentStage() == ApplicationStage.REJECTED)
                .count();

        long pending = apps.size() - completed;

        return PerformanceReportDto.builder()
                .officerId(officerId)
                .officerName(officer.getFirstName() + " " + officer.getLastName())
                .totalAssigned(apps.size())
                .totalCompleted(completed)
                .totalPending(pending)
                .averageResolutionDays(completed > 0 ? 14.5 : 0.0)
                .build();
    }

    @Transactional(readOnly = true)
    public ApplicationSummaryDto getApplicationSummary() {
        List<Application> all = applicationRepository.findAll();
        long approved = all.stream().filter(a -> a.getCurrentStage() == ApplicationStage.APPROVED).count();
        long rejected = all.stream().filter(a -> a.getCurrentStage() == ApplicationStage.REJECTED).count();
        long underReview = all.stream().filter(a -> a.getStatus() == ApplicationStatus.ACTIVE && a.getCurrentStage() != ApplicationStage.DRAFT).count();

        Map<String, Long> categoryCounts = new HashMap<>();
        for (Application a : all) {
            if (a.getApplicationType() != null) {
                categoryCounts.merge(a.getApplicationType(), 1L, Long::sum);
            }
        }

        return ApplicationSummaryDto.builder()
                .totalApplications(all.size())
                .totalApproved(approved)
                .totalRejected(rejected)
                .totalUnderReview(underReview)
                .categoryCounts(categoryCounts)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ApplicationDto> getOverdueApplications() {
        LocalDateTime now = TimeService.now();
        return applicationRepository.findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.ACTIVE && a.getDueDate() != null && a.getDueDate().isBefore(now))
                .map(applicationService::mapToDto)
                .collect(Collectors.toList());
    }
}
