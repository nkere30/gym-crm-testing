package com.gymcrm.service;

import com.gymcrm.client.WorkloadServiceAdapter;
import com.gymcrm.dao.TrainingDao;
import com.gymcrm.metric.TrainingMetrics;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    private TrainingDao trainingDao;
    private TrainingServiceImpl trainingService;
    private TrainingMetrics trainingMetrics;
    private WorkloadServiceAdapter workloadServiceAdapter;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        trainingMetrics = mock(TrainingMetrics.class);
        workloadServiceAdapter = mock(WorkloadServiceAdapter.class);
        trainingService = new TrainingServiceImpl(trainingDao, trainingMetrics, workloadServiceAdapter);
    }

    @Test
    void create_shouldSaveTraining() {
        Training training = new Training();
        Trainer trainer = new Trainer();
        trainer.setUsername("john.doe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setIsActive(true);
        training.setTrainer(trainer);

        when(trainingDao.save(training)).thenReturn(training);

        Training result = trainingService.create(training);

        assertEquals(training, result);
        verify(trainingDao).save(training);
        verify(trainingMetrics).increment();
        verify(workloadServiceAdapter).sendWorkloadEvent(any());
    }
}
