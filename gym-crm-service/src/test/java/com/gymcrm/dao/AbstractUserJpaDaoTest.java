package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractUserJpaDaoTest {

    private EntityManager entityManager;
    private DummyUserDao dummyDao;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        dummyDao = new DummyUserDao();
        dummyDao.entityManager = entityManager;
    }

    @Test
    void findByUsername_shouldReturnEntity() {
        Trainee expected = new Trainee();
        expected.setUsername("john");

        var query = mock(jakarta.persistence.TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(eq("username"), eq("john"))).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(expected));

        var result = dummyDao.findByUsername("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    static class DummyUserDao extends AbstractUserJpaDao<Trainee> {
        public DummyUserDao() {
            super(Trainee.class);
        }
    }
}
