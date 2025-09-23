package com.gymcrm.workload.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workload_summaries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_username", "year_val", "month_val"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_username", nullable = false)
    private String trainerUsername;

    @Column(name = "trainer_first_name", nullable = false)
    private String trainerFirstName;

    @Column(name = "trainer_last_name", nullable = false)
    private String trainerLastName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "year_val", nullable = false)
    private int year;

    @Column(name = "month_val", nullable = false)
    private int month;

    @Column(name = "total_minutes", nullable = false)
    private int totalMinutes;
}
