package com.gymcrm.client;

import com.gymcrm.dto.WorkloadActionType;
import com.gymcrm.dto.WorkloadEventRequest;
import com.gymcrm.messaging.WorkloadEventsPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceAdapterTest {

    @Mock WorkloadEventsPublisher publisher;
    @InjectMocks WorkloadServiceAdapter adapter;

    @Test
    void delegatesToPublisher() {
        WorkloadEventRequest evt = new WorkloadEventRequest(
                "Nina.Trainer", "Nina", "Trainer", true,
                LocalDate.parse("2025-09-09"), 54L, WorkloadActionType.ADD
        );

        adapter.sendWorkloadEvent(evt);

        verify(publisher).publish(evt);
        verifyNoMoreInteractions(publisher);
    }
}
