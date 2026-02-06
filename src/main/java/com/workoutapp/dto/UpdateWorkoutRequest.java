package com.workoutapp.dto;

import com.workoutapp.model.WorkoutEntry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateWorkoutRequest {
    @Size(max = 255, message = "title cannot be greater than 255 characters")
    private String title;

    private String description;

    @Min(value = 1, message = "duration_minutes must be greater than 0")
    private Integer durationMinutes;

    @Min(value = 0, message = "calories_burned cannot be negative")
    private Integer caloriesBurned;

    @Valid
    private List<WorkoutEntry> entries;
}
