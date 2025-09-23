package com.gymcrm.facade;

import com.gymcrm.dto.trainee.TraineeShortResponse;
import com.gymcrm.dto.trainer.TrainerProfileResponse;
import com.gymcrm.dto.trainer.TrainerRegistrationResponse;
import com.gymcrm.dto.trainer.TrainerUpdateRequest;
import com.gymcrm.dto.trainer.TrainerUpdateResponse;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.UserCredentialsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerFacadeTest {

    private TrainerService trainerService;
    private TraineeService traineeService;
    private UserCredentialsService credentialsService;
    private TrainerFacade trainerFacade;

    @BeforeEach
    void setUp() {
        trainerService = mock(TrainerService.class);
        traineeService = mock(TraineeService.class);
        credentialsService = mock(UserCredentialsService.class);
        trainerFacade = new TrainerFacade(trainerService, traineeService, credentialsService);
    }

    @Test
    void createTrainer_shouldGenerateCredentialsAndSave() {
        Trainer input = new Trainer();
        input.setFirstName("Jane");
        input.setLastName("Smith");
        TrainingType specialization = new TrainingType();
        specialization.setTrainingTypeName("Boxing");
        input.setSpecialization(specialization);

        when(trainerService.findAll()).thenReturn(List.of());
        when(traineeService.findAll()).thenReturn(List.of());
        when(credentialsService.generateUsername(eq("Jane"), eq("Smith"), anyList(), any())).thenReturn("jane.smith");
        when(credentialsService.generatePassword()).thenReturn("trainerpass");

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        when(trainerService.create(trainerCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        TrainerRegistrationResponse result = trainerFacade.createTrainer(input);

        assertEquals("jane.smith", result.getUsername());
        assertEquals("trainerpass", result.getPassword());

        Trainer savedTrainer = trainerCaptor.getValue(); // Now works
        assertTrue(savedTrainer.getIsActive());
    }



    @Test
    void updateTrainerProfile_shouldModifyTrainer() {
        Trainer existing = new Trainer();
        existing.setUsername("trainer");
        existing.setSpecialization(new TrainingType());
        existing.setTrainees(Set.of());

        when(trainerService.findByUsername("trainer")).thenReturn(existing);
        when(trainerService.update(any())).thenAnswer(inv -> inv.getArgument(0));

        TrainerUpdateRequest request = new TrainerUpdateRequest();
        request.setUsername("trainer");
        request.setFirstName("Updated");
        request.setLastName("Last");
        request.setIsActive(true);

        TrainerUpdateResponse response = trainerFacade.updateTrainerProfile(request);

        assertEquals("Updated", response.getFirstName());
        assertEquals("Last", response.getLastName());
        assertEquals("trainer", response.getUsername());
    }

    @Test
    void setActiveStatus_shouldChangeFlag() {
        trainerFacade.setActiveStatus("trainer", false);
        verify(trainerService).setActiveStatus("trainer", false);
    }

    @Test
    void updatePassword_shouldUpdatePassword() {
        trainerFacade.updatePassword("trainer", "newpass");
        verify(trainerService).updatePassword("trainer", "newpass");
    }

    @Test
    void getTrainerTrainings_shouldReturnResponses() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer");

        Training training = new Training();
        training.setTrainingName("Session");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60L);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Boxing");
        training.setTrainingType(type);

        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        training.setTrainee(trainee);

        when(trainerService.findTrainingsByFilter("trainer", null, null, null)).thenReturn(List.of(training));

        var list = trainerFacade.getTrainerTrainings("trainer", null, null, null);
        assertEquals(1, list.size());
        assertEquals("Session", list.get(0).getTrainingName());
    }

    @Test
    void getProfile_shouldReturnMappedDto() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer");
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setIsActive(true);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        trainer.setSpecialization(type);
        trainer.setTrainees(Set.of());

        when(trainerService.findByUsername("trainer")).thenReturn(trainer);

        TrainerProfileResponse result = trainerFacade.getProfile("trainer");

        assertEquals("Alice", result.getFirstName());
        assertEquals("Yoga", result.getSpecialization());
        assertTrue(result.getIsActive());
    }

    @Test
    void createTrainer_shouldThrowIfFirstNameBlank() {
        Trainer trainer = new Trainer();
        trainer.setFirstName(" ");
        trainer.setLastName("Smith");
        trainer.setSpecialization(new TrainingType());

        assertThrows(IllegalArgumentException.class, () -> trainerFacade.createTrainer(trainer));
    }

    @Test
    void createTrainer_shouldThrowIfLastNameBlank() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName(" ");
        trainer.setSpecialization(new TrainingType());

        assertThrows(IllegalArgumentException.class, () -> trainerFacade.createTrainer(trainer));
    }

    @Test
    void createTrainer_shouldThrowIfSpecializationMissing() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        assertThrows(IllegalArgumentException.class, () -> trainerFacade.createTrainer(trainer));
    }

    @Test
    void createTrainer_shouldThrowIfDuplicateTraineeExists() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setSpecialization(new TrainingType());

        Trainee existing = new Trainee();
        existing.setFirstName("Jane");
        existing.setLastName("Smith");

        when(traineeService.findAll()).thenReturn(List.of(existing));

        assertThrows(IllegalStateException.class, () -> trainerFacade.createTrainer(trainer));
    }
}
