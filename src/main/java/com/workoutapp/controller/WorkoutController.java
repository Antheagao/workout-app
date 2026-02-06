package com.workoutapp.controller;

import com.workoutapp.dto.CreateWorkoutRequest;
import com.workoutapp.dto.UpdateWorkoutRequest;
import com.workoutapp.exception.UnauthorizedException;
import com.workoutapp.model.User;
import com.workoutapp.model.Workout;
import com.workoutapp.service.IWorkoutService;
import com.workoutapp.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {
    private final IWorkoutService workoutService;

    @GetMapping("/{id}")
    public ResponseEntity<JsonResponse> getWorkoutById(@PathVariable Long id) {
        Workout workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(JsonResponse.of("workout", workout));
    }

    @PostMapping
    public ResponseEntity<JsonResponse> createWorkout(
            @Valid @RequestBody CreateWorkoutRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        if (currentUser == null || currentUser.isAnonymous()) {
            throw new UnauthorizedException("you must be logged in");
        }

        Workout created = workoutService.createWorkout(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JsonResponse.of("workout", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JsonResponse> updateWorkout(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkoutRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        if (currentUser == null || currentUser.isAnonymous()) {
            throw new UnauthorizedException("you must be logged in to update");
        }

        Workout updated = workoutService.updateWorkout(id, request, currentUser.getId());
        return ResponseEntity.ok(JsonResponse.of("workout", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        if (currentUser == null || currentUser.isAnonymous()) {
            throw new UnauthorizedException("you must be logged in to delete");
        }

        workoutService.deleteWorkout(id, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
