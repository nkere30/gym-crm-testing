package com.gymcrm.service;

import com.gymcrm.client.WorkloadServiceAdapter;
import com.gymcrm.dao.TrainingDao;
import com.gymcrm.dto.WorkloadActionType;
import com.gymcrm.dto.WorkloadEventRequest;
import com.gymcrm.metric.TrainingMetrics;
import com.gymcrm.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;
    private final TrainingMetrics trainingMetrics;
    private final WorkloadServiceAdapter workloadServiceAdapter;

    public TrainingServiceImpl(TrainingDao trainingDao,
                               TrainingMetrics trainingMetrics,
                               WorkloadServiceAdapter workloadServiceAdapter) {
        this.trainingDao = trainingDao;
        this.trainingMetrics = trainingMetrics;
        this.workloadServiceAdapter = workloadServiceAdapter;
    }

    @Override
    public Training create(Training training) {
        log.info("Creating new training: {}", training);
        Training saved = trainingDao.save(training);
        trainingMetrics.increment();

        Boolean active = training.getTrainer().getIsActive();
        WorkloadEventRequest event = new WorkloadEventRequest(
                training.getTrainer().getUsername(),
                training.getTrainer().getFirstName(),
                training.getTrainer().getLastName(),
                active != null ? active : Boolean.TRUE,
                training.getTrainingDate(),
                training.getTrainingDuration(),
                WorkloadActionType.ADD
        );
        workloadServiceAdapter.sendWorkloadEvent(event);

        return saved;
    }

    @Override
    public void delete(Long id) {
        Training training = trainingDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        trainingDao.deleteById(id);

        WorkloadEventRequest event = new WorkloadEventRequest(
                training.getTrainer().getUsername(),
                training.getTrainer().getFirstName(),
                training.getTrainer().getLastName(),
                training.getTrainer().getIsActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                WorkloadActionType.DELETE
        );
        workloadServiceAdapter.sendWorkloadEvent(event);

        log.info("Training with id {} deleted", id);
    }
}
