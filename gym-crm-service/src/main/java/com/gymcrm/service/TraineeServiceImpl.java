package com.gymcrm.service;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.dao.TrainingDao;
import com.gymcrm.metric.TraineeMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class TraineeServiceImpl extends AbstractUserService<Trainee> implements TraineeService{

    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TraineeMetrics traineeMetrics;
    private final PasswordEncoder passwordEncoder;

    public TraineeServiceImpl(TraineeDao traineeDao, TrainingDao trainingDao, TrainerDao trainerDao, TraineeMetrics traineeMetrics, PasswordEncoder passwordEncoder) {
        super(traineeDao, passwordEncoder);
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeMetrics = traineeMetrics;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        Trainee trainee = findByUsername(username);

        for (Trainer trainer : new ArrayList<>(trainee.getTrainers())) {
            if (trainer != null && trainer.getTrainees() != null) {
                trainer.getTrainees().remove(trainee);
            }
        }
        trainee.getTrainers().clear();

        userDao.deleteById(trainee.getId());
    }


    @Override
    public List<Training> findTrainingsByFilter(String traineeUsername, LocalDate from, LocalDate to, String trainerName, String trainingType) {
        log.info("Fetching trainings for trainee '{}' with filters [from={}, to={}, trainerName={}, trainingType={}]",
                traineeUsername, from, to, trainerName, trainingType);
        return trainingDao.findByTraineeUsernameWithFilters(traineeUsername, from, to, trainerName, trainingType);
    }

    @Override
    public void setAssignedTrainers(String traineeUsername, List<String> trainerUsernames) {
        log.info("Assigning trainers to trainee '{}': {}", traineeUsername, trainerUsernames);

        Trainee trainee = findByUsername(traineeUsername);
        Set<Trainer> selectedTrainers = trainerUsernames.stream()
                .map(username -> trainerDao.findByUsername(username)
                        .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username)))
                .collect(Collectors.toSet());

        for (Trainer trainer : selectedTrainers) {
            trainer.getTrainees().add(trainee);
        }

        trainee.setTrainers(selectedTrainers);
        userDao.save(trainee);

        log.info("Assigned {} trainers to trainee '{}'", selectedTrainers.size(), traineeUsername);
    }

    @Override
    public Trainee create(Trainee trainee) {
        trainee.setPassword(passwordEncoder.encode(trainee.getPassword()));
        Trainee saved = super.create(trainee);
        traineeMetrics.increment();
        return saved;
    }

}
