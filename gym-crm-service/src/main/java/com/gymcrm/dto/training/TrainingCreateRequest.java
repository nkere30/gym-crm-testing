package com.gymcrm.dto.training;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingCreateRequest {

    @NotBlank
    private String traineeUsername;

    @NotBlank
    private String trainerUsername;

    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\- ]+$", message = "Training name contains invalid characters")
    private String trainingName;

    @NotNull
    private LocalDate trainingDate;

    @NotNull
    @Min(value = 1, message = "Duration must be positive")
    private Long trainingDuration;
}

