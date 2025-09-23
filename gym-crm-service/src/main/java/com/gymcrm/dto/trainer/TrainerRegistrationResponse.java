package com.gymcrm.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerRegistrationResponse {
    private String username;
    private String password;
}
