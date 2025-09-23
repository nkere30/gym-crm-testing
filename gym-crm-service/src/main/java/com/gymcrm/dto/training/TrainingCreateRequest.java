package com.gymcrm.dto.training;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingCreateRequest {
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private LocalDate trainingDate;
    private Long trainingDuration;
}
