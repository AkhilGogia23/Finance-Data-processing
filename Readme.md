# Finance Data Processing and Access Control Backend

## 1. Project Summary

This project is a Spring Boot backend for a finance dashboard system.

It supports:
- User and role management
- Financial record CRUD and filtering
- Dashboard summary analytics
- Role-based access control
- Validation and centralized exception handling

Tech stack:
- Java + Spring Boot
- Spring Security
- Spring Data JPA + Hibernate
- PostgreSQL (runtime)
- H2 (test runtime)

## 2. Backend Flow

Request lifecycle:
1. Client sends request with header X-USER (email).
2. RoleFilter validates user identity and status.
3. Spring Security sets Authentication and authorities.
4. Method-level @PreAuthorize checks required role.
5. Controller delegates to service layer.
6. Service executes business logic and persistence operations.
7. Exceptions are converted to API responses.

## 3. Authentication and Access Control

Authentication model:
- Header-based authentication through X-USER.

Role model:
- ADMIN
- ANALYST
- VIEWER

Security checks (RoleFilter):
- Missing X-USER -> 401
- Unknown user -> 401
- Deleted user -> 404
- Inactive user -> 403
- Invalid user configuration -> 403

Authorization model:
- Enforced with @PreAuthorize on controller methods.
- VIEWER can access dashboard summary only (no /records access).

## 4. Data Model

Users:
- id (Long)
- name (String)
- email (String, unique)
- role (ADMIN | ANALYST | VIEWER, mandatory)
- status (ACTIVE | INACTIVE, mandatory; defaults to ACTIVE)
- deleted (boolean, soft delete)

FinancialRecord:
- id (Long)
- amount (Double)
- type (INCOME | EXPENSE)
- category (String)
- date (LocalDate)
- notes (String)
- deleted (boolean, soft delete)
- user (ManyToOne owner)

RecordDto:
- amount: required, positive
- type: required
- userEmail: required
- category: optional
- notes: optional

## 5. API Base

Base URL:
- http://localhost:8080

Route groups:
- /users
- /records
- /dashboard

## 6. API Endpoints

### 6.1 User APIs

Create user:
- POST /users
- Access: ADMIN
- Header: X-USER

Update role:
- PUT /users/{id}/role?role=ADMIN
- Access: ADMIN
- Header: X-USER

List active users:
- GET /users
- Access: ADMIN, ANALYST
- Header: X-USER
- Behavior: returns users with deleted=false and status=ACTIVE.

List inactive users:
- GET /users/inactive
- Access: ADMIN, ANALYST
- Header: X-USER
- Behavior: returns users with deleted=false and status=INACTIVE.

List deleted users:
- GET /users/deleted
- Access: ADMIN, ANALYST
- Header: X-USER
- Behavior: returns users with deleted=true.

Deactivate user:
- PUT /users/{id}/deactivate
- Access: ADMIN
- Header: X-USER
- Behavior: sets status=INACTIVE only (does not soft delete).

Reactivate user:
- PUT /users/{id}/reactivate
- Access: ADMIN
- Header: X-USER
- Behavior: sets status=ACTIVE for non-deleted users.

Soft delete user:
- DELETE /users/{id}
- Access: ADMIN
- Header: X-USER
- Behavior: sets deleted=true and status=INACTIVE.

### 6.2 Financial Record APIs

Create record:
- POST /records
- Access: ADMIN
- Header: X-USER
- Body fields: amount, type, userEmail, category, notes
- Behavior: creates record for userEmail.
- Rule: ADMIN can create only for users who are ACTIVE and not soft-deleted.

Example body:
{
  "amount": 1500,
  "type": "INCOME",
  "userEmail": "employee@example.com",
  "category": "SALARY",
  "notes": "Monthly salary"
}

List records:
- GET /records
- Access: ADMIN, ANALYST
- Header: X-USER
- Query params (optional):
  - userEmail
- Behavior:
  - With userEmail: returns that user's non-deleted records.
  - Without userEmail: returns all non-deleted records across users.

Filter records (current authenticated user):
- GET /records/filter
- Access: ADMIN, ANALYST
- Header: X-USER
- Query params (optional):
  - type
  - category
  - startDate (yyyy-MM-dd)
  - endDate (yyyy-MM-dd)

Update record:
- PUT /records/{id}
- Access: ADMIN
- Header: X-USER
- Behavior: updates any non-deleted record.
- If record is soft-deleted: returns 404.

Soft delete record:
- DELETE /records/{id}
- Access: ADMIN
- Header: X-USER
- Behavior: sets deleted=true.

### 6.3 Dashboard APIs

Summary:
- GET /dashboard/summary
- Access: ADMIN, ANALYST, VIEWER
- Header: X-USER
- Query param: type (optional, comma-separated totalIncome,totalExpense,netBalance)
- Behavior: returns global totals across all non-deleted records.

Response keys:
- totalIncome
- totalExpense
- netBalance

## 7. Error Handling

Standard error format:
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "..."
}

Mapped exceptions:
- MethodArgumentNotValidException -> 400
- ResourceNotFoundException -> 404
- BadRequestException -> 400
- DataIntegrityViolationException -> 400
- IllegalArgumentException -> 400
- AuthenticationException -> 401
- AccessDeniedException -> 403
- Exception -> 500

Note:
- RoleFilter writes some authentication failures as plain text responses.

## 8. Assignment Requirement Mapping

1. User and role management:
- Implemented through /users create/list/update/deactivate/reactivate/soft-delete APIs.

2. Financial record management:
- Implemented through /records create/list/filter/update/soft-delete APIs.

3. Dashboard summary APIs:
- Implemented through /dashboard/summary with global totals.

4. Access control logic:
- Implemented through RoleFilter + @PreAuthorize role checks.

5. Validation and error handling:
- DTO validation and centralized exception mapping implemented.

6. Data persistence:
- PostgreSQL with JPA/Hibernate.

## 9. Setup for Evaluation

Set environment variables before run:
- DB_URL
- DB_USER
- DB_PASSWORD

Example Windows cmd:
- set DB_URL=jdbc:postgresql://localhost:5432/finance
- set DB_USER=postgres
- set DB_PASSWORD=your_password

Run:
- mvnw spring-boot:run

Important:
- Ensure at least one ACTIVE ADMIN exists in the users table.

## 10. Assumptions and Tradeoffs

- Authentication uses X-USER header for assignment scope.
- Role authority is derived server-side from DB.
- Records and users use soft deletion for recoverability/history.
- Deactivated users cannot authenticate or access protected APIs.
- Soft-deleted users cannot be restored.
- VIEWER has dashboard-only access.

