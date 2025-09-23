package com.gymcrm.service;

import com.gymcrm.dao.TrainingTypeDao;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingTypeServiceImplTest {

    private TrainingTypeDao trainingTypeDao;
    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void setUp() {
        trainingTypeDao = mock(TrainingTypeDao.class);
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeDao);
    }

    @Test
    void findAll_shouldReturnListOfTrainingTypes() {
        List<TrainingType> types = List.of(new TrainingType(), new TrainingType());
        when(trainingTypeDao.findAll()).thenReturn(types);

        List<TrainingType> result = trainingTypeService.findAll();

        assertEquals(2, result.size());
        verify(trainingTypeDao).findAll();
    }
}
