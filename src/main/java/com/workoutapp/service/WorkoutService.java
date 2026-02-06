package com.workoutapp.service;

import com.workoutapp.dto.CreateWorkoutRequest;
import com.workoutapp.dto.UpdateWorkoutRequest;
import com.workoutapp.exception.ResourceNotFoundException;
import com.workoutapp.exception.UnauthorizedException;
import com.workoutapp.exception.ValidationException;
import com.workoutapp.model.Workout;
import com.workoutapp.model.WorkoutEntry;
import com.workoutapp.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutService implements IWorkoutService {
    private final WorkoutRepository workoutRepository;

    @Transactional
    public Workout createWorkout(CreateWorkoutRequest request, Long userId) {
        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setTitle(request.getTitle());
        workout.setDescription(request.getDescription());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setCaloriesBurned(request.getCaloriesBurned());

        // Set workout reference for entries
        if (request.getEntries() != null) {
            for (WorkoutEntry entry : request.getEntries()) {
                entry.setWorkout(workout);
            }
            workout.setEntries(request.getEntries());
        }

        return workoutRepository.save(workout);
    }

    public Workout getWorkoutById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workout not found"));
    }

    @Transactional
    public Workout updateWorkout(Long workoutId, UpdateWorkoutRequest request, Long userId) {
        Workout existing = getWorkoutById(workoutId);

        // Check authorization
        if (!existing.getUserId().equals(userId)) {
            throw new UnauthorizedException("you are not authorized to update this workout");
        }

        // Update fields
        if (request.getTitle() != null) {
            if (request.getTitle().isEmpty()) {
                throw new ValidationException("title cannot be empty");
            }
            if (request.getTitle().length() > 255) {
                throw new ValidationException("title cannot be greater than 255 characters");
            }
            existing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getDurationMinutes() != null) {
            if (request.getDurationMinutes() <= 0) {
                throw new ValidationException("duration_minutes must be greater than 0");
            }
            existing.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getCaloriesBurned() != null) {
            if (request.getCaloriesBurned() < 0) {
                throw new ValidationException("calories_burned cannot be negative");
            }
            existing.setCaloriesBurned(request.getCaloriesBurned());
        }
        if (request.getEntries() != null) {
            // Replace entries
            existing.getEntries().clear();
            for (WorkoutEntry entry : request.getEntries()) {
                entry.setWorkout(existing);
                existing.getEntries().add(entry);
            }
        }

        return workoutRepository.save(existing);
    }

    @Transactional
    public void deleteWorkout(Long workoutId, Long userId) {
        Workout workout = getWorkoutById(workoutId);

        // Check authorization
        if (!workout.getUserId().equals(userId)) {
            throw new UnauthorizedException("you are not authorized to delete this workout");
        }

        workoutRepository.deleteById(workoutId);
    }
}
