package com.aicte.dto.response;

import com.aicte.enums.ApplicationStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowConfigurationDto {
    private UUID id;
    private String name;
    private String category;
    private ApplicationStage fromStage;
    private ApplicationStage toStage;
    private String requiredPermission;
    private boolean autoAssign;
    private Integer slaHours;
    private boolean active;
}
