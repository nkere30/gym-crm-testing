package com.gymcrm.facade;

import com.gymcrm.dto.training.TrainingCreateRequest;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingFacadeTest {

    private TrainingService trainingService;
    private TrainingTypeService trainingTypeService;
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingFacade trainingFacade;

    @BeforeEach
    void setUp() {
        trainingService = mock(TrainingService.class);
        trainingTypeService = mock(TrainingTypeService.class);
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        trainingFacade = new TrainingFacade(trainingService, trainingTypeService, traineeService, trainerService);
    }

    @Test
    void createTraining_shouldCreate() {
        setupValidAuth();
        TrainingCreateRequest request = validRequest();

        trainingFacade.createTraining("trainee", request);

        verify(trainingService).create(any(Training.class));
    }

    @Test
    void createTraining_shouldThrowIfTrainingDateMissing() {
        setupValidAuth();
        TrainingCreateRequest request = validRequest();
        request.setTrainingDate(null);

        assertThrows(IllegalArgumentException.class, () ->
                trainingFacade.createTraining("trainee", request));
    }

    @Test
    void createTraining_shouldThrowIfDurationIsZero() {
        setupValidAuth();
        TrainingCreateRequest request = validRequest();
        request.setTrainingDuration(0L);

        assertThrows(IllegalArgumentException.class, () ->
                trainingFacade.createTraining("trainee", request));
    }

    @Test
    void createTraining_shouldThrowIfTrainerIsNull() {
        setupValidAuth();

        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainerUsername("trainer");
        request.setTrainingName("Boxing");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60L);

        when(trainerService.findByUsername("trainer"))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Trainer not found"));

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () ->
                trainingFacade.createTraining("trainee", request));
    }

    @Test
    void createTraining_shouldThrowIfTrainingTypeIsNull() {
        setupValidAuth();

        Trainer trainer = new Trainer();
        when(trainerService.findByUsername("trainer")).thenReturn(trainer);

        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainerUsername("trainer");
        request.setTrainingName("Boxing");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60L);

        assertThrows(IllegalArgumentException.class, () ->
                trainingFacade.createTraining("trainee", request));
    }

    @Test
    void getAllTrainingTypes_shouldReturnAll() {
        List<TrainingType> types = List.of(new TrainingType(), new TrainingType());
        when(trainingTypeService.findAll()).thenReturn(types);

        List<TrainingType> result = trainingFacade.getAllTrainingTypes();

        assertEquals(2, result.size());
        verify(trainingTypeService).findAll();
    }

    @Test
    void createTraining_shouldThrowIfTrainerReturnedNull() {
        setupValidAuth();

        when(trainerService.findByUsername("trainer")).thenReturn(null);

        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainerUsername("trainer");
        request.setTrainingName("Boxing");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60L);

        assertThrows(IllegalArgumentException.class, () ->
                trainingFacade.createTraining("trainee", request));
    }

    @Test
    void createTraining_shouldPropagateUnexpectedExceptionFromCreate() {
        setupValidAuth();
        TrainingCreateRequest request = validRequest();

        doThrow(new RuntimeException("DB error"))
                .when(trainingService).create(any(Training.class));

        assertThrows(RuntimeException.class, () ->
                trainingFacade.createTraining("trainee", request));
    }

    private void setupValidAuth() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee");
        when(traineeService.findByUsername("trainee")).thenReturn(trainee);
    }

    private TrainingCreateRequest validRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainerUsername("trainer");
        request.setTrainingName("Boxing");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60L);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Boxing");

        Trainer trainer = new Trainer();
        trainer.setSpecialization(type);
        when(trainerService.findByUsername("trainer")).thenReturn(trainer);

        return request;
    }
}
