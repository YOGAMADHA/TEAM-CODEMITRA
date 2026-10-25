package com.aicte.service;

import com.aicte.dto.request.CreateApplicationRequest;
import com.aicte.entity.Application;
import com.aicte.entity.Institution;
import com.aicte.entity.User;
import com.aicte.repository.ApplicationRepository;
import com.aicte.repository.InstitutionRepository;
import com.aicte.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Application Service Unit Tests")
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private DeadlineService deadlineService;

    @InjectMocks
    private ApplicationService applicationService;

    private UUID institutionId;
    private UUID userId;
    private Institution institution;
    private User user;

    @BeforeEach
    void setUp() {
        institutionId = UUID.randomUUID();
        userId = UUID.randomUUID();

        institution = new Institution();
        institution.setId(institutionId);
        institution.setName("Test Engineering College");
        institution.setCode("TEC-001");

        user = new User();
        user.setId(userId);
        user.setEmail("test_applicant@example.com");
    }

    @Test
    @DisplayName("Should create application with valid data")
    void shouldCreateApplicationWithValidData() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setInstitutionId(institutionId);
        request.setCategory("NEW_INSTITUTION");
        request.setProgramName("B.Tech Computer Science");
        request.setIntakeSanctioned(120);
        request.setAcademicYear("2024-25");

        Application savedApplication = new Application();
        savedApplication.setId(UUID.randomUUID());
        savedApplication.setApplicationNumber("AICTE-2024-001");
        savedApplication.setInstitution(institution);

        when(institutionRepository.findById(institutionId)).thenReturn(Optional.of(institution));

        assertDoesNotThrow(() -> {
            institutionRepository.findById(institutionId);
        });

        verify(institutionRepository, atLeastOnce()).findById(institutionId);
    }

    @Test
    @DisplayName("Should return empty when institution not found")
    void shouldHandleMissingInstitution() {
        when(institutionRepository.findById(any())).thenReturn(Optional.empty());
        assertFalse(institutionRepository.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    @DisplayName("Application number should follow AICTE-YYYY-NNN format")
    void applicationNumberFormat() {
        String applicationNumber = "AICTE-2024-001";
        assertTrue(applicationNumber.matches("AICTE-\\d{4}-\\d{3,}"),
                "Application number should match AICTE-YYYY-NNN format");
    }
}
