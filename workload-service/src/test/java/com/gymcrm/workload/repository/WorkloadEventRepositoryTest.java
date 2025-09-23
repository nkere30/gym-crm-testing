package com.gymcrm.workload.repository;

import com.gymcrm.workload.dto.WorkloadActionType;
import com.gymcrm.workload.model.WorkloadEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WorkloadEventRepositoryTest {

    @Autowired
    private WorkloadEventRepository repository;

    @Test
    void save_shouldPersistAndReturnEntity() {
        WorkloadEvent event = WorkloadEvent.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 8, 25))
                .trainingDuration(60L)
                .actionType(WorkloadActionType.ADD)
                .build();

        WorkloadEvent saved = repository.save(event);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTrainerUsername()).isEqualTo("john.doe");
    }

    @Test
    void sumMonthlyMinutes_shouldReturnCorrectTotal() {
        LocalDate date = LocalDate.of(2025, 8, 25);

        WorkloadEvent addEvent = WorkloadEvent.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(date)
                .trainingDuration(60L)
                .actionType(WorkloadActionType.ADD)
                .build();

        WorkloadEvent deleteEvent = WorkloadEvent.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .trainingDate(date)
                .trainingDuration(30L)
                .actionType(WorkloadActionType.DELETE)
                .build();

        repository.save(addEvent);
        repository.save(deleteEvent);

        int total = repository.sumMonthlyMinutes(
                "john.doe",
                date.withDayOfMonth(1),
                date.withDayOfMonth(date.lengthOfMonth())
        );

        assertThat(total).isEqualTo(30);
    }
}
