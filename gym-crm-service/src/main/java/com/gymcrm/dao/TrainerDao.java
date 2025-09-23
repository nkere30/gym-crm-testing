package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class TrainerDao extends AbstractUserJpaDao<Trainer>{

    public TrainerDao() {
        super(Trainer.class);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Deleting trainers is not supported.");
    }

    public List<Trainer> findUnassignedToTrainee(String traineeUsername) {
        log.info("Fetching unassigned trainers for trainee: {}", traineeUsername);

        String query = "SELECT tr FROM Trainer tr " +
                "WHERE tr NOT IN (" +
                " SELECT t FROM Trainee tn JOIN tn.trainers t " +
                " WHERE tn.username = :traineeUsername)";
        return entityManager.
                createQuery(query, Trainer.class)
                .setParameter("traineeUsername", traineeUsername)
                .getResultList();
    }
}
