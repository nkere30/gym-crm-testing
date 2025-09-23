package com.gymcrm.service;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService extends UserService<Trainee>{
    void deleteByUsername(String username);
    List<Training> findTrainingsByFilter(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingType
    );

    void setAssignedTrainers(String traineeUsername, List<String> trainerUsernames);
}
