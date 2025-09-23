package com.gymcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.ChangePasswordRequest;
import com.gymcrm.dto.LoginRequest;
import com.gymcrm.dto.trainer.*;
import com.gymcrm.dto.training.TrainerTrainingResponse;
import com.gymcrm.facade.TrainerFacade;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.AuthenticationService;
import com.gymcrm.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
@Import({TrainerController.class, TrainerControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        HandlerMappingIntrospector handlerMappingIntrospector() {
            return new HandlerMappingIntrospector();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerFacade trainerFacade;

    @MockBean
    private TrainingTypeService trainingTypeService;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerTrainer_shouldReturnCreatedCredentials() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Nina");
        request.setLastName("Trainer");
        request.setSpecialization("Boxing");

        TrainingType boxing = new TrainingType();
        boxing.setTrainingTypeName("Boxing");

        TrainerRegistrationResponse response = new TrainerRegistrationResponse("Nina.Trainer", "pass123");

        when(trainingTypeService.findAll()).thenReturn(List.of(boxing));
        when(trainerFacade.createTrainer(any(Trainer.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Nina.Trainer"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void loginTrainer_shouldReturnJwtToken() throws Exception {
        LoginRequest request = new LoginRequest("Nina", "pass123");
        when(authenticationService.login(any())).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/trainers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked.jwt.token"));
    }

    @Test
    void changeTrainerPassword_shouldReturnOk() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old123");
        request.setNewPassword("new123");

        doNothing().when(authenticationService).authenticate("Nina", "old123");
        doNothing().when(trainerFacade).updatePassword("Nina", "new123");

        mockMvc.perform(put("/api/trainers/login/password")
                        .principal(() -> "Nina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer password updated"));
    }

    @Test
    void getTrainerProfile_shouldReturnProfile() throws Exception {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setFirstName("Nina");
        response.setLastName("Trainer");
        response.setSpecialization("Boxing");
        response.setIsActive(true);
        response.setTrainees(Collections.emptyList());

        when(trainerFacade.getProfile("Nina")).thenReturn(response);

        mockMvc.perform(get("/api/trainers/profile")
                        .principal(() -> "Nina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nina"))
                .andExpect(jsonPath("$.specialization").value("Boxing"));
    }

    @Test
    void updateTrainerProfile_shouldReturnUpdatedResponse() throws Exception {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("Trainer");
        request.setIsActive(true);

        TrainerUpdateResponse response = new TrainerUpdateResponse();
        response.setUsername("Nina");
        response.setFirstName("Updated");
        response.setLastName("Trainer");
        response.setSpecialization("Boxing");
        response.setIsActive(true);

        when(trainerFacade.updateTrainerProfile(any())).thenReturn(response);

        mockMvc.perform(put("/api/trainers/profile")
                        .principal(() -> "Nina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.specialization").value("Boxing"));
    }

    @Test
    void getTrainerTrainings_shouldReturnList() throws Exception {
        TrainerTrainingResponse training = new TrainerTrainingResponse(
                "Boxing Session", LocalDate.of(2025, 7, 6), "Boxing", 60, "Nina T"
        );

        when(trainerFacade.getTrainerTrainings(eq("Nina"), any(), any(), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainers/trainings")
                        .principal(() -> "Nina")
                        .param("from", "2025-07-01")
                        .param("to", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Boxing Session"));
    }

    @Test
    void updateTrainerActivation_shouldReturnOk() throws Exception {
        doNothing().when(trainerFacade).setActiveStatus("Nina", true);

        mockMvc.perform(patch("/api/trainers/activation")
                        .principal(() -> "Nina")
                        .param("isActive", "true"))
                .andExpect(status().isOk());
    }
}
