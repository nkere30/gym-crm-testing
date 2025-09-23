package com.gymcrm.workload.messaging;

import com.gymcrm.workload.config.TrainingMessagingProps;
import com.gymcrm.workload.dto.WorkloadActionType;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.service.WorkloadService;
import jakarta.jms.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadEventsConsumerTest {

    @Mock private WorkloadService workloadService;
    @Mock private TrainingMessagingProps props;
    @Mock private JmsTemplate jmsTemplate;

    @InjectMocks private WorkloadEventsConsumer consumer;

    private static WorkloadEventRequest evt() {
        return new WorkloadEventRequest(
                "Nina.Trainer", "Nina", "Trainer", true,
                LocalDate.parse("2025-09-09"), 54L, WorkloadActionType.ADD
        );
    }

    @Test
    void processesMessage_success() {
        consumer.onMessage(evt(), null, mock(Message.class));

        verify(workloadService).recordEvent(any(WorkloadEventRequest.class));
        verifyNoInteractions(jmsTemplate, props);
    }

    @Test
    void rethrows_whenBelowMax_toTriggerRedelivery() {
        when(props.getMaxRedeliveries()).thenReturn(3);
        doThrow(new RuntimeException("boom")).when(workloadService).recordEvent(any());

        assertThrows(RuntimeException.class,
                () -> consumer.onMessage(evt(), 1, mock(Message.class)));

        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void routesToDlq_whenAtOrBeyondMax() {
        when(props.getMaxRedeliveries()).thenReturn(3);
        when(props.getDlq()).thenReturn("training.events.dlq");
        doThrow(new RuntimeException("boom")).when(workloadService).recordEvent(any());

        assertDoesNotThrow(() -> consumer.onMessage(evt(), 3, mock(Message.class)));

        verify(jmsTemplate).convertAndSend(eq("training.events.dlq"), any(WorkloadEventRequest.class));
    }
}
