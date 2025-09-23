package com.gymcrm.service;

import com.gymcrm.dao.TrainingTypeDao;
import com.gymcrm.model.TrainingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeServiceImpl(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public List<TrainingType> findAll() {
        log.info("Fetching all training types");
        return trainingTypeDao.findAll();
    }
}
