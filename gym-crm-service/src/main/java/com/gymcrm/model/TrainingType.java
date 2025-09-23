package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training_types")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"trainings", "trainers"})
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "training_type_name", nullable = false)
    private String trainingTypeName;

    @OneToMany(mappedBy = "specialization")
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(mappedBy = "trainingType")
    private Set<Training> trainings = new HashSet<>();

}
