package com.gymcrm.workload.controller;

import com.gymcrm.workload.dto.MonthlyTotalResponse;
import com.gymcrm.workload.dto.WorkloadEventRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.service.WorkloadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = WorkloadController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.gymcrm\\.workload\\.security\\..*"
        )
)
@AutoConfigureMockMvc(addFilters = false)
class WorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkloadService workloadService;

    @Test
    void recordEvent_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(workloadService).recordEvent(any(WorkloadEventRequest.class));

        mockMvc.perform(post("/api/workloads/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainerUsername": "john.doe",
                                  "trainerFirstName": "John",
                                  "trainerLastName": "Doe",
                                  "isActive": true,
                                  "trainingDate": "2025-08-25",
                                  "trainingDuration": 60,
                                  "actionType": "ADD"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void getMonthlyTotal_shouldReturnResponse() throws Exception {
        Mockito.when(workloadService.getMonthlyTotal("john.doe", 2025, 8))
                .thenReturn(new MonthlyTotalResponse(120));

        mockMvc.perform(get("/api/workloads/john.doe/totals")
                        .param("year", "2025")
                        .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutes").value(120));
    }

    @Test
    void getTrainerSummary_shouldReturnResponse() throws Exception {
        WorkloadSummaryResponse response = WorkloadSummaryResponse.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .isActive(true)
                .years(List.of())
                .build();

        Mockito.when(workloadService.getTrainerSummary("john.doe"))
                .thenReturn(response);

        mockMvc.perform(get("/api/workloads/john.doe/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("john.doe"))
                .andExpect(jsonPath("$.trainerFirstName").value("John"))
                .andExpect(jsonPath("$.trainerLastName").value("Doe"));
    }
}
