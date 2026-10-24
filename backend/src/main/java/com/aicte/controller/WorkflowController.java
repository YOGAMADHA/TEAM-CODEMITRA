package com.aicte.controller;

import com.aicte.dto.response.ApiResponse;
import com.aicte.dto.response.WorkflowConfigurationDto;
import com.aicte.enums.ApplicationStage;
import com.aicte.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Tag(name = "Workflow Configuration", description = "Endpoints for viewing configurable stage flows and valid transitions")
public class WorkflowController {

    private final WorkflowService workflowService;

    @GetMapping("/transitions/{category}/{currentStage}")
    @Operation(summary = "Get allowable next stages from current stage for a category")
    public ResponseEntity<ApiResponse<List<ApplicationStage>>> getAvailableNextStages(
            @PathVariable String category,
            @PathVariable ApplicationStage currentStage) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getAvailableNextStages(category, currentStage)));
    }

    @GetMapping("/configurations/{category}")
    @Operation(summary = "Get all transition rules for an application category")
    public ResponseEntity<ApiResponse<List<WorkflowConfigurationDto>>> getConfigurationsByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getConfigurationsByCategory(category)));
    }
}
