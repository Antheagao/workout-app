package api

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"regexp"
	"strings"

	"github.com/antheagao/femProject/internal/store"
	"github.com/antheagao/femProject/internal/utils"
)


type registerUserRequest struct {
	Username string `json:"username"`
	Email string `json:"email"`
	Password string `json:"password"`
	Bio string `json:"bio"`
}


type UserHandler struct {
	userStore store.UserStore
	logger *log.Logger
}


func NewUserHandler(userStore store.UserStore, logger *log.Logger) *UserHandler {
	return &UserHandler{
		userStore: userStore,
		logger: logger,
	}
}


func (h *UserHandler) validateRegisterRequest(req *registerUserRequest) error {
	if req.Username == "" {
		return errors.New("username is required")
	}

	if len(req.Username) > 50 {
		return errors.New("username cannot be greater than 50 characters")
	}

	if req.Email == "" {
		return errors.New("email is required")
	}

	emailRegex := regexp.MustCompile(`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`)
	if !emailRegex.MatchString(req.Email) {
		return errors.New("invalid email format")
	}

	if req.Password == "" {
		return errors.New("password is required")
	}

	return nil
}

func contains(s, substr string) bool {
	return strings.Contains(strings.ToLower(s), strings.ToLower(substr))
}

func (h *UserHandler) HandleRegisterUser(w http.ResponseWriter, r *http.Request) {
	var req registerUserRequest

	err := json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		h.logger.Printf("ERROR: decoding register request: %v", err)
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": "invalid request payload"})
		return
	}

	err = h.validateRegisterRequest(&req)
	if err != nil {
		utils.WriteJSON(w, http.StatusBadRequest, utils.Envelope{"error": err.Error()})
		return
	}

	user := &store.User{
		Username: req.Username,
		Email: req.Email,
	}

	if req.Bio != "" {
		user.Bio = req.Bio
	}

	err = user.PasswordHash.Set(req.Password)
	if err != nil {
		h.logger.Printf("ERROR: hashing password %v", err)
		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "internal server error"})
		return
	}

	err = h.userStore.CreateUser(user)
	if err != nil {
		// Check for unique constraint violations
		if err.Error() != "" {
			errStr := err.Error()
			if contains(errStr, "username") && contains(errStr, "unique") {
				utils.WriteJSON(w, http.StatusConflict, utils.Envelope{"error": "username already exists"})
				return
			}
			if contains(errStr, "email") && contains(errStr, "unique") {
				utils.WriteJSON(w, http.StatusConflict, utils.Envelope{"error": "email already exists"})
				return
			}
		}
		h.logger.Printf("ERROR: registering user %v", err)
		utils.WriteJSON(w, http.StatusInternalServerError, utils.Envelope{"error": "internal server error"})
		return
	}

	utils.WriteJSON(w, http.StatusCreated, utils.Envelope{"user": user})
}
