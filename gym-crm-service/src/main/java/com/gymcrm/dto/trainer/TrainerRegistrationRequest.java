package com.gymcrm.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrainerRegistrationRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String specialization;
}
