package com.gymcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.ChangePasswordRequest;
import com.gymcrm.dto.LoginRequest;
import com.gymcrm.dto.trainee.*;
import com.gymcrm.dto.trainer.TrainerShortResponse;
import com.gymcrm.dto.training.TraineeTrainingResponse;
import com.gymcrm.facade.TraineeFacade;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.AuthenticationService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
@Import({TraineeController.class, TraineeControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = false) // disable security filters in test context
class TraineeControllerTest {

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
    private TraineeFacade traineeFacade;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerTrainee_shouldReturnCreatedCredentials() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("Nina");
        request.setLastName("Trainee");
        request.setDateOfBirth(LocalDate.of(1999, 1, 1));
        request.setAddress("Tbilisi");

        TraineeRegistrationResponse response =
                new TraineeRegistrationResponse("Nina.Trainee", "pass123");
        when(traineeFacade.createTrainee(any(Trainee.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainees")
                        .principal(() -> "anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Nina.Trainee"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void loginTrainee_shouldReturnJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("Nina", "pass123");
        when(authenticationService.login(any())).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/trainees/login")
                        .principal(() -> "anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked-jwt-token"));
    }

    @Test
    void changePassword_shouldReturnOk() throws Exception {
        doNothing().when(authenticationService).authenticate("Nina", "old123");
        doNothing().when(traineeFacade).updatePassword("Nina", "new123");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old123");
        request.setNewPassword("new123");

        mockMvc.perform(put("/api/trainees/login/password")
                        .principal(() -> "Nina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getTraineeProfile_shouldReturnProfile() throws Exception {
        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setFirstName("Nina");
        response.setLastName("Trainee");
        response.setDateOfBirth(LocalDate.of(1999, 1, 1));
        response.setAddress("Tbilisi");
        response.setActive(true);
        response.setTrainers(List.of());

        when(traineeFacade.getProfile("Nina")).thenReturn(response);

        mockMvc.perform(get("/api/trainees/profile")
                        .principal(() -> "Nina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nina"))
                .andExpect(jsonPath("$.lastName").value("Trainee"));
    }

    @Test
    void updateProfile_shouldReturnUpdated() throws Exception {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setFirstName("Updated");

        TraineeUpdateResponse response = new TraineeUpdateResponse();
        response.setUsername("Nina");
        response.setFirstName("Updated");

        when(traineeFacade.updateTraineeProfile(any())).thenReturn(response);

        mockMvc.perform(put("/api/trainees/profile")
                        .principal(() -> "Nina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.username").value("Nina"));
    }

    @Test
    void deleteTrainee_shouldReturnOk() throws Exception {
        doNothing().when(traineeFacade).deleteByUsername("Nina");

        mockMvc.perform(delete("/api/trainees/profile")
                        .principal(() -> "Nina"))
                .andExpect(status().isOk());
    }

    @Test
    void getUnassignedTrainers_shouldReturnList() throws Exception {
        TrainerShortResponse trainer =
                new TrainerShortResponse("john.trainer", "John", "Trainer", "Boxing");
        when(traineeFacade.getUnassignedTrainers("Nina")).thenReturn(List.of(trainer));

        mockMvc.perform(get("/api/trainees/unassigned-trainers")
                        .principal(() -> "Nina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john.trainer"));
    }

    @Test
    void assignTrainers_shouldReturnSuccess() throws Exception {
        List<String> trainers = List.of("john.trainer");
        doNothing().when(traineeFacade).assignTrainers("Nina", trainers);

        mockMvc.perform(put("/api/trainees/assign")
                        .principal(() -> "Nina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainers)))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainers assigned successfully"));
    }

    @Test
    void getTrainings_shouldReturnList() throws Exception {
        TraineeTrainingResponse training =
                new TraineeTrainingResponse("Boxing", LocalDate.of(2025, 7, 6),
                        "Boxing", 60, "John T");

        when(traineeFacade.getTraineeTrainings(
                eq("Nina"), any(LocalDate.class), any(LocalDate.class),
                any(), any())).thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainees/trainings")
                        .principal(() -> "Nina")
                        .param("from", "2025-07-01")
                        .param("to", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Boxing"));
    }

    @Test
    void updateActivationStatus_shouldReturnOk() throws Exception {
        doNothing().when(traineeFacade).setActiveStatus("Nina", false);

        mockMvc.perform(patch("/api/trainees/activation")
                        .principal(() -> "Nina")
                        .param("isActive", "false"))
                .andExpect(status().isOk());
    }
}
