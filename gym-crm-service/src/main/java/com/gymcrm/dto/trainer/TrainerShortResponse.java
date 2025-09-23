package com.gymcrm.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerShortResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization; // training type name
}
