package com.gymcrm.security;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        userDetailsService = new CustomUserDetailsService(traineeService, trainerService);
    }

    @Test
    void loadUserByUsername_shouldReturnTraineeDetails() {
        Trainee trainee = new Trainee();
        trainee.setUsername("nina");
        trainee.setPassword("pass");

        when(traineeService.findByUsername("nina")).thenReturn(trainee);

        var details = userDetailsService.loadUserByUsername("nina");

        assertEquals("nina", details.getUsername());
        assertEquals("pass", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINEE")));
    }

    @Test
    void loadUserByUsername_shouldReturnTrainerDetailsIfTraineeNotFound() {
        when(traineeService.findByUsername("john")).thenThrow(new RuntimeException());

        Trainer trainer = new Trainer();
        trainer.setUsername("john");
        trainer.setPassword("secure");
        when(trainerService.findByUsername("john")).thenReturn(trainer);

        var details = userDetailsService.loadUserByUsername("john");

        assertEquals("john", details.getUsername());
        assertEquals("secure", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER")));
    }

    @Test
    void loadUserByUsername_shouldThrowIfUserNotFound() {
        when(traineeService.findByUsername("ghost")).thenThrow(new RuntimeException());
        when(trainerService.findByUsername("ghost")).thenThrow(new RuntimeException());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("ghost"));
    }
}
