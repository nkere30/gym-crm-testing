package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trainings")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"trainee", "trainer", "trainingType"})
public class Training {

    public Training(Long trainingDuration, LocalDate trainingDate, TrainingType trainingType, String trainingName, Trainer trainer, Trainee trainee) {
        this.trainingDuration = trainingDuration;
        this.trainingDate = trainingDate;
        this.trainingType = trainingType;
        this.trainingName = trainingName;
        this.trainer = trainer;
        this.trainee = trainee;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Long trainingDuration;

}
