package com.gymcrm.service;

import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService extends UserService<Trainer> {
    List<Training> findTrainingsByFilter(String trainerUsername, LocalDate from, LocalDate to, String traineeName);

    List<Trainer> findUnassignedToTrainee(String traineeUsername);
}
