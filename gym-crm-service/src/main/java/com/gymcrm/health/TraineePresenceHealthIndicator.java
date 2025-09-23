package com.gymcrm.health;

import com.gymcrm.dao.TraineeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineePresenceHealthIndicator implements HealthIndicator {

    private final TraineeDao traineeDao;

    @Override
    public Health health() {
        Boolean hasTrainees = !traineeDao.findAll().isEmpty();
        return Health.
                status(hasTrainees ? "UP" : "DOWN")
                .withDetail("traineesPresent", hasTrainees)
                .build();
    }
}
