package com.gymcrm.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TrainerRegistrationRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\- ]+$", message = "First name contains invalid characters")
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\- ]+$", message = "Last name contains invalid characters")
    private String lastName;

    @NotBlank
    private String specialization;
}
