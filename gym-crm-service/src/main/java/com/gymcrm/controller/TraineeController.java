package com.gymcrm.controller;

import com.gymcrm.dto.ChangePasswordRequest;
import com.gymcrm.dto.LoginRequest;
import com.gymcrm.dto.trainee.*;
import com.gymcrm.dto.trainer.TrainerShortResponse;
import com.gymcrm.dto.training.TraineeTrainingResponse;
import com.gymcrm.facade.TraineeFacade;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@Slf4j
public class TraineeController {

    private final TraineeFacade traineeFacade;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainee successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<TraineeRegistrationResponse> registerTrainee(@Valid @RequestBody TraineeRegistrationRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Registering trainee: {}", tx, request.getFirstName());

        Trainee trainee = new Trainee();
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        TraineeRegistrationResponse response = traineeFacade.createTrainee(trainee);

        log.info("[{}] Trainee registered: {}", tx, response.getUsername());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Login as trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginTrainee(@RequestBody LoginRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Login attempt: {}", tx, request.getUsername());
        String token = authenticationService.login(request);
        log.info("[{}] Login success: {}", tx, request.getUsername());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Get trainee profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(Principal principal) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Fetching profile for: {}", tx, principal.getName());
        TraineeProfileResponse response = traineeFacade.getProfile(principal.getName());
        log.info("[{}] Profile returned for: {}", tx, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update trainee profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/profile")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(Principal principal,
                                                                      @RequestBody TraineeUpdateRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Updating profile for: {}", tx, principal.getName());
        request.setUsername(principal.getName());
        TraineeUpdateResponse response = traineeFacade.updateTraineeProfile(request);
        log.info("[{}] Profile updated for: {}", tx, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete trainee", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee deleted"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteTrainee(Principal principal) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Deleting trainee: {}", tx, principal.getName());
        traineeFacade.deleteByUsername(principal.getName());
        log.info("[{}] Deleted trainee: {}", tx, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get unassigned trainers for trainee", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unassigned trainers retrieved")
    })
    @GetMapping("/unassigned-trainers")
    public ResponseEntity<List<TrainerShortResponse>> getUnassignedTrainers(Principal principal) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Fetching unassigned trainers for: {}", tx, principal.getName());
        List<TrainerShortResponse> trainers = traineeFacade.getUnassignedTrainers(principal.getName());
        log.info("[{}] {} unassigned trainers found", tx, trainers.size());
        return ResponseEntity.ok(trainers);
    }

    @Operation(summary = "Assign trainers to trainee", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers assigned"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    @PutMapping("/assign")
    public ResponseEntity<String> assignTrainers(Principal principal,
                                                 @RequestBody List<String> trainerUsernames) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Assigning trainers to trainee: {}", tx, principal.getName());
        traineeFacade.assignTrainers(principal.getName(), trainerUsernames);
        log.info("[{}] Trainers assigned to trainee: {}", tx, principal.getName());
        return ResponseEntity.ok("Trainers assigned successfully");
    }

    @Operation(summary = "Get trainee training list", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved")
    })
    @GetMapping("/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {

        List<TraineeTrainingResponse> trainings =
                traineeFacade.getTraineeTrainings(principal.getName(), from, to, trainerName, trainingType);

        return ResponseEntity.ok(trainings);
    }





    @Operation(summary = "Update trainee activation status", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation status updated")
    })
    @PatchMapping("/activation")
    public ResponseEntity<Void> updateTraineeActivationStatus(Principal principal,
                                                              @RequestParam("isActive") Boolean isActive) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Setting isActive={} for: {}", tx, isActive, principal.getName());
        traineeFacade.setActiveStatus(principal.getName(), isActive);
        log.info("[{}] Activation status updated for: {}", tx, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change trainee password", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PutMapping("/login/password")
    public ResponseEntity<String> changeTraineePassword(Principal principal,
                                                        @RequestBody ChangePasswordRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Password change request: {}", tx, principal.getName());

        authenticationService.authenticate(principal.getName(), request.getOldPassword());
        traineeFacade.updatePassword(principal.getName(), request.getNewPassword());

        log.info("[{}] Password updated: {}", tx, principal.getName());
        return ResponseEntity.ok("Trainee password updated");
    }
}
