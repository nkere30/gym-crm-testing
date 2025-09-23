package com.gymcrm.dto.trainee;

import com.gymcrm.dto.trainer.TrainerShortResponse;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TraineeProfileResponse {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private boolean isActive;
    private List<TrainerShortResponse> trainers;
}
