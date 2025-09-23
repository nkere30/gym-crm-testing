package com.gymcrm.dao;

import com.gymcrm.model.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
public class TrainingDao extends AbstractJpaDao<Training, Long> {

    public TrainingDao() {
        super(Training.class);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting Training by ID: {}", id);
        super.deleteById(id);
    }

    public List<Training> findByTraineeUsernameWithFilters(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingTypeName) {

        log.info("Fetching trainings for trainee '{}' with filters - from: {}, to: {}, trainer: {}, type: {}",
                traineeUsername, from, to, trainerName, trainingTypeName);

        String safeTrainerName = trainerName == null ? "" : trainerName;
        String safeTrainingTypeName = trainingTypeName == null ? "" : trainingTypeName;

        String query = "SELECT t FROM Training t " +
                "WHERE t.trainee.username = :traineeUsername " +
                "AND (:from IS NULL OR t.trainingDate >= :from) " +
                "AND (:to IS NULL OR t.trainingDate <= :to) " +
                "AND (:trainerName = '' OR " +
                "      CONCAT(t.trainer.firstName, ' ', t.trainer.lastName) LIKE CONCAT('%', :trainerName, '%')) " +
                "AND (:trainingTypeName = '' OR t.trainingType.trainingTypeName = :trainingTypeName)";

        return entityManager
                .createQuery(query, Training.class)
                .setParameter("traineeUsername", traineeUsername)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("trainerName", safeTrainerName)
                .setParameter("trainingTypeName", safeTrainingTypeName)
                .getResultList();
    }

    public List<Training> findByTrainerUsernameWithFilters(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeName
    ) {
        log.info("Fetching trainings for trainer '{}' with filters - from: {}, to: {}, traineeName: {}",
                trainerUsername, from, to, traineeName);

        String safeTraineeName = traineeName == null ? "" : traineeName;

        String query = "SELECT t FROM Training t " +
                "WHERE t.trainer.username = :trainerUsername " +
                "AND (:from IS NULL OR t.trainingDate >= :from) " +
                "AND (:to IS NULL OR t.trainingDate <= :to) " +
                "AND (:traineeName = '' OR " +
                "      CONCAT(t.trainee.firstName, ' ', t.trainee.lastName) LIKE CONCAT('%', :traineeName, '%'))";

        return entityManager
                .createQuery(query, Training.class)
                .setParameter("trainerUsername", trainerUsername)
                .setParameter("from", from)
                .setParameter("to", to)
                .setParameter("traineeName", safeTraineeName)
                .getResultList();
    }

    public boolean hasAnyTrainings() {
        String query = "SELECT COUNT(t) FROM Training t";
        Long count = entityManager.createQuery(query, Long.class).getSingleResult();
        return count > 0;
    }
}
