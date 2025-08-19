# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
```bash
# Run application with environment-specific configurations
./scripts/start.sh [local|dev|prod]

# Stop and remove containers with volumes (for schema changes)
docker-compose down -v
docker-compose up --build
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.example.todo.api.GoalApiTest"
```

### Build
```bash
# Build the application
./gradlew build

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build
```

### Code Formatting
```bash
# Format all Java files using Google Java Format
./gradlew format

# Alternative: Use CLI directly
find src -name "*.java" -exec google-java-format --replace {} \;
```

## Code Architecture

### Domain Structure
This is a Spring Boot 3.4 application with Java 21 using a layered architecture organized by domain:
- `user/` - User management and authentication
- `goal/` - Goal management 
- `milestone/` - Goal milestones
- `quest/` - Individual tasks/quests
- `conversation/` - Chat conversations
- `message/` - Conversation messages with OpenAI integration
- `openai/` - OpenAI service integration
- `auth/` - Authentication endpoints
- `health/` - Health check endpoint

Each domain follows the pattern:
- Entity class (JPA entity)
- Repository interface (Spring Data JPA)
- Service class (business logic)
- Controller class (REST endpoints)
- `dto/` package (data transfer objects)
- `enums/` package (domain enums)

### Security
- JWT-based authentication using JJWT library
- Spring Security configuration in `common/config/SecurityConfig.java`
- JWT token provider and authentication filter in `common/security/`
- Password encoding with BCrypt
- Public endpoints: `/auth/**`, `/health`
- All other endpoints require authentication

### Database
- PostgreSQL with UUID v7 extension
- Database initialization scripts in `db/` directory
- JPA entities with soft delete support (deleted_at timestamp)
- Schema includes hierarchical structure: User → Goals → Milestones → Quests
- Conversation and Message entities support OpenAI chat integration

### OpenAI Integration
- Uses OpenAI Java client library v2.20.1
- Service handles chat completions and title generation
- Messages support different content types (text, tool)
- Integration points in conversation and message management

### Configuration
- Environment-specific YAML files in `src/main/resources/`
- Docker Compose configurations for different environments
- JWT expiration configured to 7 days (604800000ms)

## Code Style

Uses Google Java Format for consistent code formatting. For VS Code:
1. Install google-java-format via Homebrew: `brew install google-java-format`
2. Install VS Code extension: google-java-format (by ilkka)
3. Configure .vscode/settings.json with executable path and format on save

## Docker Development

The application runs in Docker containers:
- Main application container (Spring Boot app)
- PostgreSQL database container
- Different compose files for local/dev/prod environments
- Database volume persistence for development