package com.gymcrm.health;

import com.gymcrm.dao.TrainerDao;
import com.gymcrm.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainerPresenceHealthIndicatorTest {

    @Test
    void testHealth_whenTrainersExist_shouldReturnUp() {
        TrainerDao trainerDao = mock(TrainerDao.class);
        when(trainerDao.findAll()).thenReturn(List.of(new Trainer()));

        TrainerPresenceHealthIndicator indicator = new TrainerPresenceHealthIndicator(trainerDao);
        Health result = indicator.health();

        assertEquals("UP", result.getStatus().getCode());
    }

    @Test
    void testHealth_whenNoTrainers_shouldReturnDown() {
        TrainerDao trainerDao = mock(TrainerDao.class);
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        TrainerPresenceHealthIndicator indicator = new TrainerPresenceHealthIndicator(trainerDao);
        Health result = indicator.health();

        assertEquals("DOWN", result.getStatus().getCode());
    }
}
