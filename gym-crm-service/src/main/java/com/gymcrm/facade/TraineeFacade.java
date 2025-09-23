package com.gymcrm.facade;

import com.gymcrm.dto.trainee.TraineeProfileResponse;
import com.gymcrm.dto.trainee.TraineeRegistrationResponse;
import com.gymcrm.dto.trainee.TraineeUpdateRequest;
import com.gymcrm.dto.trainee.TraineeUpdateResponse;
import com.gymcrm.dto.trainer.TrainerShortResponse;
import com.gymcrm.dto.training.TraineeTrainingResponse;
import com.gymcrm.model.Trainee;
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
public class TraineeFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final UserCredentialsService credentialsService;

    public TraineeRegistrationResponse createTrainee(Trainee trainee) {
        log.info("Creating new trainee profile: {} {}", trainee.getFirstName(), trainee.getLastName());
        validateTraineeFields(trainee);

        String firstName = trainee.getFirstName();
        String lastName = trainee.getLastName();

        boolean existsAsTrainer = trainerService.findAll().stream()
                .anyMatch(trainer ->
                        trainer.getFirstName().equalsIgnoreCase(firstName) &&
                                trainer.getLastName().equalsIgnoreCase(lastName)
                );

        if (existsAsTrainer) {
            throw new IllegalStateException("User with this name already registered as a trainer");
        }

        String username = credentialsService.generateUsername(
                firstName, lastName,
                traineeService.findAll(), Trainee::getUsername);

        String rawPassword = credentialsService.generatePassword();
        trainee.setUsername(username);
        trainee.setPassword(rawPassword);
        trainee.setIsActive(true);

        traineeService.create(trainee);

        return new TraineeRegistrationResponse(username, rawPassword);
    }



    public TraineeUpdateResponse updateTraineeProfile(TraineeUpdateRequest request) {
        String username = request.getUsername();
        log.info("Updating trainee profile for '{}'", username);

        Trainee existing = traineeService.findByUsername(username);
        log.debug("Before update: {}", existing);

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setDateOfBirth(request.getDateOfBirth());
        existing.setAddress(request.getAddress());
        existing.setIsActive(request.getIsActive());

        Trainee updated = traineeService.update(existing);
        log.info("Trainee '{}' updated successfully", username);
        log.debug("After update: {}", updated);

        List<TrainerShortResponse> trainerDtos = updated.getTrainers().stream()
                .map(trainer -> new TrainerShortResponse(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                ))
                .toList();

        TraineeUpdateResponse response = new TraineeUpdateResponse();
        response.setUsername(updated.getUsername());
        response.setFirstName(updated.getFirstName());
        response.setLastName(updated.getLastName());
        response.setDateOfBirth(updated.getDateOfBirth());
        response.setAddress(updated.getAddress());
        response.setActive(updated.getIsActive());
        response.setTrainers(trainerDtos);

        return response;
    }

    public void updatePassword(String username, String newPassword) {
        log.info("Updating password for trainee '{}'", username);
        traineeService.updatePassword(username, newPassword);
        log.debug("Password updated for trainee '{}'", username);
    }

    public void setActiveStatus(String username, boolean active) {
        log.info("Setting active status for trainee '{}' to '{}'", username, active);
        traineeService.setActiveStatus(username, active);
    }

    public void deleteByUsername(String username) {
        log.info("Deleting trainee by username: '{}'", username);
        traineeService.deleteByUsername(username);
        log.debug("Trainee '{}' deleted", username);
    }

    public void assignTrainers(String username, List<String> trainerUsernames) {
        log.info("Assigning {} trainer(s) to trainee '{}'", trainerUsernames.size(), username);
        traineeService.setAssignedTrainers(username, trainerUsernames);
        log.debug("Trainer assignment updated for trainee '{}'", username);
    }

    public List<TrainerShortResponse> getUnassignedTrainers(String username) {
        log.info("Fetching unassigned active trainers for trainee '{}'", username);

        List<Trainer> unassigned = trainerService.findUnassignedToTrainee(username).stream()
                .filter(Trainer::getIsActive)
                .toList();

        return unassigned.stream()
                .map(trainer -> new TrainerShortResponse(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                ))
                .toList();
    }

    public TraineeProfileResponse getProfile(String username) {
        log.info("Fetching profile for trainee '{}'", username);
        Trainee trainee = traineeService.findByUsername(username);

        List<TrainerShortResponse> trainerDtos = trainee.getTrainers().stream()
                .map(trainer -> new TrainerShortResponse(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                ))
                .toList();

        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setFirstName(trainee.getFirstName());
        response.setLastName(trainee.getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.getIsActive());
        response.setTrainers(trainerDtos);

        return response;
    }

    public List<TraineeTrainingResponse> getTraineeTrainings(
            String username,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingType) {

        log.info("Fetching filtered trainings for trainee '{}'", username);

        List<Training> filtered = traineeService.findTrainingsByFilter(
                username, from, to, trainerName, trainingType);

        return filtered.stream()
                .map(training -> new TraineeTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration().intValue(),
                        training.getTrainer().getFirstName() + " " + training.getTrainer().getLastName()
                ))
                .toList();
    }

    private void validateTraineeFields(Trainee trainee) {
        log.debug("Validating fields for trainee creation");
        if (trainee.getFirstName() == null || trainee.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (trainee.getLastName() == null || trainee.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }
}
