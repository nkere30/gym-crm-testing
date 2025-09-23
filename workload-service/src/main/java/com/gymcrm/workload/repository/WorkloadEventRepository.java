package com.gymcrm.workload.repository;

import com.gymcrm.workload.model.WorkloadEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface WorkloadEventRepository extends JpaRepository<WorkloadEvent, Long> {

    @Query("""
        select coalesce(sum(
            case
                when e.actionType = com.gymcrm.workload.dto.WorkloadActionType.ADD then e.trainingDuration
                when e.actionType = com.gymcrm.workload.dto.WorkloadActionType.DELETE then -e.trainingDuration
                else 0
            end
        ), 0)
        from WorkloadEvent e
        where e.trainerUsername = :username
          and e.trainingDate >= :from
          and e.trainingDate <= :to
    """)
    int sumMonthlyMinutes(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
