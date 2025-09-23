package com.gymcrm.service;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.dao.TrainingDao;
import com.gymcrm.metric.TraineeMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    private TraineeDao traineeDao;
    private TrainingDao trainingDao;
    private TrainerDao trainerDao;
    private TraineeServiceImpl traineeService;
    private TraineeMetrics traineeMetrics;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainingDao = mock(TrainingDao.class);
        trainerDao = mock(TrainerDao.class);
        traineeMetrics = mock(TraineeMetrics.class);
        passwordEncoder = mock(PasswordEncoder.class);
        traineeService = new TraineeServiceImpl(traineeDao, trainingDao, trainerDao, traineeMetrics, passwordEncoder);
    }


    @Test
    void deleteByUsername_shouldDeleteWhenFound() {
        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(42L);
        when(traineeDao.findByUsername("trainee123")).thenReturn(Optional.of(mockTrainee));

        traineeService.deleteByUsername("trainee123");

        verify(traineeDao).deleteById(42L);
    }

    @Test
    void findTrainingsByFilter_shouldReturnFilteredList() {
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingDao.findByTraineeUsernameWithFilters("trainee123", null, null, null, null))
                .thenReturn(trainings);

        List<Training> result = traineeService.findTrainingsByFilter("trainee123", null, null, null, null);

        assertEquals(2, result.size());
        verify(trainingDao).findByTraineeUsernameWithFilters("trainee123", null, null, null, null);
    }

    @Test
    void setAssignedTrainers_shouldAssignAndSave() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeDao.findByUsername("trainee123")).thenReturn(Optional.of(trainee));

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("trainer1");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");

        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer1));
        when(trainerDao.findByUsername("trainer2")).thenReturn(Optional.of(trainer2));

        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        traineeService.setAssignedTrainers("trainee123", trainerUsernames);

        verify(traineeDao).save(trainee);
        assertEquals(Set.of(trainer1, trainer2), trainee.getTrainers());
    }

    @Test
    void setAssignedTrainers_shouldThrowIfTrainerNotFound() {
        Trainee trainee = new Trainee();
        when(traineeDao.findByUsername("trainee123")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("missingTrainer")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                traineeService.setAssignedTrainers("trainee123", List.of("missingTrainer")));
    }

    @Test
    void create_shouldSaveTrainee() {
        Trainee trainee = new Trainee();
        trainee.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);

        Trainee result = traineeService.create(trainee);

        assertEquals(trainee, result);
        verify(traineeDao).save(trainee);
        verify(traineeMetrics).increment();
        verify(passwordEncoder).encode("plainPassword");
    }


    @Test
    void update_shouldUpdateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeDao.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.update(trainee);

        assertEquals(trainee, result);
        verify(traineeDao).save(trainee);
    }

    @Test
    void updatePassword_shouldUpdateAndSavePassword() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee123");

        when(traineeDao.findByUsername("trainee123")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(traineeDao.save(any())).thenReturn(trainee);

        Trainee result = traineeService.updatePassword("trainee123", "newPassword");

        assertEquals("encodedNewPassword", result.getPassword());
        verify(traineeDao).save(trainee);
        verify(passwordEncoder).encode("newPassword");
    }


    @Test
    void setActiveStatus_shouldUpdateAndSaveStatus() {
        Trainee trainee = new Trainee();
        when(traineeDao.findByUsername("trainee123")).thenReturn(Optional.of(trainee));
        when(traineeDao.save(any())).thenReturn(trainee);

        traineeService.setActiveStatus("trainee123", true);

        assertTrue(trainee.getIsActive());
        verify(traineeDao).save(trainee);
    }

    @Test
    void findAll_shouldReturnListOfTrainees() {
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());
        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.findAll();

        assertEquals(2, result.size());
        verify(traineeDao).findAll();
    }

    @Test
    void findById_shouldReturnTraineeIfFound() {
        Trainee trainee = new Trainee();
        trainee.setId(99L);
        when(traineeDao.findById(99L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findById(99L);

        assertEquals(99L, result.getId());
        verify(traineeDao).findById(99L);
    }

    @Test
    void findByUsername_shouldReturnTraineeIfFound() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee123");
        when(traineeDao.findByUsername("trainee123")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findByUsername("trainee123");

        assertEquals("trainee123", result.getUsername());
        verify(traineeDao).findByUsername("trainee123");
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.findById(99L));
    }

    @Test
    void findByUsername_shouldThrowIfNotFound() {
        when(traineeDao.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.findByUsername("unknown"));
    }
}
