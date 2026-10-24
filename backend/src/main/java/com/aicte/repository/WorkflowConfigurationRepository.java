package com.aicte.repository;

import com.aicte.entity.WorkflowConfiguration;
import com.aicte.enums.ApplicationStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowConfigurationRepository extends JpaRepository<WorkflowConfiguration, UUID> {
    List<WorkflowConfiguration> findByCategoryAndActiveTrue(String category);
    List<WorkflowConfiguration> findByCategoryAndFromStageAndActiveTrue(String category, ApplicationStage fromStage);
    Optional<WorkflowConfiguration> findByCategoryAndFromStageAndToStageAndActiveTrue(String category, ApplicationStage fromStage, ApplicationStage toStage);
}
