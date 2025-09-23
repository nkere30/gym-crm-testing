package com.gymcrm.service;

import com.gymcrm.dao.TrainerDao;
import com.gymcrm.dao.TrainingDao;
import com.gymcrm.metric.TrainerMetrics;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Transactional
@Service
public class TrainerServiceImpl extends AbstractUserService<Trainer> implements TrainerService {

    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TrainerMetrics trainerMetrics;
    private final PasswordEncoder passwordEncoder;

    public TrainerServiceImpl(TrainerDao trainerDao, TrainingDao trainingDao, TrainerMetrics trainerMetrics, PasswordEncoder passwordEncoder) {
        super(trainerDao, passwordEncoder);
        this.trainerDao = trainerDao;
        this.trainingDao = trainingDao;
        this.trainerMetrics = trainerMetrics;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Training> findTrainingsByFilter(String trainerUsername, LocalDate from, LocalDate to, String traineeName) {
        log.info("Fetching trainings for trainer '{}' with filters [from={}, to={}, traineeName={}]",
                trainerUsername, from, to, traineeName);
        return trainingDao.findByTrainerUsernameWithFilters(trainerUsername, from, to, traineeName);
    }

    @Override
    public List<Trainer> findUnassignedToTrainee(String traineeUsername) {
        log.info("Finding unassigned trainers for trainee '{}'", traineeUsername);
        return trainerDao.findUnassignedToTrainee(traineeUsername);
    }

    @Override
    public Trainer create(Trainer trainer) {
        trainer.setPassword(passwordEncoder.encode(trainer.getPassword()));
        Trainer saved = super.create(trainer);
        trainerMetrics.increment();
        return saved;
    }

}
