package com.gymcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.training.TrainingCreateRequest;
import com.gymcrm.facade.TrainingFacade;
import com.gymcrm.model.TrainingType;
import com.gymcrm.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private TrainingFacade trainingFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addTraining_shouldReturnOk() throws Exception {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainerUsername("Trainer.Username");
        request.setTraineeUsername("Trainee.Username");
        request.setTrainingName("Boxing Basics");
        request.setTrainingDate(LocalDate.of(2025, 7, 6));
        request.setTrainingDuration(60L);

        doNothing().when(trainingFacade).createTraining(eq("Trainee.Username"), any(TrainingCreateRequest.class));

        mockMvc.perform(post("/api/trainings")
                        .principal(() -> "Trainee.Username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainingFacade).createTraining(eq("Trainee.Username"), any(TrainingCreateRequest.class));
    }


    @Test
    void getAllTrainingTypes_shouldReturnTrainingTypeList() throws Exception {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("Boxing");

        when(trainingFacade.getAllTrainingTypes()).thenReturn(List.of(type));

        mockMvc.perform(get("/api/trainings/types")
                        .with(user("mockUser").roles("TRAINEE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingTypeId").value(1))
                .andExpect(jsonPath("$[0].trainingType").value("Boxing"));
    }
}
