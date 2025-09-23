package com.gymcrm.workload.repository;

import com.gymcrm.workload.model.WorkloadSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class WorkloadSummaryRepositoryTest {

    @Autowired
    private WorkloadSummaryRepository repository;

    @Test
    void saveAndFindByTrainerUsernameAndYearAndMonth_shouldWork() {
        WorkloadSummary summary = WorkloadSummary.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .year(2025)
                .month(8)
                .totalMinutes(120)
                .build();

        repository.save(summary);

        Optional<WorkloadSummary> found = repository.findByTrainerUsernameAndYearAndMonth(
                "john.doe", 2025, 8
        );

        assertThat(found).isPresent();
        assertThat(found.get().getTotalMinutes()).isEqualTo(120);
    }
}
