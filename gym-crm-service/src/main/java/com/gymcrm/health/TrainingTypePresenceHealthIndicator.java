package com.gymcrm.health;

import com.gymcrm.dao.TrainingTypeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingTypePresenceHealthIndicator implements HealthIndicator {

    private final TrainingTypeDao trainingTypeDao;

    @Override
    public Health health() {
        boolean hasTypes = !trainingTypeDao.findAll().isEmpty();
        return Health.status(hasTypes ? "UP" : "DOWN")
                .withDetail("trainingTypesPresent", hasTypes)
                .build();
    }
}
