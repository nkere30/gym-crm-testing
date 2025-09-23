package com.gymcrm.messaging;

import com.gymcrm.dto.WorkloadEventRequest;

public interface WorkloadEventsPublisher {
    void publish(WorkloadEventRequest event);
}
