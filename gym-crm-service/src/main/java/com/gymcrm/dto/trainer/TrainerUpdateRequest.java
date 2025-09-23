package com.gymcrm.dto.trainer;

import lombok.Data;

@Data
public class TrainerUpdateRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}
