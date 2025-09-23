package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.MonthlyTotalResponse;
import com.gymcrm.workload.dto.WorkloadActionType;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.model.WorkloadEvent;
import com.gymcrm.workload.model.WorkloadSummary;
import com.gymcrm.workload.repository.WorkloadEventRepository;
import com.gymcrm.workload.repository.WorkloadSummaryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class WorkloadServiceImplTest {

    private WorkloadEventRepository eventRepository;
    private WorkloadSummaryRepository summaryRepository;
    private WorkloadServiceImpl service;

    @BeforeEach
    void setUp() {
        eventRepository = mock(WorkloadEventRepository.class);
        summaryRepository = mock(WorkloadSummaryRepository.class);
        service = new WorkloadServiceImpl(eventRepository, summaryRepository);
    }

    @Test
    void recordEvent_add_shouldCreateSummary() {
        WorkloadEventRequest request = new WorkloadEventRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2025, 8, 25), 60L, WorkloadActionType.ADD
        );

        when(summaryRepository.findByTrainerUsernameAndYearAndMonth("john.doe", 2025, 8))
                .thenReturn(Optional.empty());

        service.recordEvent(request);

        verify(eventRepository).save(any(WorkloadEvent.class));
        ArgumentCaptor<WorkloadSummary> summaryCaptor = ArgumentCaptor.forClass(WorkloadSummary.class);
        verify(summaryRepository).save(summaryCaptor.capture());

        WorkloadSummary savedSummary = summaryCaptor.getValue();
        assertThat(savedSummary.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(savedSummary.getTotalMinutes()).isEqualTo(60);
    }

    @Test
    void recordEvent_delete_shouldDecrementSummary() {
        WorkloadEventRequest request = new WorkloadEventRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2025, 8, 25), 30L, WorkloadActionType.DELETE
        );

        WorkloadSummary existing = WorkloadSummary.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .year(2025)
                .month(8)
                .totalMinutes(100)
                .build();

        when(summaryRepository.findByTrainerUsernameAndYearAndMonth("john.doe", 2025, 8))
                .thenReturn(Optional.of(existing));

        service.recordEvent(request);

        verify(eventRepository).save(any(WorkloadEvent.class));
        ArgumentCaptor<WorkloadSummary> summaryCaptor = ArgumentCaptor.forClass(WorkloadSummary.class);
        verify(summaryRepository).save(summaryCaptor.capture());

        WorkloadSummary updated = summaryCaptor.getValue();
        assertThat(updated.getTotalMinutes()).isEqualTo(70);
    }

    @Test
    void getMonthlyTotal_shouldReturnValueFromSummary() {
        WorkloadSummary summary = new WorkloadSummary(
                1L, "john.doe", "John", "Doe", true, 2025, 8, 120
        );

        when(summaryRepository.findByTrainerUsernameAndYearAndMonth("john.doe", 2025, 8))
                .thenReturn(Optional.of(summary));

        MonthlyTotalResponse response = service.getMonthlyTotal("john.doe", 2025, 8);

        assertThat(response.getTotalMinutes()).isEqualTo(120);
    }

    @Test
    void getTrainerSummary_shouldReturnAggregatedResponse() {
        WorkloadSummary s1 = new WorkloadSummary(1L, "john.doe", "John", "Doe", true, 2025, 8, 100);
        WorkloadSummary s2 = new WorkloadSummary(2L, "john.doe", "John", "Doe", true, 2025, 9, 80);
        WorkloadSummary s3 = new WorkloadSummary(3L, "john.doe", "John", "Doe", true, 2026, 1, 50);

        when(summaryRepository.findByTrainerUsername("john.doe"))
                .thenReturn(List.of(s1, s2, s3));

        WorkloadSummaryResponse response = service.getTrainerSummary("john.doe");

        assertThat(response.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(response.getYears()).hasSize(2);
        assertThat(response.getYears().get(0).getMonths()).isNotEmpty();
    }

    @Test
    void getTrainerSummary_shouldThrowIfEmpty() {
        when(summaryRepository.findByTrainerUsername("unknown"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.getTrainerSummary("unknown"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
