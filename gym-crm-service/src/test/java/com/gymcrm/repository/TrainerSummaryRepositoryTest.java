package com.gymcrm.repository;

import com.gymcrm.model.TrainerSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@DataMongoTest
@ActiveProfiles("test")
public class TrainerSummaryRepositoryTest {

    @Autowired
    private TrainerSummaryRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    void saveAndFindByUsername_shouldWorkCorrectly() {
        TrainerSummary trainer = new TrainerSummary();
        trainer.setUsername("john.doe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setStatus(true);
        trainer.setYears(List.of());

        repository.save(trainer);

        TrainerSummary found = repository.findByUsername("john.doe").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("John");
        assertThat(found.getLastName()).isEqualTo("Doe");
        assertThat(found.getStatus()).isTrue();
    }
}
