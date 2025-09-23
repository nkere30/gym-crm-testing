package com.gymcrm.workload.model;

import com.gymcrm.workload.dto.WorkloadActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "workload_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trainerUsername;

    @Column(nullable = false)
    private String trainerFirstName;

    @Column(nullable = false)
    private String trainerLastName;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @Column(nullable = false)
    private Long trainingDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkloadActionType actionType;
}
