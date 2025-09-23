package com.gymcrm.client;

import com.gymcrm.dto.WorkloadEventRequest;
import com.gymcrm.messaging.WorkloadEventsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadServiceAdapter {

    private final WorkloadEventsPublisher publisher;

    public void sendWorkloadEvent(WorkloadEventRequest request) {
        publisher.publish(request);
        log.info("Enqueued workload event for trainer {}", request.getTrainerUsername());
    }
}
