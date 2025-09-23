package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoTest {

    private TrainerDao trainerDao;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        trainerDao = new TrainerDao();
        trainerDao.entityManager = entityManager;
    }

    @Test
    void findUnassignedToTrainee_shouldReturnList() {
        String traineeUsername = "traineeUser";
        List<Trainer> mockResult = List.of(new Trainer(), new Trainer());

        var query = mock(jakarta.persistence.TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(eq("traineeUsername"), eq(traineeUsername))).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResult);

        List<Trainer> result = trainerDao.findUnassignedToTrainee(traineeUsername);

        assertEquals(2, result.size());
        verify(entityManager).createQuery(anyString(), eq(Trainer.class));
        verify(query).setParameter("traineeUsername", traineeUsername);
    }
}
