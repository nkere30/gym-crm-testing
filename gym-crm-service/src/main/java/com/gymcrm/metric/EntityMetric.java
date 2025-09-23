package com.gymcrm.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;

public abstract class EntityMetric {

    private final MeterRegistry meterRegistry;
    private final String metricName;
    private final String description;
    private Counter counter;

    protected EntityMetric(MeterRegistry meterRegistry, String metricName, String description) {
        this.meterRegistry = meterRegistry;
        this.metricName = metricName;
        this.description = description;
    }

    @PostConstruct
    public void init() {
        this.counter = Counter.builder(metricName)
                .description(description)
                .register(meterRegistry);
    }

    public void increment() {
        counter.increment();
    }
}
