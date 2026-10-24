package com.aicte.controller;

import com.aicte.dto.response.ApiResponse;
import com.aicte.service.TimeSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration & Simulation", description = "Endpoints for development time simulation and administrative operations")
public class AdminController {

    private final TimeSimulationService timeSimulationService;

    @GetMapping("/time/current")
    @Operation(summary = "Get current simulated or actual system time")
    public ResponseEntity<ApiResponse<LocalDateTime>> getCurrentTime() {
        return ResponseEntity.ok(ApiResponse.success(timeSimulationService.getCurrentTime()));
    }

    @PostMapping("/time/advance")
    @PreAuthorize("hasAuthority('WORKFLOW_CONFIGURE') or hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Advance simulated system clock by N days for deadline testing")
    public ResponseEntity<ApiResponse<LocalDateTime>> advanceDays(@RequestParam("days") int days) {
        LocalDateTime newTime = timeSimulationService.advanceDays(days);
        return ResponseEntity.ok(ApiResponse.success("Simulated time advanced by " + days + " days", newTime));
    }

    @PostMapping("/time/set")
    @PreAuthorize("hasAuthority('WORKFLOW_CONFIGURE') or hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Set simulated system clock to specific date/time")
    public ResponseEntity<ApiResponse<LocalDateTime>> setTime(
            @RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        LocalDateTime newTime = timeSimulationService.setSimulatedDate(dateTime);
        return ResponseEntity.ok(ApiResponse.success("Simulated time set", newTime));
    }

    @PostMapping("/time/reset")
    @PreAuthorize("hasAuthority('WORKFLOW_CONFIGURE') or hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Reset simulated system clock back to real system time")
    public ResponseEntity<ApiResponse<LocalDateTime>> resetTime() {
        LocalDateTime realTime = timeSimulationService.reset();
        return ResponseEntity.ok(ApiResponse.success("Simulated time reset to system clock", realTime));
    }
}
