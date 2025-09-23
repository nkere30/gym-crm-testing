package com.gymcrm.workload.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadEventRequest {

    @NotBlank
    private String trainerUsername;

    @NotBlank
    private String trainerFirstName;

    @NotBlank
    private String trainerLastName;

    @NotNull
    @JsonProperty("isActive")
    private Boolean isActive;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    @NotNull
    @Positive
    private Long trainingDuration;

    @NotNull
    private WorkloadActionType actionType;
}
