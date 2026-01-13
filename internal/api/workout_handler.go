package api

import (
	"database/sql"
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"strconv"

	"github.com/antheagao/femProject/internal/middleware"
	"github.com/antheagao/femProject/internal/store"
	"github.com/antheagao/femProject/internal/utils"
	"github.com/go-chi/chi/v5"
)

type WorkoutHandler struct {
	workoutStore store.WorkoutStore
	logger       *log.Logger
}

func NewWorkoutHandler(workoutStore store.WorkoutStore, logger *log.Logger) *WorkoutHandler {
	return &WorkoutHandler{
		workoutStore: workoutStore,
		logger:       logger,
	}
}

func (wh *WorkoutHandler) HandleGetWorkoutByID(w http.ResponseWriter, r *http.Request) {
	workoutID, err := utils.ReadIDParam(r)
	if err != nil {
		wh.logger.Printf("ERROR: readIDParam: %v", err)
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "invalid workout id"})
		return
	}

	workout, err := wh.workoutStore.GetWorkoutByID(workoutID)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			http.NotFound(w, r)
			return
		}
		wh.logger.Printf("ERROR: getWorkoutByID: %v", err)
		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "internal server error"})
		return
	}
	if workout == nil {
		http.NotFound(w, r)
		return
	}

	utils.WriteJSON(w, http.StatusOK, utils.Envelope{"workout": workout})
}

func (wh *WorkoutHandler) validateWorkout(workout *store.Workout) error {
	if workout.Title == "" {
		return errors.New("title is required")
	}
	if len(workout.Title) > 255 {
		return errors.New("title cannot be greater than 255 characters")
	}
	if workout.DurationMinutes <= 0 {
		return errors.New("duration_minutes must be greater than 0")
	}
	if workout.CaloriesBurned < 0 {
		return errors.New("calories_burned cannot be negative")
	}
	return nil
}

func (wh *WorkoutHandler) HandleCreateWorkout(w http.ResponseWriter, r *http.Request) {
	var workout store.Workout
	err := json.NewDecoder(r.Body).Decode(&workout)
	if err != nil {
		wh.logger.Printf("ERROR: decodingCreateWorkout: %v", err)
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "invalid request sent"})
		return
	}

	currentUser := middleware.GetUser(r)
	if currentUser == nil || currentUser == store.AnonymousUser {
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "you must be logged in"})
		return
	}

	workout.UserID = currentUser.ID

	err = wh.validateWorkout(&workout)
	if err != nil {
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": err.Error()})
		return
	}

	createdWorkout, err := wh.workoutStore.CreateWorkout(&workout)
	if err != nil {
		wh.logger.Printf("ERROR: createWorkout: %v", err)
		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "failed to create workout"})
		return
	}

	utils.WriteJSON(w, http.StatusCreated, utils.Envelope{"workout": createdWorkout})
}

func (wh *WorkoutHandler) HandleUpdateWorkoutByID(w http.ResponseWriter, r *http.Request) {
	workoutID, err := utils.ReadIDParam(r)
	if err != nil {
		wh.logger.Printf("ERROR: readIDParam: %v", err)
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "invalid workout update id"})
		return
	}

	existingWorkout, err := wh.workoutStore.GetWorkoutByID(workoutID)
	if err != nil {
		wh.logger.Printf("ERROR: getWorkoutByID: %v", err)
		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "internal server error"})
		return
	}

	if existingWorkout == nil {
		http.NotFound(w, r)
		return
	}

	// Can find an existing workout
	var updateWorkoutRequest struct {
		Title           *string              `json:"title"`
		Description     *string              `json:"description"`
		DurationMinutes *int                 `json:"duration_minutes"`
		CaloriesBurned  *int                 `json:"calories_burned"`
		Entries         []store.WorkoutEntry `json:"entries"`
	}

	err = json.NewDecoder(r.Body).Decode(&updateWorkoutRequest)
	if err != nil {
		wh.logger.Printf("ERROR: decodingUpdateRequest: %v", err)
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "invalid request payload"})
		return
	}

	if updateWorkoutRequest.Title != nil {
		if *updateWorkoutRequest.Title == "" {
			utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "title cannot be empty"})
			return
		}
		if len(*updateWorkoutRequest.Title) > 255 {
			utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "title cannot be greater than 255 characters"})
			return
		}
		existingWorkout.Title = *updateWorkoutRequest.Title
	}
	if updateWorkoutRequest.Description != nil {
		existingWorkout.Description = *updateWorkoutRequest.Description
	}
	if updateWorkoutRequest.DurationMinutes != nil {
		if *updateWorkoutRequest.DurationMinutes <= 0 {
			utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "duration_minutes must be greater than 0"})
			return
		}
		existingWorkout.DurationMinutes = *updateWorkoutRequest.DurationMinutes
	}
	if updateWorkoutRequest.CaloriesBurned != nil {
		if *updateWorkoutRequest.CaloriesBurned < 0 {
			utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "calories_burned cannot be negative"})
			return
		}
		existingWorkout.CaloriesBurned = *updateWorkoutRequest.CaloriesBurned
	}
	if updateWorkoutRequest.Entries != nil {
		existingWorkout.Entries = updateWorkoutRequest.Entries
	}

	currentUser := middleware.GetUser(r)
	if currentUser == nil || currentUser == store.AnonymousUser {
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "you must be logged in to update"})
		return
	}

	workoutOwner, err := wh.workoutStore.GetWorkoutOwner(workoutID)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			utils.WriteJSON(w, http.StatusNotFound, utils.Envelope{"error": "workout does not exist"})
			return
		}

		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "internal server error"})
		return
	}

	if workoutOwner != currentUser.ID {
		utils.WriteJSON(w, http.StatusForbidden, utils.Envelope{"error": "you are not authorized to update this workout"})
		return
	}

	err = wh.workoutStore.UpdateWorkout(existingWorkout)
	if err != nil {
		wh.logger.Printf("ERROR: updateWorkout: %v", err)
		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "internal server error"})
		return
	}

	utils.WriteJSON(w, http.StatusOK, utils.Envelope{"workout": existingWorkout})
}

func (wh *WorkoutHandler) HandleDeleteWorkoutByID(w http.ResponseWriter, r *http.Request) {
	paramsWorkoutID := chi.URLParam(r, "id")
	if paramsWorkoutID == "" {
		http.NotFound(w, r)
		return
	}

	workoutID, err := strconv.ParseInt(paramsWorkoutID, 10, 64)
	if err != nil {
		http.NotFound(w, r)
		return
	}

	currentUser := middleware.GetUser(r)
	if currentUser == nil || currentUser == store.AnonymousUser {
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "you must be logged in to update"})
		return
	}

	workoutOwner, err := wh.workoutStore.GetWorkoutOwner(workoutID)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			utils.WriteJSON(w, http.StatusNotFound, utils.Envelope{"error": "workout does not exist"})
			return
		}

		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "internal server error"})
		return
	}

	if workoutOwner != currentUser.ID {
		utils.WriteJSON(w, http.StatusForbidden, utils.Envelope{"error": "you are not authorized to delete this workout"})
		return
	}

	err = wh.workoutStore.DeleteWorkout(workoutID)
	if err == sql.ErrNoRows {
		http.Error(w, "workout not found", http.StatusNotFound)
		return
	}

	if err != nil {
		http.Error(w, "error deleting workout", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusNoContent)
}
