package com.workoutapp.service;

import com.workoutapp.dto.CreateWorkoutRequest;
import com.workoutapp.dto.UpdateWorkoutRequest;
import com.workoutapp.model.Workout;

public interface IWorkoutService {
    Workout createWorkout(CreateWorkoutRequest request, Long userId);
    Workout getWorkoutById(Long id);
    Workout updateWorkout(Long workoutId, UpdateWorkoutRequest request, Long userId);
    void deleteWorkout(Long workoutId, Long userId);
}
