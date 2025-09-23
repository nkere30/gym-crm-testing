package com.gymcrm.health;

import com.gymcrm.dao.TrainingDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingPresenceHealthIndicatorTest {

    @Test
    void testHealth_whenTrainingsExist_shouldReturnUp() {
        TrainingDao trainingDao = mock(TrainingDao.class);
        when(trainingDao.hasAnyTrainings()).thenReturn(true);

        TrainingPresenceHealthIndicator indicator = new TrainingPresenceHealthIndicator(trainingDao);
        Health result = indicator.health();

        assertEquals("UP", result.getStatus().getCode());
    }

    @Test
    void testHealth_whenNoTrainings_shouldReturnDown() {
        TrainingDao trainingDao = mock(TrainingDao.class);
        when(trainingDao.hasAnyTrainings()).thenReturn(false);

        TrainingPresenceHealthIndicator indicator = new TrainingPresenceHealthIndicator(trainingDao);
        Health result = indicator.health();

        assertEquals("DOWN", result.getStatus().getCode());
    }
}
