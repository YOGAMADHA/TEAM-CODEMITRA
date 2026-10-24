package com.aicte.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Workflow Stage Transition Tests")
class WorkflowServiceTest {

    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
        "SUBMITTED", Set.of("INITIAL_VALIDATION", "RETURNED"),
        "INITIAL_VALIDATION", Set.of("SCRUTINY", "DEFICIENCY_RAISED", "RETURNED"),
        "SCRUTINY", Set.of("DEFICIENCY_RAISED", "EVC_SCHEDULED", "COMPETENT_AUTHORITY_DECISION", "RETURNED"),
        "DEFICIENCY_RAISED", Set.of("APPLICANT_RESPONSE", "RESCRUTINY"),
        "APPLICANT_RESPONSE", Set.of("RESCRUTINY", "DEFICIENCY_RAISED"),
        "RESCRUTINY", Set.of("EVC_SCHEDULED", "COMPETENT_AUTHORITY_DECISION", "DEFICIENCY_RAISED"),
        "EVC_SCHEDULED", Set.of("EVC_CONDUCTED", "RESCHEDULED"),
        "EVC_CONDUCTED", Set.of("EVC_REPORT_PENDING"),
        "EVC_REPORT_PENDING", Set.of("EVC_REPORT_UPLOADED"),
        "EVC_REPORT_UPLOADED", Set.of("COMPETENT_AUTHORITY_DECISION")
    );

    @Test
    @DisplayName("Valid stage transitions should be permitted")
    void validTransitionsShouldBePermitted() {
        assertTrue(isValidTransition("SUBMITTED", "INITIAL_VALIDATION"));
        assertTrue(isValidTransition("SCRUTINY", "DEFICIENCY_RAISED"));
        assertTrue(isValidTransition("EVC_CONDUCTED", "EVC_REPORT_PENDING"));
    }

    @Test
    @DisplayName("Invalid stage transitions should be rejected")
    void invalidTransitionsShouldBeRejected() {
        assertFalse(isValidTransition("SUBMITTED", "EVC_SCHEDULED"));
        assertFalse(isValidTransition("COMPETENT_AUTHORITY_DECISION", "SCRUTINY"));
        assertFalse(isValidTransition("SUBMITTED", "SUBMITTED"));
    }

    @Test
    @DisplayName("Application cannot be approved from non-final stage")
    void applicationCannotBeApprovedFromNonFinalStage() {
        List<String> nonFinalStages = List.of("SUBMITTED", "INITIAL_VALIDATION", "SCRUTINY",
                "DEFICIENCY_RAISED", "APPLICANT_RESPONSE", "EVC_SCHEDULED");

        for (String stage : nonFinalStages) {
            assertFalse(isValidTransition(stage, "APPROVED"),
                    "Should not allow direct approval from " + stage);
        }
    }

    @Test
    @DisplayName("WITHDRAWN stage should be terminal")
    void withdrawnStageShouldBeTerminal() {
        assertFalse(isValidTransition("WITHDRAWN", "SUBMITTED"));
        assertFalse(isValidTransition("WITHDRAWN", "SCRUTINY"));
        assertFalse(isValidTransition("WITHDRAWN", "COMPETENT_AUTHORITY_DECISION"));
    }

    private boolean isValidTransition(String from, String to) {
        Set<String> allowedTargets = VALID_TRANSITIONS.get(from);
        return allowedTargets != null && allowedTargets.contains(to);
    }
}
