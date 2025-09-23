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
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.service.UserCredentialsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeFacadeTest {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private UserCredentialsService credentialsService;
    private TraineeFacade traineeFacade;

    @BeforeEach
    void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        credentialsService = mock(UserCredentialsService.class);
        traineeFacade = new TraineeFacade(traineeService, trainerService, credentialsService);
    }

    @Test
    void createTrainee_shouldGenerateUsernameAndPassword() {
        Trainee input = new Trainee();
        input.setFirstName("Anna");
        input.setLastName("Smith");

        when(trainerService.findAll()).thenReturn(List.of());
        when(traineeService.findAll()).thenReturn(List.of());
        when(credentialsService.generateUsername(eq("Anna"), eq("Smith"), anyList(), any())).thenReturn("Anna.Smith");
        when(credentialsService.generatePassword()).thenReturn("generatedPass");
        when(traineeService.create(any())).thenAnswer(inv -> inv.getArgument(0));

        TraineeRegistrationResponse result = traineeFacade.createTrainee(input);

        assertEquals("Anna.Smith", result.getUsername());
        assertEquals("generatedPass", result.getPassword());
    }


    @Test
    void updatePassword_shouldDelegateToService() {
        traineeFacade.updatePassword("trainee", "newpass");
        verify(traineeService).updatePassword("trainee", "newpass");
    }

    @Test
    void deleteByUsername_shouldDelegateToService() {
        traineeFacade.deleteByUsername("trainee");
        verify(traineeService).deleteByUsername("trainee");
    }

    @Test
    void setActiveStatus_shouldDelegateToService() {
        traineeFacade.setActiveStatus("trainee", true);
        verify(traineeService).setActiveStatus("trainee", true);
    }

    @Test
    void assignTrainers_shouldDelegateToService() {
        List<String> trainers = List.of("T1", "T2");
        traineeFacade.assignTrainers("trainee", trainers);
        verify(traineeService).setAssignedTrainers("trainee", trainers);
    }

    @Test
    void getUnassignedTrainers_shouldReturnOnlyActive() {
        Trainer active = new Trainer();
        active.setUsername("a");
        active.setFirstName("Active");
        active.setLastName("Trainer");
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Boxing");
        active.setSpecialization(type);
        active.setIsActive(true);

        Trainer inactive = new Trainer();
        inactive.setIsActive(false);

        when(trainerService.findUnassignedToTrainee("trainee")).thenReturn(List.of(active, inactive));

        List<TrainerShortResponse> result = traineeFacade.getUnassignedTrainers("trainee");

        assertEquals(1, result.size());
        assertEquals("a", result.get(0).getUsername());
    }

    @Test
    void getProfile_shouldMapCorrectly() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Anna");
        trainee.setLastName("Smith");
        trainee.setIsActive(true);
        trainee.setTrainers(Set.of());

        when(traineeService.findByUsername("trainee")).thenReturn(trainee);

        TraineeProfileResponse result = traineeFacade.getProfile("trainee");

        assertEquals("Anna", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertTrue(result.isActive());
    }

    @Test
    void getTraineeTrainings_shouldMapCorrectly() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");

        Training training = new Training();
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60L);
        training.setTrainingType(type);
        training.setTrainer(trainer);

        when(traineeService.findTrainingsByFilter(eq("trainee"), any(), any(), any(), any()))
                .thenReturn(List.of(training));

        List<TraineeTrainingResponse> result = traineeFacade.getTraineeTrainings("trainee", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
        assertEquals("Yoga", result.get(0).getTrainingType());
        assertEquals("John Doe", result.get(0).getTrainerName());
    }


    @Test
    void updateTraineeProfile_shouldMapResponseCorrectly() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setUsername("trainee");
        request.setFirstName("New");
        request.setLastName("Name");
        request.setIsActive(true);

        Trainee updated = new Trainee();
        updated.setUsername("trainee");
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setIsActive(true);
        updated.setTrainers(Set.of());

        when(traineeService.findByUsername("trainee")).thenReturn(updated);
        when(traineeService.update(any())).thenReturn(updated);

        TraineeUpdateResponse response = traineeFacade.updateTraineeProfile(request);

        assertEquals("trainee", response.getUsername());
        assertEquals("New", response.getFirstName());
    }

    @Test
    void createTrainee_shouldThrowIfTrainerAlreadyExists() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Anna");
        trainee.setLastName("Smith");

        Trainer existing = new Trainer();
        existing.setFirstName("Anna");
        existing.setLastName("Smith");

        when(trainerService.findAll()).thenReturn(List.of(existing));

        assertThrows(IllegalStateException.class, () -> traineeFacade.createTrainee(trainee));
    }

    @Test
    void createTrainee_shouldThrowIfFirstOrLastNameInvalid() {
        Trainee t1 = new Trainee();
        t1.setFirstName(" ");
        t1.setLastName("Doe");

        assertThrows(IllegalArgumentException.class, () -> traineeFacade.createTrainee(t1));

        Trainee t2 = new Trainee();
        t2.setFirstName("John");
        t2.setLastName(" ");

        assertThrows(IllegalArgumentException.class, () -> traineeFacade.createTrainee(t2));
    }
}
