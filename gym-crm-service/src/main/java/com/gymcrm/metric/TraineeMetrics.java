package com.gymcrm.metric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics extends EntityMetric {
    public TraineeMetrics(MeterRegistry registry) {
        super(registry, "trainee_registrations_total", "Total number of registered trainees");
    }
}