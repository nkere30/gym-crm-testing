package com.gymcrm.health;

import com.gymcrm.dao.TrainerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerPresenceHealthIndicator implements HealthIndicator {

    private final TrainerDao trainerDao;

    @Override
    public Health health() {
        Boolean hasTrainers = !trainerDao.findAll().isEmpty();
        return Health.status(hasTrainers ? "UP" : "DOWN")
                .withDetail("trainersPresent", hasTrainers)
                .build();
    }
}
