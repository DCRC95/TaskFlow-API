# TaskFlow API

A production-style **multi-user task and project management REST API** built with **Java 21, Spring Boot 3, PostgreSQL, JWT authentication, Flyway migrations, and Testcontainers**.

This project was designed as a **portfolio-quality backend service**, demonstrating clean architecture, security best practices, database migrations, background jobs, and integration testing with real infrastructure.

---

## Features

* **JWT Authentication & Authorisation**

  * Stateless authentication using Bearer tokens
  * Per-user resource ownership enforcement
  * Secure 404 masking to prevent data leakage

* **Projects & Tasks Management**

  * CRUD operations for projects
  * Tasks belong to projects (one-to-many relationship)
  * Task filtering by status and due date

* **Scheduled Overdue Detection**

  * Background job automatically marks tasks as `OVERDUE`
  * Uses Spring’s scheduling + transactional JPA updates

* **Persistence with PostgreSQL & JPA**

  * Spring Data JPA repositories
  * Hibernate ORM with validation
  * Clean entity mappings

* **Flyway Database Migrations**

  * Versioned schema migrations
  * Full control over database evolution
  * Safe, repeatable schema changes

* **Swagger / OpenAPI Documentation**

  * Live, interactive API documentation
  * JWT support directly inside Swagger UI

* **Integration Tests with Testcontainers**

  * Real PostgreSQL instance spun up in Docker
  * Flyway migrations applied automatically in tests
  * End-to-end testing of auth and secured endpoints

---

## Architecture Overview

The application follows a **layered architecture**, separating concerns clearly and enforcing clean boundaries:

```
┌──────────────┐
│   Client     │  (curl / Swagger UI / Frontend)
└──────┬───────┘
       │ HTTP (JSON + JWT)
┌──────▼───────┐
│ Controller   │  (@RestController)
│              │  - Request/response mapping
└──────┬───────┘
       │ calls
┌──────▼───────┐
│ Service      │  (@Service)
│              │  - Business logic
│              │  - Authorization & ownership checks
└──────┬───────┘
       │ uses
┌──────▼───────┐
│ Repository   │  (Spring Data JPA)
│              │  - Database abstraction
└──────┬───────┘
       │ ORM (Hibernate)
┌──────▼───────┐
│ PostgreSQL   │
└──────────────┘
```

This design ensures:

* Business logic is independent of HTTP and persistence
* Authorisation rules are enforced centrally
* Database access can be changed with minimal impact

---

## Tech Stack

* **Java 21**
* **Spring Boot 3.5.x**
* **Spring Security (JWT)**
* **Spring Data JPA (Hibernate)**
* **PostgreSQL 16**
* **Flyway**
* **Swagger / OpenAPI (springdoc)**
* **Testcontainers + JUnit 5**
* **Maven**

---

## Configuration

### Application Configuration (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskflow
    username: taskflow
    password: taskflow

security:
  jwt:
    secret: "CHANGE_ME_64_CHAR_MIN_SECRET"
    expiry-minutes: 60
```

---

## Authentication Flow

1. User registers or logs in
2. Server issues a JWT
3. Client sends:

```
Authorization: Bearer <JWT>
```

4. Security filter validates token
5. User identity is loaded into the SecurityContext

All protected endpoints require a valid token.

---

## API Documentation (Swagger)

Once the application is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

Swagger provides:

* A live list of all endpoints
* Request/response schemas generated from DTOs
* JWT authentication via the **Authorise** button
* Interactive testing of secured endpoints

### Example Swagger Flow

1. Register or log in to obtain a JWT
2. Click **Authorise** and paste the token
3. Call protected endpoints directly from the UI

---

## Scheduled Job: Overdue Tasks

A background job runs periodically to:

* Find tasks with `dueDate < today`
* Exclude tasks marked as `DONE`
* Automatically mark them as `OVERDUE`

This demonstrates:

* Scheduled background processing
* Transactional updates
* Time-based business rules

---

## Testing Strategy

### Integration Tests with Testcontainers

* PostgreSQL runs inside Docker during tests
* Spring Boot automatically connects via `@ServiceConnection`
* Flyway migrations run before each test

Run tests with:

```bash
./mvnw test
```

This ensures:

* The application boots correctly
* Database migrations are valid
* Security and endpoints work end-to-end

---

## Running the Application

### Prerequisites

* Java 21+
* Docker
* PostgreSQL (or Docker-based DB)

### Start the app

```bash
./mvnw spring-boot:run
```

---

## Example curl Commands

### Register a user

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"Password123!"}'
```

### Create a project (authenticated)

```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"name":"My Project","description":"Demo project"}'
```

### List projects

```bash
curl -X GET http://localhost:8080/api/v1/projects \
  -H "Authorization: Bearer <JWT>"
```

---

## Future Improvements

Planned enhancements that could be added:

* Role-based access control (ADMIN / USER)
* Refresh tokens for JWT authentication
* Pagination & sorting across all list endpoints
* Soft deletes and audit logging
* Event-based overdue notifications
* Containerized production deployment (Docker Compose / Kubernetes)

These improvements demonstrate how the architecture supports growth without major refactoring.

