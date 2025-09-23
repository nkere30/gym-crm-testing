package com.gymcrm.service;

import com.gymcrm.model.Training;

public interface TrainingService {
    Training create(Training training);
    void delete(Long id);
}
