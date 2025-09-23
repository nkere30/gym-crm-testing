package com.gymcrm.repository;

import com.gymcrm.model.TrainerSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;

public interface TrainerSummaryRepository extends MongoRepository<TrainerSummary, String> {
    Optional<TrainerSummary> findByUsername(String username);

    @Query("{ 'username': ?0, 'years.year': ?1, 'years.months.month': ?2 }")
    @Update("{ '$inc': { 'years.$[y].months.$[m].trainingsSummaryDuration': ?3 } }")
    void updateTrainingDuration(String username, int year, int month, int duration);
}
