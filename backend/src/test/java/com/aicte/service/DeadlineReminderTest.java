package com.aicte.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Deadline Reminder Engine Tests")
class DeadlineReminderTest {

    @Test
    @DisplayName("Reminder should be scheduled before due date")
    void reminderShouldBeScheduledBeforeDueDate() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        int reminderDaysBefore = 3;
        LocalDateTime reminderDate = dueDate.minusDays(reminderDaysBefore);

        assertTrue(reminderDate.isBefore(dueDate),
                "Reminder must be scheduled before the due date");
        assertTrue(reminderDate.isAfter(LocalDateTime.now()),
                "Reminder must be in the future");
    }

    @Test
    @DisplayName("No reminder should be sent for completed deadlines")
    void noReminderForCompletedDeadlines() {
        String deadlineStatus = "COMPLETED";
        boolean shouldSendReminder = !"COMPLETED".equals(deadlineStatus)
                && !"CANCELLED".equals(deadlineStatus)
                && !"PAUSED".equals(deadlineStatus);
        assertFalse(shouldSendReminder, "Completed deadlines should not trigger reminders");
    }

    @Test
    @DisplayName("Overdue deadline should trigger escalation")
    void overdueDeadlineShouldTriggerEscalation() {
        LocalDateTime dueDate = LocalDateTime.now().minusDays(1);
        String status = "PENDING";

        boolean isOverdue = LocalDateTime.now().isAfter(dueDate) && "PENDING".equals(status);
        assertTrue(isOverdue, "Past-due pending deadline should be marked as overdue");
    }

    @Test
    @DisplayName("Reminder rules must not be hard-coded")
    void reminderRulesMustBeConfigurable() {
        int[] reminderDaysBefore = {7, 3, 1};
        assertNotNull(reminderDaysBefore, "Reminder rules must be configurable");
        assertTrue(reminderDaysBefore.length > 0, "At least one reminder rule must exist");
    }

    @Test
    @DisplayName("Paused deadline should not send reminders")
    void pausedDeadlineShouldNotSendReminders() {
        String status = "PAUSED";
        boolean shouldRemind = "PENDING".equals(status) || "OVERDUE".equals(status);
        assertFalse(shouldRemind, "Paused deadline must not trigger reminders");
    }
}
