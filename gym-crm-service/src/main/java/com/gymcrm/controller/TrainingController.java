package com.gymcrm.controller;

import com.gymcrm.dto.training.TrainingCreateRequest;
import com.gymcrm.dto.training.TrainingResponse;
import com.gymcrm.dto.training.TrainingTypeResponse;
import com.gymcrm.facade.TrainingFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingFacade trainingFacade;

    @Operation(summary = "Add new training session", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Training created"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<TrainingResponse> addTraining(@RequestBody TrainingCreateRequest request,
                                                        Principal principal) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Creating training session for trainee: {}", tx, principal.getName());

        trainingFacade.createTraining(principal.getName(), request);

        TrainingResponse response = new TrainingResponse(
                principal.getName(),
                request.getTrainerUsername(),
                request.getTrainingName(),
                request.getTrainingDate(),
                request.getTrainingDuration()
        );

        log.info("[{}] Training session created successfully for trainee: {}", tx, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Cancel a training session", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "404", description = "Training not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Cancelling training session with id {}", tx, id);

        trainingFacade.deleteTraining(id);

        log.info("[{}] Training session with id {} cancelled successfully", tx, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all available training types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types returned")
    })
    @GetMapping("/types")
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Fetching all training types", tx);

        List<TrainingTypeResponse> types = trainingFacade.getAllTrainingTypes()
                .stream()
                .map(type -> new TrainingTypeResponse(type.getId(), type.getTrainingTypeName()))
                .toList();

        log.info("[{}] Retrieved {} training types", tx, types.size());
        return ResponseEntity.ok(types);
    }
}
