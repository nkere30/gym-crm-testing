package com.gymcrm.dto.trainer;

import com.gymcrm.dto.trainee.TraineeShortResponse;
import lombok.Data;

import java.util.List;

@Data
public class TrainerProfileResponse {
    private String firstName;
    private String lastName;
    private String specialization;
    private Boolean isActive;
    private List<TraineeShortResponse> trainees;
}
