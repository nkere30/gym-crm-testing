package com.gymcrm.health;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TraineePresenceHealthIndicatorTest {

    @Test
    void testHealth_whenTraineesExist_shouldReturnUp() {
        TraineeDao traineeDao = mock(TraineeDao.class);
        when(traineeDao.findAll()).thenReturn(List.of(new Trainee()));


        TraineePresenceHealthIndicator indicator = new TraineePresenceHealthIndicator(traineeDao);
        Health result = indicator.health();

        assertEquals("UP", result.getStatus().getCode());
    }

    @Test
    void testHealth_whenNoTrainees_shouldReturnDown() {
        TraineeDao traineeDao = mock(TraineeDao.class);
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());

        TraineePresenceHealthIndicator indicator = new TraineePresenceHealthIndicator(traineeDao);
        Health result = indicator.health();

        assertEquals("DOWN", result.getStatus().getCode());
    }
}
