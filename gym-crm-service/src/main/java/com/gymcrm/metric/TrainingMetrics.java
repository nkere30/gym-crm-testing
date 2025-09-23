package com.gymcrm.metric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainingMetrics extends EntityMetric{
    public TrainingMetrics(MeterRegistry registry) {
        super(registry, "training_sessions_created_total", "Total number of created training sessions");
    }
}
