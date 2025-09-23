package com.gymcrm.service;

import com.gymcrm.dao.TrainerDao;
import com.gymcrm.dao.TrainingDao;
import com.gymcrm.metric.TrainerMetrics;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    private TrainerDao trainerDao;
    private TrainingDao trainingDao;
    private TrainerServiceImpl trainerService;
    private TrainerMetrics trainerMetrics;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        trainingDao = mock(TrainingDao.class);
        trainerMetrics = mock(TrainerMetrics.class);
        passwordEncoder = mock(PasswordEncoder.class); // << This was missing
        trainerService = new TrainerServiceImpl(trainerDao, trainingDao, trainerMetrics, passwordEncoder);
    }


    @Test
    void findTrainingsByFilter_shouldReturnFilteredList() {
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingDao.findByTrainerUsernameWithFilters("trainer123", null, null, null)).thenReturn(trainings);

        List<Training> result = trainerService.findTrainingsByFilter("trainer123", null, null, null);

        assertEquals(2, result.size());
        verify(trainingDao).findByTrainerUsernameWithFilters("trainer123", null, null, null);
    }

    @Test
    void findUnassignedToTrainee_shouldReturnList() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());
        when(trainerDao.findUnassignedToTrainee("trainee123")).thenReturn(trainers);

        List<Trainer> result = trainerService.findUnassignedToTrainee("trainee123");

        assertEquals(2, result.size());
        verify(trainerDao).findUnassignedToTrainee("trainee123");
    }

    @Test
    void findByUsername_shouldReturnTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer123");
        when(trainerDao.findByUsername("trainer123")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findByUsername("trainer123");

        assertEquals("trainer123", result.getUsername());
        verify(trainerDao).findByUsername("trainer123");
    }

    @Test
    void findByUsername_shouldThrowIfNotFound() {
        when(trainerDao.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.findByUsername("unknown"));
    }

    @Test
    void create_shouldSaveTrainer() {
        Trainer trainer = new Trainer();
        trainer.setPassword("rawpass");

        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(trainerDao.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.create(trainer);

        assertEquals("encodedpass", result.getPassword());
        verify(passwordEncoder).encode("rawpass");
        verify(trainerDao).save(trainer);
        verify(trainerMetrics).increment();
    }


    @Test
    void update_shouldUpdateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerDao.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.update(trainer);

        assertEquals(trainer, result);
        verify(trainerDao).save(trainer);
    }

    @Test
    void updatePassword_shouldUpdateAndSavePassword() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer123");

        when(trainerDao.findByUsername("trainer123")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.encode("secure")).thenReturn("encodedSecure");
        when(trainerDao.save(any())).thenReturn(trainer);

        Trainer result = trainerService.updatePassword("trainer123", "secure");

        assertEquals("encodedSecure", result.getPassword());
        verify(trainerDao).save(trainer);
        verify(passwordEncoder).encode("secure");
    }


    @Test
    void setActiveStatus_shouldUpdateAndSaveStatus() {
        Trainer trainer = new Trainer();
        when(trainerDao.findByUsername("trainer123")).thenReturn(Optional.of(trainer));
        when(trainerDao.save(any())).thenReturn(trainer);

        trainerService.setActiveStatus("trainer123", true);

        assertTrue(trainer.getIsActive());
        verify(trainerDao).save(trainer);
    }

    @Test
    void findById_shouldReturnTrainerIfFound() {
        Trainer trainer = new Trainer();
        trainer.setId(77L);
        when(trainerDao.findById(77L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findById(77L);

        assertEquals(77L, result.getId());
        verify(trainerDao).findById(77L);
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        when(trainerDao.findById(88L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.findById(88L));
    }

    @Test
    void findAll_shouldReturnListOfTrainers() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());
        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.findAll();

        assertEquals(2, result.size());
        verify(trainerDao).findAll();
    }

}
