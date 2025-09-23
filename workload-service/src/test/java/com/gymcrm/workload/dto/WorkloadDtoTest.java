package com.gymcrm.workload.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class WorkloadDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void monthlyTotalResponse_shouldHoldValue() {
        MonthlyTotalResponse response = new MonthlyTotalResponse(120);

        assertThat(response.getTotalMinutes()).isEqualTo(120);

        response.setTotalMinutes(200);
        assertThat(response.getTotalMinutes()).isEqualTo(200);
    }

    @Test
    void workloadActionType_shouldContainValues() {
        assertThat(WorkloadActionType.valueOf("ADD")).isEqualTo(WorkloadActionType.ADD);
        assertThat(WorkloadActionType.valueOf("DELETE")).isEqualTo(WorkloadActionType.DELETE);
    }

    @Test
    void workloadEventRequest_shouldStoreFields() {
        LocalDate date = LocalDate.of(2025, 8, 25);
        WorkloadEventRequest req = new WorkloadEventRequest(
                "john.doe",
                "John",
                "Doe",
                true,
                date,
                60L,
                WorkloadActionType.ADD
        );

        assertThat(req.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(req.getTrainerFirstName()).isEqualTo("John");
        assertThat(req.getTrainerLastName()).isEqualTo("Doe");
        assertThat(req.getIsActive()).isTrue();
        assertThat(req.getTrainingDate()).isEqualTo(date);
        assertThat(req.getTrainingDuration()).isEqualTo(60L);
        assertThat(req.getActionType()).isEqualTo(WorkloadActionType.ADD);
    }

    @Test
    void workloadEventRequest_shouldSerializeToJson() throws Exception {
        LocalDate date = LocalDate.of(2025, 8, 25);
        WorkloadEventRequest req = new WorkloadEventRequest(
                "john.doe",
                "John",
                "Doe",
                true,
                date,
                60L,
                WorkloadActionType.ADD
        );

        mapper.registerModule(new JavaTimeModule()); // 👈 add this line

        String json = mapper.writeValueAsString(req);
        assertThat(json).contains("john.doe", "John", "Doe", "2025-08-25", "ADD");

        WorkloadEventRequest deserialized = mapper.readValue(json, WorkloadEventRequest.class);
        assertThat(deserialized.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(deserialized.getTrainingDate()).isEqualTo(date);
    }

}
