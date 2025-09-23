package com.gymcrm.dao;

import com.gymcrm.model.TrainingType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class TrainingTypeDaoTest {

    private TrainingTypeDao trainingTypeDao;

    @BeforeEach
    void setUp() {
        trainingTypeDao = new TrainingTypeDao();
        trainingTypeDao.entityManager = mock(EntityManager.class);
    }

    @Test
    void save_shouldThrowUnsupportedOperationException() {
        TrainingType type = new TrainingType();
        assertThrows(UnsupportedOperationException.class, () -> trainingTypeDao.save(type));
    }

    @Test
    void deleteById_shouldThrowUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> trainingTypeDao.deleteById(1L));
    }
}
