package com.aicte.service;

import com.aicte.dto.response.WorkflowConfigurationDto;
import com.aicte.entity.WorkflowConfiguration;
import com.aicte.enums.ApplicationStage;
import com.aicte.exception.WorkflowException;
import com.aicte.repository.WorkflowConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final WorkflowConfigurationRepository workflowConfigRepository;

    @Transactional(readOnly = true)
    public WorkflowConfiguration validateTransition(String category, ApplicationStage fromStage, ApplicationStage toStage) {
        return workflowConfigRepository.findByCategoryAndFromStageAndToStageAndActiveTrue(category, fromStage, toStage)
                .orElseThrow(() -> new WorkflowException(
                        String.format("Invalid workflow transition from '%s' to '%s' for category '%s'",
                                fromStage, toStage, category)
                ));
    }

    @Transactional(readOnly = true)
    public List<ApplicationStage> getAvailableNextStages(String category, ApplicationStage currentStage) {
        return workflowConfigRepository.findByCategoryAndFromStageAndActiveTrue(category, currentStage)
                .stream()
                .map(WorkflowConfiguration::getToStage)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkflowConfigurationDto> getConfigurationsByCategory(String category) {
        return workflowConfigRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private WorkflowConfigurationDto mapToDto(WorkflowConfiguration c) {
        return WorkflowConfigurationDto.builder()
                .id(c.getId())
                .name(c.getName())
                .category(c.getCategory())
                .fromStage(c.getFromStage())
                .toStage(c.getToStage())
                .requiredPermission(c.getRequiredPermission())
                .autoAssign(c.isAutoAssign())
                .slaHours(c.getSlaHours())
                .active(c.isActive())
                .build();
    }
}
