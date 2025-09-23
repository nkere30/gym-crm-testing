package com.gymcrm.security;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);

        Trainee trainee = null;
        Trainer trainer = null;

        try {
            trainee = traineeService.findByUsername(username);
        } catch (Exception ignored) {}

        if (trainee != null) {
            return new GymUserDetails(trainee.getUsername(), trainee.getPassword(), "ROLE_TRAINEE");
        }

        try {
            trainer = trainerService.findByUsername(username);
        } catch (Exception ignored) {}

        if (trainer != null) {
            return new GymUserDetails(trainer.getUsername(), trainer.getPassword(), "ROLE_TRAINER");
        }

        log.warn("User not found: {}", username);
        throw new UsernameNotFoundException("User not found: " + username);
    }

}
