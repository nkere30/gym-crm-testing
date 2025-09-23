package com.gymcrm.workload.messaging;

import com.gymcrm.workload.config.TrainingMessagingProps;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.service.WorkloadService;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadEventsConsumer {

    private final WorkloadService workloadService;
    private final TrainingMessagingProps props;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = "${app.messaging.training.queue}")
    public void onMessage(@Payload WorkloadEventRequest event,
                          @Header(name = "JMSXDeliveryCount", required = false) Integer deliveryCount,
                          Message raw) {

        int attempt = (deliveryCount == null ? 1 : deliveryCount);
        try {
            workloadService.recordEvent(event);
            log.info("Recorded event and updated summary for {}: {} min on {}",
                    event.getTrainerUsername(), event.getTrainingDuration(), event.getTrainingDate());
        } catch (Exception ex) {
            if (attempt >= props.getMaxRedeliveries()) {
                jmsTemplate.convertAndSend(props.getDlq(), event);
                log.error("Moved message to DLQ {} after {} attempts for {}. Cause={}",
                        props.getDlq(), attempt, event.getTrainerUsername(), ex.getMessage(), ex);
            } else {
                log.warn("Processing failed (attempt {}/{}). Will redeliver. Cause={}",
                        attempt, props.getMaxRedeliveries(), ex.getMessage());
                throw ex;
            }
        }
    }
}
