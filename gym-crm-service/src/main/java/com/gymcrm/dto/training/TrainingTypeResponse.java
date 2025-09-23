package com.gymcrm.dto.training;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainingTypeResponse {
    private Long trainingTypeId;
    private String trainingType;
}
