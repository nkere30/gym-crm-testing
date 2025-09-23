package com.gymcrm.dto.trainee;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeUpdateRequest {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
}
