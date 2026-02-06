package com.workoutapp.repository;

import com.workoutapp.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    @Query("SELECT w.userId FROM Workout w WHERE w.id = :workoutId")
    Optional<Long> findUserIdByWorkoutId(@Param("workoutId") Long workoutId);
}
