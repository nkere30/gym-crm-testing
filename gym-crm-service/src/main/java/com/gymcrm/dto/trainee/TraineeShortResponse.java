package com.gymcrm.dto.trainee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeShortResponse {
    private String username;
    private String firstName;
    private String lastName;
}
