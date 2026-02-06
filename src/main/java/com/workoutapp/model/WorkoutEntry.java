package com.workoutapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "workout_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Column(name = "exercise_name", nullable = false, length = 255)
    private String exerciseName;

    @Column(nullable = false)
    private Integer sets;

    private Integer reps;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(precision = 5, scale = 2)
    private Double weight;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
