package com.gymcrm.service;

import com.gymcrm.exception.TrainerSummaryException;
import com.gymcrm.model.TrainerSummary;
import com.gymcrm.model.TrainerSummary.MonthSummary;
import com.gymcrm.model.TrainerSummary.YearSummary;
import com.gymcrm.repository.TrainerSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainerSummaryServiceTest {

    @Mock
    private TrainerSummaryRepository repository;

    @InjectMocks
    private TrainerSummaryService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processEvent_shouldCreateNewTrainerSummary_ifNotExists() {
        when(repository.findByUsername("John.Smith")).thenReturn(Optional.empty());
        when(repository.save(any(TrainerSummary.class))).thenAnswer(i -> i.getArgument(0));

        TrainerSummary result = service.processEvent(
                "tx1", "John.Smith", "John", "Smith", true,
                LocalDate.of(2025, 9, 1), 60L
        );

        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getYears()).hasSize(1);
        YearSummary year = result.getYears().get(0);
        assertThat(year.getYear()).isEqualTo(2025);
        assertThat(year.getMonths()).hasSize(1);
        MonthSummary month = year.getMonths().get(0);
        assertThat(month.getMonth()).isEqualTo(9);
        assertThat(month.getTrainingsSummaryDuration()).isEqualTo(60L);
    }

    @Test
    void processEvent_shouldUpdateExistingTrainerSummary_ifExists() {
        TrainerSummary existing = new TrainerSummary();
        existing.setUsername("John.Smith");
        existing.setYears(new ArrayList<>());
        YearSummary ys = new YearSummary(2025, new ArrayList<>());
        ys.getMonths().add(new MonthSummary(9, 30L));
        existing.getYears().add(ys);

        when(repository.findByUsername("John.Smith")).thenReturn(Optional.of(existing));
        when(repository.save(any(TrainerSummary.class))).thenAnswer(i -> i.getArgument(0));

        TrainerSummary result = service.processEvent(
                "tx2", "John.Smith", "John", "Smith", true,
                LocalDate.of(2025, 9, 1), 90L
        );

        assertThat(result.getYears().get(0).getMonths().get(0).getTrainingsSummaryDuration())
                .isEqualTo(120L);
    }

    @Test
    void processEvent_shouldThrowException_ifInvalidData() {
        assertThatThrownBy(() -> service.processEvent(
                "tx3", "", "John", "Smith", true,
                LocalDate.of(2025, 9, 1), 60L
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> service.processEvent(
                "tx3", "John.Smith", "John", "Smith", true,
                null, 60L
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> service.processEvent(
                "tx3", "John.Smith", "John", "Smith", true,
                LocalDate.of(2025, 9, 1), 0L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void processEvent_shouldWrapException_inTrainerSummaryException() {
        when(repository.findByUsername("John.Smith"))
                .thenThrow(new RuntimeException("DB down"));

        assertThatThrownBy(() -> service.processEvent(
                "tx4", "John.Smith", "John", "Smith", true,
                LocalDate.of(2025, 9, 1), 60L
        )).isInstanceOf(TrainerSummaryException.class);
    }
}
