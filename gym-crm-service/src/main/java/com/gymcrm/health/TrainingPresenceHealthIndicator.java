package com.gymcrm.health;

import com.gymcrm.dao.TrainingDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingPresenceHealthIndicator implements HealthIndicator {

    private final TrainingDao trainingDao;

    @Override
    public Health health() {
        Boolean hasTrainings = trainingDao.hasAnyTrainings();
        return Health.status(hasTrainings ? "UP" : "DOWN")
                .withDetail("trainingsPresent", hasTrainings)
                .build();
    }
}
