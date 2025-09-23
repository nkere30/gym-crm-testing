package com.gymcrm.controller;

import com.gymcrm.dto.ChangePasswordRequest;
import com.gymcrm.dto.LoginRequest;
import com.gymcrm.dto.trainer.*;
import com.gymcrm.dto.training.TrainerTrainingResponse;
import com.gymcrm.facade.TrainerFacade;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.AuthenticationService;
import com.gymcrm.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerFacade trainerFacade;
    private final TrainingTypeService trainingTypeService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<TrainerRegistrationResponse> registerTrainer(@Valid @RequestBody TrainerRegistrationRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Registering trainer: {}", tx, request.getFirstName());

        Trainer trainer = new Trainer();
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());

        for (TrainingType type : trainingTypeService.findAll()) {
            if (type.getTrainingTypeName().equalsIgnoreCase(request.getSpecialization())) {
                trainer.setSpecialization(type);
                break;
            }
        }

        TrainerRegistrationResponse created = trainerFacade.createTrainer(trainer);
        TrainerRegistrationResponse response = new TrainerRegistrationResponse(
                created.getUsername(),
                created.getPassword()
        );

        log.info("[{}] Trainer created: {}", tx, created.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Login as trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginTrainer(@RequestBody LoginRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Trainer login attempt: {}", tx, request.getUsername());
        String token = authenticationService.login(request);
        log.info("[{}] Login success: {}", tx, request.getUsername());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Change trainer password", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PutMapping("/login/password")
    public ResponseEntity<String> changeTrainerPassword(Principal principal,
                                                        @RequestBody ChangePasswordRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Changing password for trainer: {}", tx, principal.getName());
        authenticationService.authenticate(principal.getName(), request.getOldPassword());
        trainerFacade.updatePassword(principal.getName(), request.getNewPassword());
        log.info("[{}] Password updated for trainer: {}", tx, principal.getName());
        return ResponseEntity.ok("Trainer password updated");
    }

    @Operation(summary = "Get trainer profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @GetMapping("/profile")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(Principal principal) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Fetching profile for trainer: {}", tx, principal.getName());
        TrainerProfileResponse response = trainerFacade.getProfile(principal.getName());
        log.info("[{}] Profile fetched for trainer: {}", tx, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update trainer profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PutMapping("/profile")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(Principal principal,
                                                                      @RequestBody TrainerUpdateRequest request) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Updating profile for trainer: {}", tx, principal.getName());
        request.setUsername(principal.getName());
        TrainerUpdateResponse response = trainerFacade.updateTrainerProfile(request);
        log.info("[{}] Profile updated for trainer: {}", tx, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get trainer's training list", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @GetMapping("/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(Principal principal,
                                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                                             @RequestParam(required = false) String traineeName) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Fetching trainings for trainer: {}", tx, principal.getName());
        List<TrainerTrainingResponse> response = trainerFacade.getTrainerTrainings(principal.getName(), from, to, traineeName);
        log.info("[{}] Trainings fetched for trainer: {}", tx, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate or deactivate trainer", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation status updated"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    @PatchMapping("/activation")
    public ResponseEntity<Void> updateTrainerActivationStatus(Principal principal,
                                                              @RequestParam("isActive") Boolean isActive) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Updating activation for trainer: {} -> {}", tx, principal.getName(), isActive);
        trainerFacade.setActiveStatus(principal.getName(), isActive);
        log.info("[{}] Activation status updated for trainer: {}", tx, principal.getName());
        return ResponseEntity.ok().build();
    }
}
