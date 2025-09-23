package com.gymcrm.messaging;

import com.gymcrm.config.TrainingMessagingProps;
import com.gymcrm.dto.WorkloadEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmsWorkloadEventsPublisher implements WorkloadEventsPublisher{

    private final JmsTemplate jmsTemplate;
    private final TrainingMessagingProps props;

    @Override
    public void publish(WorkloadEventRequest event) {
        jmsTemplate.convertAndSend(props.getQueue(), event);
        log.info("Published workload event to {} for {}", props.getQueue(), event.getTrainerUsername());
    }
}
