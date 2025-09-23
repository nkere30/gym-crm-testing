package com.gymcrm.dao;

import com.gymcrm.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingDaoTest {

    private TrainingDao trainingDao;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        trainingDao = new TrainingDao();
        trainingDao.entityManager = entityManager;
    }

    @Test
    void findByTraineeUsernameWithFilters_shouldReturnList() {
        String username = "trainee1";
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);
        String trainerName = "John";
        String trainingType = "Boxing";

        List<Training> mockResult = List.of(new Training());

        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResult);

        List<Training> result = trainingDao.findByTraineeUsernameWithFilters(
                username, from, to, trainerName, trainingType);

        assertEquals(1, result.size());
    }

    @Test
    void findByTrainerUsernameWithFilters_shouldReturnList() {
        String username = "trainer1";
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);
        String traineeName = "Nina";

        List<Training> mockResult = List.of(new Training());

        TypedQuery<Training> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResult);

        List<Training> result = trainingDao.findByTrainerUsernameWithFilters(
                username, from, to, traineeName);

        assertEquals(1, result.size());
    }
}
