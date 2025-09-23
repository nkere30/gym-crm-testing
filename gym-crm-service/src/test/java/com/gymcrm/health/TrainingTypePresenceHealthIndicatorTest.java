package com.gymcrm.health;

import com.gymcrm.dao.TrainingTypeDao;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingTypePresenceHealthIndicatorTest {

    @Test
    void testHealth_whenTypesExist_shouldReturnUp() {
        TrainingTypeDao trainingTypeDao = mock(TrainingTypeDao.class);
        when(trainingTypeDao.findAll()).thenReturn(List.of(new TrainingType()));

        TrainingTypePresenceHealthIndicator indicator = new TrainingTypePresenceHealthIndicator(trainingTypeDao);
        Health result = indicator.health();

        assertEquals("UP", result.getStatus().getCode());
    }

    @Test
    void testHealth_whenNoTypes_shouldReturnDown() {
        TrainingTypeDao trainingTypeDao = mock(TrainingTypeDao.class);
        when(trainingTypeDao.findAll()).thenReturn(Collections.emptyList());

        TrainingTypePresenceHealthIndicator indicator = new TrainingTypePresenceHealthIndicator(trainingTypeDao);
        Health result = indicator.health();

        assertEquals("DOWN", result.getStatus().getCode());
    }
}
