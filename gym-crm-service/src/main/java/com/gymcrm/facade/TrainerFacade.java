package com.gymcrm.facade;

import com.gymcrm.dto.trainee.TraineeShortResponse;
import com.gymcrm.dto.trainer.TrainerProfileResponse;
import com.gymcrm.dto.trainer.TrainerRegistrationResponse;
import com.gymcrm.dto.trainer.TrainerUpdateRequest;
import com.gymcrm.dto.trainer.TrainerUpdateResponse;
import com.gymcrm.dto.training.TrainerTrainingResponse;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.UserCredentialsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class TrainerFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final UserCredentialsService credentialsService;

    public TrainerRegistrationResponse createTrainer(Trainer trainer) {
        log.info("Creating new trainer profile: {} {}", trainer.getFirstName(), trainer.getLastName());
        validateTrainerFields(trainer);

        String firstName = trainer.getFirstName();
        String lastName = trainer.getLastName();

        boolean existsAsTrainee = traineeService.findAll().stream()
                .anyMatch(trainee ->
                        trainee.getFirstName().equalsIgnoreCase(firstName) &&
                                trainee.getLastName().equalsIgnoreCase(lastName)
                );

        if (existsAsTrainee) {
            log.warn("Attempted to register as trainer, but already exists as trainee: {} {}", firstName, lastName);
            throw new IllegalStateException("User with this name already registered as a trainee");
        }

        String username = credentialsService.generateUsername(
                firstName, lastName,
                trainerService.findAll(), Trainer::getUsername);

        String rawPassword = credentialsService.generatePassword();
        trainer.setUsername(username);
        trainer.setPassword(rawPassword);
        trainer.setIsActive(true);

        trainerService.create(trainer);

        log.info("Trainer successfully created with username: {}", username);
        return new TrainerRegistrationResponse(username, rawPassword);
    }


    public TrainerUpdateResponse updateTrainerProfile(TrainerUpdateRequest request) {
        String username = request.getUsername();
        log.info("Updating trainer profile for '{}'", username);

        Trainer trainer = trainerService.findByUsername(username);
        log.debug("Before update: {}", trainer);

        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setIsActive(request.getIsActive());

        Trainer updated = trainerService.update(trainer);
        log.info("Trainer '{}' updated successfully", username);
        log.debug("After update: {}", updated);

        List<TraineeShortResponse> traineeDtos = updated.getTrainees().stream()
                .map(trainee -> new TraineeShortResponse(
                        trainee.getUsername(),
                        trainee.getFirstName(),
                        trainee.getLastName()
                ))
                .toList();

        TrainerUpdateResponse response = new TrainerUpdateResponse();
        response.setUsername(updated.getUsername());
        response.setFirstName(updated.getFirstName());
        response.setLastName(updated.getLastName());
        response.setSpecialization(updated.getSpecialization().getTrainingTypeName());
        response.setIsActive(updated.getIsActive());
        response.setTrainees(traineeDtos);

        return response;
    }

    public void updatePassword(String username, String newPassword) {
        log.info("Updating password for trainer '{}'", username);
        trainerService.updatePassword(username, newPassword);
        log.debug("Password updated for trainer '{}'", username);
    }

    public void setActiveStatus(String username, boolean active) {
        log.info("Setting active status for trainer '{}' to '{}'", username, active);
        trainerService.setActiveStatus(username, active);
    }

    public List<TrainerTrainingResponse> getTrainerTrainings(
            String username,
            LocalDate from,
            LocalDate to,
            String traineeName) {

        log.info("Fetching filtered training list for trainer '{}'", username);

        List<Training> trainings = trainerService.findTrainingsByFilter(
                username, from, to, traineeName);

        List<TrainerTrainingResponse> response = trainings.stream()
                .map(training -> new TrainerTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration().intValue(),
                        training.getTrainee().getFirstName() + " " + training.getTrainee().getLastName()
                ))
                .toList();

        log.debug("Trainer '{}' has {} trainings matching filters", username, response.size());
        return response;
    }

    public TrainerProfileResponse getProfile(String username) {
        log.info("Fetching profile for trainer '{}'", username);

        Trainer trainer = trainerService.findByUsername(username);

        List<TraineeShortResponse> traineeDtos = trainer.getTrainees().stream()
                .map(trainee -> new TraineeShortResponse(
                        trainee.getUsername(),
                        trainee.getFirstName(),
                        trainee.getLastName()
                ))
                .toList();

        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setFirstName(trainer.getFirstName());
        response.setLastName(trainer.getLastName());
        response.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        response.setIsActive(trainer.getIsActive());
        response.setTrainees(traineeDtos);

        return response;
    }

    private void validateTrainerFields(Trainer trainer) {
        log.debug("Validating fields for trainer creation");
        if (trainer.getFirstName() == null || trainer.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (trainer.getLastName() == null || trainer.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (trainer.getSpecialization() == null) {
            throw new IllegalArgumentException("Specialization is required");
        }
    }
}
