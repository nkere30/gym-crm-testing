package com.gymcrm.workload.repository;

import com.gymcrm.workload.model.WorkloadSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkloadSummaryRepository extends JpaRepository<WorkloadSummary, Long> {

    Optional<WorkloadSummary> findByTrainerUsernameAndYearAndMonth(
            String trainerUsername,
            int year,
            int month
    );

    List<WorkloadSummary> findByTrainerUsername(String trainerUsername);

}
