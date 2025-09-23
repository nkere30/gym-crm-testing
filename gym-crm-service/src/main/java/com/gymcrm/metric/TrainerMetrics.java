package com.gymcrm.metric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainerMetrics extends EntityMetric {
    public TrainerMetrics(MeterRegistry registry) {
        super(registry, "trainer_registrations_total", "Total number of registered trainers");
    }
}
