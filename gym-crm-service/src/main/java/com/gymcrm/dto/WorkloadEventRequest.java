package com.gymcrm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadEventRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    @JsonProperty("isActive")
    private Boolean isActive;
    private LocalDate trainingDate;
    private Long trainingDuration;
    private WorkloadActionType actionType;
}
