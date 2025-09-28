package com.gymcrm.workload.controller;

import com.gymcrm.workload.dto.MonthlyTotalResponse;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.service.WorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Tag(name = "Workloads")
@RestController
@RequestMapping("/api/workloads")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WorkloadController {

    private static final Logger log = LoggerFactory.getLogger(WorkloadController.class);

    private final WorkloadService service;

    @Operation(
            summary = "Record a workload event (ADD or DELETE)",
            description = "Persists a raw workload event and updates the trainer’s monthly summary."
    )
    @PostMapping("/events")
    public ResponseEntity<Void> recordEvent(@Valid @RequestBody WorkloadEventRequest request) {
        String txId = UUID.randomUUID().toString();
        log.info("Transaction [{}] - Recording workload event for trainer {}", txId, request.getTrainerUsername());
        service.recordEvent(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get monthly total minutes for a trainer",
            description = "Returns the aggregated monthly workload summary for the given trainer."
    )
    @GetMapping("/{trainerUsername}/totals")
    public MonthlyTotalResponse getMonthlyTotal(
            @PathVariable("trainerUsername") String trainerUsername,
            @RequestParam("year") @Min(2000) @Max(2100) int year,
            @RequestParam("month") @Min(1) @Max(12) int month) {
        String txId = UUID.randomUUID().toString();
        log.info("Transaction [{}] - Fetching monthly total for trainer {} ({}-{})", txId, trainerUsername, year, month);
        return service.getMonthlyTotal(trainerUsername, year, month);
    }

    @Operation(
            summary = "Get full workload summary for a trainer",
            description = "Returns the trainer’s workload aggregated by year and month, including all monthly totals."
    )
    @GetMapping("/{trainerUsername}/summary")
    public WorkloadSummaryResponse getTrainerSummary(
            @PathVariable("trainerUsername") String trainerUsername) {
        String txId = UUID.randomUUID().toString();
        log.info("Transaction [{}] - Fetching workload summary for trainer {}", txId, trainerUsername);
        return service.getTrainerSummary(trainerUsername);
    }
}
