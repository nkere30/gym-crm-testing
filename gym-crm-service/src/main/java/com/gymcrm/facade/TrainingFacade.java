package com.gymcrm.facade;

import com.gymcrm.dto.training.TrainingCreateRequest;
import com.gymcrm.dto.training.TrainingResponse;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.service.TrainingTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Component
public class TrainingFacade {

    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TrainingFacade(TrainingService trainingService,
                          TrainingTypeService trainingTypeService,
                          TraineeService traineeService,
                          TrainerService trainerService) {
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public TrainingResponse createTraining(String traineeUsername, TrainingCreateRequest request) {
        log.info("Creating training '{}' for trainee '{}'", request.getTrainingName(), traineeUsername);

        Trainee trainee = traineeService.findByUsername(traineeUsername);
        Trainer trainer = trainerService.findByUsername(request.getTrainerUsername());
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found");
        }

        TrainingType trainingType = trainer.getSpecialization();
        if (trainingType == null) {
            throw new IllegalArgumentException("Trainer has no specialization");
        }

        Training training = new Training();
        training.setTrainingName(request.getTrainingName());
        training.setTrainingDate(request.getTrainingDate());
        training.setTrainingDuration(request.getTrainingDuration());
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        validateTrainingFields(training);

        Training saved = trainingService.create(training);

        return new TrainingResponse(
                saved.getId(),
                saved.getTrainee().getUsername(),
                saved.getTrainer().getUsername(),
                saved.getTrainingName(),
                saved.getTrainingDate(),
                saved.getTrainingDuration()
        );
    }



    public void deleteTraining(Long trainingId) {
        log.info("Cancelling training with id {}", trainingId);
        trainingService.delete(trainingId);
        log.info("Training with id {} successfully cancelled", trainingId);
    }

    public List<TrainingType> getAllTrainingTypes() {
        log.info("Fetching all training types");
        return trainingTypeService.findAll();
    }

    private void validateTrainingFields(Training training) {
        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }
        if (training.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
        if (training.getTrainer() == null) {
            throw new IllegalArgumentException("Trainer must be assigned");
        }
        if (training.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type must be selected");
        }
    }
}
