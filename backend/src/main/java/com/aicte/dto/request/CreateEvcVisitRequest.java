package com.aicte.dto.request;

import com.aicte.enums.MeetingMode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEvcVisitRequest {
    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    private MeetingMode meetingMode;
}
