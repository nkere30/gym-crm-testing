package com.gymcrm.messaging;

import com.gymcrm.config.TrainingMessagingProps;
import com.gymcrm.dto.WorkloadActionType;
import com.gymcrm.dto.WorkloadEventRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JmsWorkloadEventsPublisherTest {

    @Mock JmsTemplate jmsTemplate;
    @Mock TrainingMessagingProps props;

    @InjectMocks JmsWorkloadEventsPublisher publisher;

    @Test
    void publishesToConfiguredQueue() {
        when(props.getQueue()).thenReturn("training.events");

        WorkloadEventRequest evt = new WorkloadEventRequest(
                "Nina.Trainer", "Nina", "Trainer", true,
                LocalDate.parse("2025-09-09"), 54L, WorkloadActionType.ADD
        );

        publisher.publish(evt);

        verify(jmsTemplate).convertAndSend("training.events", evt);
        verifyNoMoreInteractions(jmsTemplate);
    }

    @Test
    void bubblesUpJmsErrors() {
        when(props.getQueue()).thenReturn("training.events");
        doThrow(new RuntimeException("broker down"))
                .when(jmsTemplate).convertAndSend(eq("training.events"), any(WorkloadEventRequest.class));

        WorkloadEventRequest evt = new WorkloadEventRequest(
                "Nina.Trainer", "Nina", "Trainer", true,
                LocalDate.parse("2025-09-09"), 54L, WorkloadActionType.ADD
        );

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> publisher.publish(evt));
    }
}
