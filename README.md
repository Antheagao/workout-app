# Workout Tracker API

A RESTful API built with Java and Spring Boot for tracking workouts and exercise routines. The application provides user authentication, workout management, and exercise entry tracking with a PostgreSQL database backend.

## Features

### User Management
- **User Registration**: Create new user accounts with username, email, password, and optional bio
- **Email Validation**: Automatic email format validation using Jakarta Bean Validation
- **Password Security**: Passwords are hashed using BCrypt with cost factor 12
- **Unique Constraints**: Username and email must be unique across all users
- **User Profiles**: Store user information including bio and timestamps

### Authentication & Authorization
- **Token-Based Authentication**: Secure authentication using bearer tokens
- **Token Generation**: Generate authentication tokens with 24-hour expiration
- **Token Hashing**: Tokens are hashed using SHA-256 before storage
- **Scope-Based Tokens**: Support for different token scopes (currently authentication)
- **Middleware Protection**: Route-level authentication middleware using Spring Interceptors
- **User Context**: Automatic user context injection for protected routes

### Workout Management
- **Create Workouts**: Create new workouts with title, description, duration, and calories burned
- **View Workouts**: Retrieve workout details by ID with all associated exercise entries
- **Update Workouts**: Partially update workout information (title, description, duration, calories, entries)
- **Delete Workouts**: Remove workouts (cascades to workout entries)
- **Ownership Validation**: Users can only modify their own workouts
- **Input Validation**: Comprehensive validation for all workout fields

### Exercise Entry Management
- **Exercise Tracking**: Track individual exercises within workouts
- **Flexible Exercise Types**: Support for both rep-based and duration-based exercises
- **Exercise Details**: Store exercise name, sets, reps (optional), duration (optional), weight, and notes
- **Ordered Entries**: Maintain exercise order using order_index
- **Data Integrity**: Database constraints ensure valid exercise entries (either reps OR duration, not both)

### Database Features
- **PostgreSQL Backend**: Robust relational database with proper foreign keys
- **Database Migrations**: Automated schema migrations using Flyway
- **Transaction Support**: Atomic operations for workout creation and updates
- **Cascade Deletes**: Automatic cleanup of related records
- **Timestamps**: Automatic tracking of created_at and updated_at timestamps

### API Features
- **RESTful Design**: Clean REST API following best practices
- **JSON Responses**: Consistent JSON response format with envelope pattern
- **Error Handling**: Comprehensive error handling with appropriate HTTP status codes
- **Request Validation**: Input validation for all endpoints using Jakarta Bean Validation
- **Health Check**: Health check endpoint for monitoring
- **Spring Boot**: Modern Java framework with dependency injection

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL 18.1
- **ORM**: Spring Data JPA / Hibernate
- **Database Driver**: PostgreSQL JDBC Driver
- **Migrations**: Flyway
- **Password Hashing**: BCrypt (Spring Security)
- **Token Encoding**: Apache Commons Codec (Base32)
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## Project Structure

```
workout-app/
├── pom.xml                          # Maven project configuration
├── src/
│   ├── main/
│   │   ├── java/com/workoutapp/
│   │   │   ├── WorkoutApplication.java    # Application entry point
│   │   │   ├── config/                    # Configuration classes
│   │   │   │   └── WebConfig.java         # Web configuration (interceptors)
│   │   │   ├── controller/                # REST controllers
│   │   │   │   ├── UserController.java    # User registration endpoint
│   │   │   │   ├── TokenController.java   # Authentication token generation
│   │   │   │   ├── WorkoutController.java # Workout CRUD operations
│   │   │   │   └── HealthController.java  # Health check endpoint
│   │   │   ├── service/                   # Business logic layer
│   │   │   │   ├── IUserService.java      # User service interface
│   │   │   │   ├── IWorkoutService.java   # Workout service interface
│   │   │   │   ├── ITokenService.java     # Token service interface
│   │   │   │   ├── UserService.java       # User business logic implementation
│   │   │   │   ├── TokenService.java      # Token business logic implementation
│   │   │   │   └── WorkoutService.java    # Workout business logic implementation
│   │   │   ├── repository/                # Data access layer
│   │   │   │   ├── UserRepository.java    # User data operations
│   │   │   │   ├── WorkoutRepository.java # Workout data operations
│   │   │   │   └── TokenRepository.java   # Token data operations
│   │   │   ├── model/                      # Entity models
│   │   │   │   ├── User.java               # User entity
│   │   │   │   ├── Workout.java            # Workout entity
│   │   │   │   ├── WorkoutEntry.java       # Workout entry entity
│   │   │   │   └── Token.java               # Token entity
│   │   │   ├── dto/                        # Data Transfer Objects
│   │   │   │   ├── RegisterUserRequest.java
│   │   │   │   ├── CreateTokenRequest.java
│   │   │   │   ├── CreateWorkoutRequest.java
│   │   │   │   ├── UpdateWorkoutRequest.java
│   │   │   │   └── AuthTokenResponse.java
│   │   │   ├── middleware/                 # HTTP middleware
│   │   │   │   └── AuthenticationInterceptor.java # Authentication middleware
│   │   │   ├── util/                       # Utility functions
│   │   │   │   ├── PasswordUtil.java       # Password hashing utilities
│   │   │   │   ├── TokenUtil.java          # Token generation utilities
│   │   │   │   └── JsonResponse.java        # JSON response helpers
│   │   │   └── exception/                   # Exception handling
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── UnauthorizedException.java
│   │   │       ├── ConflictException.java
│   │   │       └── ValidationException.java
│   │   └── resources/
│   │       ├── application.properties       # Application configuration
│   │       └── db/migration/                # Database migrations
│   │           ├── V1__create_users.sql
│   │           ├── V2__create_workouts.sql
│   │           ├── V3__create_workout_entries.sql
│   │           └── V4__create_tokens.sql
│   └── test/                                # Test files
└── docker-compose.yml                        # Docker services configuration
```

## Setup Instructions

### Prerequisites

- Java 17 or later
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL 18.1 (or use Docker)

### Environment Variables

Create a `.env` file in the root directory with the following variables:

```env
POSTGRES_HOST=localhost
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password
POSTGRES_DB=your_database_name
POSTGRES_PORT=5432
```

Alternatively, you can set these in `application.properties` or as system environment variables.

### Database Setup

1. Start the PostgreSQL database using Docker Compose:

```bash
docker-compose up -d db
```

This will start a PostgreSQL container on port 5432.

2. The application will automatically run migrations on startup using Flyway.

### Running the Application

1. Install dependencies and build the project:

```bash
mvn clean install
```

2. Run the application:

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/workout-app-1.0.0.jar
```

The server will start on port 8080 by default.

### Running Tests

A test database container is configured in `docker-compose.yml`:

```bash
docker-compose up -d test_db
```

## API Documentation

### Base URL

```
http://localhost:8080
```

### Authentication

Most endpoints require authentication using a Bearer token. Include the token in the Authorization header:

```
Authorization: Bearer <your_token>
```

### Endpoints

#### Health Check

**GET** `/health`

Check if the API is running.

**Response:**
```
Status is available
```

---

#### User Registration

**POST** `/users`

Create a new user account.

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepassword123",
  "bio": "Fitness enthusiast"
}
```

**Response:** `201 Created`
```json
{
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "bio": "Fitness enthusiast",
    "created_at": "2024-01-15T10:30:00",
    "updated_at": "2024-01-15T10:30:00"
  }
}
```

**Error Responses:**
- `400 Bad Request`: Invalid input (missing fields, invalid email format, etc.)
- `409 Conflict`: Username or email already exists
- `500 Internal Server Error`: Server error

---

#### Authentication

**POST** `/tokens/authentication`

Generate an authentication token.

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "securepassword123"
}
```

**Response:** `201 Created`
```json
{
  "auth_token": {
    "token": "ABC123XYZ...",
    "expiry": "2024-01-16T10:30:00"
  }
}
```

**Error Responses:**
- `400 Bad Request`: Invalid request payload
- `401 Unauthorized`: Invalid credentials
- `500 Internal Server Error`: Server error

---

#### Create Workout

**POST** `/workouts`

Create a new workout. **Requires authentication.**

**Request Body:**
```json
{
  "title": "Morning Cardio",
  "description": "30-minute morning run",
  "duration_minutes": 30,
  "calories_burned": 300,
  "entries": [
    {
      "exercise_name": "Running",
      "sets": 1,
      "duration_seconds": 1800,
      "weight": null,
      "notes": "Steady pace",
      "order_index": 0
    }
  ]
}
```

**Response:** `201 Created`
```json
{
  "workout": {
    "id": 1,
    "user_id": 1,
    "title": "Morning Cardio",
    "description": "30-minute morning run",
    "duration_minutes": 30,
    "calories_burned": 300,
    "entries": [
      {
        "id": 1,
        "exercise_name": "Running",
        "sets": 1,
        "reps": null,
        "duration_seconds": 1800,
        "weight": null,
        "notes": "Steady pace",
        "order_index": 0
      }
    ]
  }
}
```

**Error Responses:**
- `400 Bad Request`: Invalid input or not logged in
- `401 Unauthorized`: Missing or invalid token
- `500 Internal Server Error`: Server error

**Validation Rules:**
- `title`: Required, max 255 characters
- `duration_minutes`: Required, must be > 0
- `calories_burned`: Required, must be >= 0
- `entries`: Optional array of exercise entries
  - Each entry must have either `reps` OR `duration_seconds` (not both)

---

#### Get Workout

**GET** `/workouts/{id}`

Retrieve a workout by ID. **Requires authentication.**

**Response:** `200 OK`
```json
{
  "workout": {
    "id": 1,
    "user_id": 1,
    "title": "Morning Cardio",
    "description": "30-minute morning run",
    "duration_minutes": 30,
    "calories_burned": 300,
    "entries": [...]
  }
}
```

**Error Responses:**
- `400 Bad Request`: Invalid workout ID
- `401 Unauthorized`: Missing or invalid token
- `404 Not Found`: Workout not found
- `500 Internal Server Error`: Server error

---

#### Update Workout

**PUT** `/workouts/{id}`

Update a workout. **Requires authentication and ownership.**

**Request Body:** (all fields optional)
```json
{
  "title": "Updated Workout Title",
  "description": "Updated description",
  "duration_minutes": 45,
  "calories_burned": 400,
  "entries": [...]
}
```

**Response:** `200 OK`
```json
{
  "workout": {
    "id": 1,
    "user_id": 1,
    "title": "Updated Workout Title",
    ...
  }
}
```

**Error Responses:**
- `400 Bad Request`: Invalid input
- `401 Unauthorized`: Missing or invalid token
- `403 Forbidden`: Not authorized to update this workout
- `404 Not Found`: Workout not found
- `500 Internal Server Error`: Server error

**Note:** Updating entries will replace all existing entries with the new array.

---

#### Delete Workout

**DELETE** `/workouts/{id}`

Delete a workout. **Requires authentication and ownership.**

**Response:** `204 No Content`

**Error Responses:**
- `401 Unauthorized`: Missing or invalid token
- `403 Forbidden`: Not authorized to delete this workout
- `404 Not Found`: Workout not found
- `500 Internal Server Error`: Server error

---

## Data Models

### User
```java
{
  "id": Long,
  "username": String (max 50 chars, unique),
  "email": String (unique),
  "password_hash": String (not returned in JSON),
  "bio": String (optional),
  "created_at": LocalDateTime,
  "updated_at": LocalDateTime
}
```

### Workout
```java
{
  "id": Long,
  "user_id": Long,
  "title": String (max 255 chars),
  "description": String (optional),
  "duration_minutes": Integer (> 0),
  "calories_burned": Integer (>= 0),
  "entries": List<WorkoutEntry>
}
```

### WorkoutEntry
```java
{
  "id": Long,
  "exercise_name": String (max 255 chars),
  "sets": Integer (> 0),
  "reps": Integer (optional, mutually exclusive with duration_seconds),
  "duration_seconds": Integer (optional, mutually exclusive with reps),
  "weight": Double (optional, max 999.99),
  "notes": String (optional),
  "order_index": Integer
}
```

### Token
```java
{
  "token": String (plaintext, only returned on creation),
  "expiry": LocalDateTime,
  "hash": byte[] (not returned),
  "user_id": Long (not returned),
  "scope": String (not returned)
}
```

## Security Features

- **Password Hashing**: BCrypt with cost factor 12
- **Token Hashing**: SHA-256 hashing before database storage
- **Token Expiration**: 24-hour token lifetime
- **User Enumeration Prevention**: Authentication errors don't reveal if username exists
- **Authorization Checks**: Users can only modify their own resources
- **Input Validation**: Comprehensive validation on all endpoints
- **SQL Injection Prevention**: Parameterized queries via JPA/Hibernate

## Error Handling

The API uses consistent error responses:

```json
{
  "error": "Error message description"
}
```

Common HTTP status codes:
- `400 Bad Request`: Invalid input or malformed request
- `401 Unauthorized`: Authentication required or failed
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict (e.g., duplicate username)
- `500 Internal Server Error`: Server-side error

## Development

### Running Migrations Manually

Migrations are automatically run on application startup via Flyway. To manually run migrations:

```bash
mvn flyway:migrate
```

### Database Connection

The application uses Spring Data JPA with Hibernate for database operations. Connection pooling is handled automatically by HikariCP (included with Spring Boot).

### Logging

The application uses SLF4J with Logback (included with Spring Boot). Logs include timestamps and are written to stdout.

## Architecture

The application follows MVC (Model-View-Controller) architecture with clear separation of concerns:

- **Controllers**: Handle HTTP requests and responses, delegate to services
- **Services**: Contain business logic, validation, and authorization (implement service interfaces)
- **Repositories**: Data access layer using Spring Data JPA
- **Models**: JPA entities representing database tables
- **DTOs**: Data Transfer Objects for request/response with validation
- **Exception Handling**: Global exception handler with custom exceptions
- **Middleware**: Authentication interceptor for protected routes

### Design Patterns

- **Dependency Injection**: Spring's IoC container manages dependencies
- **Repository Pattern**: Abstract data access through interfaces
- **Service Layer Pattern**: Business logic separated from controllers
- **DTO Pattern**: Separate request/response objects from entities
- **Exception Handling Pattern**: Centralized error handling

## License

This project is for educational/practice purposes.

## Contributing

This is a practice project. Feel free to use it as a reference or starting point for your own projects.
