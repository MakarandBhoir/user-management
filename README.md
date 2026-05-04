# User Management System

A demo Spring Boot application for security training and CI/CD pipeline demonstrations.

> **WARNING:** This application contains **intentional vulnerabilities and technical debt** for educational purposes.
> **DO NOT deploy to production.**

---

## Tech Stack

| Component | Technology |
|---|---|
| Framework | Spring Boot 3.2.5 |
| Language | Java 17 |
| Build | Maven |
| Database | H2 (in-memory) |
| Security | Spring Security (misconfigured) |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Monitoring | Spring Actuator |

---

## How to Run Locally

### Prerequisites
- Java 17+
- Maven 3.8+

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/app.jar
```

### Run on a custom port
```bash
PORT=9090 java -jar target/app.jar
# or on Windows:
set PORT=9090 && java -jar target/app.jar
```

### Run with Docker
```bash
# Build image
docker build -t user-management:latest .

# Run container
docker run -p 8080:8080 user-management:latest

# Run on custom port
docker run -p 9090:8080 -e PORT=8080 user-management:latest
```

---

## Available Endpoints

| Method | URL | Description |
|---|---|---|
| POST | `/users` | Create a user |
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get user by ID |
| PUT | `/users/{id}` | Update user |
| DELETE | `/users/{id}` | Delete user |
| GET | `/admin/users/search?name=Alice` | Search by name (SQL Injection) |
| GET | `/admin/greet?message=Hello` | Greet (XSS) |
| GET | `/admin/users/dump` | Dump all users with passwords |
| GET | `/admin/users/slow` | Slow response (5s delay) |
| GET | `/admin/info` | App info with hardcoded credentials |
| GET | `/h2-console` | H2 database console |
| GET | `/swagger-ui.html` | Swagger UI |
| GET | `/api-docs` | OpenAPI JSON |
| GET | `/actuator` | Actuator endpoints |

### Seed Users (from data.sql)

| Name | Email | Password | Role |
|---|---|---|---|
| Alice Admin | alice@example.com | admin123 | ADMIN |
| Bob User | bob@example.com | password | USER |
| Charlie Dev | charlie@example.com | charlie99 | USER |
| Diana Ops | diana@example.com | diana2024 | USER |
| Eve Hacker | eve@example.com | letmein | USER |

### Spring Security In-Memory Users

| Username | Password | Role |
|---|---|---|
| admin | admin123 | ADMIN, USER |
| user | password | USER |

---

## Known Vulnerabilities (Intentional)

### 1. SQL Injection — `/admin/users/search`
- **Location:** `UserService.searchUsersByName()`, `AdminController.searchUsers()`
- **Description:** The `name` query parameter is concatenated directly into a native SQL string.
- **Exploit:** `GET /admin/users/search?name=' OR '1'='1`
- **Fix:** Use parameterized queries or Spring Data method queries.

### 2. Cross-Site Scripting (XSS) — `/admin/greet`
- **Location:** `AdminController.greet()`
- **Description:** The `message` query parameter is reflected into an HTML response without encoding.
- **Exploit:** `GET /admin/greet?message=<script>alert('XSS')</script>`
- **Fix:** Use `HtmlUtils.htmlEscape()` or return JSON responses.

### 3. Sensitive Data Exposure — `/admin/users/dump`
- **Location:** `AdminController.dumpAllUsers()`
- **Description:** Returns all users including plain-text passwords with no authentication required.
- **Fix:** Require `ROLE_ADMIN`, exclude password field, add audit logging.

### 4. Hardcoded Credentials
- **Location:** `SecurityConfig.userDetailsService()`, `AdminController.appInfo()`
- **Description:** Credentials `admin/admin123` and `user/password` are hardcoded in source code.
- **Fix:** Load credentials from environment variables or a secret manager (e.g., Azure Key Vault).

### 5. No Password Encoding
- **Location:** `SecurityConfig`, `UserService.createUser()`, `data.sql`
- **Description:** Passwords are stored and compared as plain text using `{noop}` prefix.
- **Fix:** Use `BCryptPasswordEncoder`.

### 6. CSRF Disabled
- **Location:** `SecurityConfig.filterChain()`
- **Description:** CSRF protection is disabled, making the app vulnerable to cross-site request forgery.
- **Fix:** Enable CSRF for browser-facing endpoints or use stateless JWT authentication.

### 7. All Endpoints Unprotected
- **Location:** `SecurityConfig.filterChain()`
- **Description:** `.anyRequest().permitAll()` allows unauthenticated access to all endpoints.
- **Fix:** Restrict endpoints by role: `/admin/**` → `ROLE_ADMIN`, `/users/**` → authenticated.

### 8. Actuator Fully Exposed
- **Location:** `application.properties`
- **Description:** All actuator endpoints are exposed (`management.endpoints.web.exposure.include=*`).
- **Fix:** Expose only `health` and `info` in production, secure with authentication.

---

## Technical Debt Areas

| Area | Location | Description |
|---|---|---|
| Field Injection | `UserService`, `UserController`, `AdminController` | Use constructor injection instead |
| Duplicate Validation | `UserService.createUser()`, `UserService.updateUser()` | Extract to a shared `validateUser()` method |
| Long Methods | `UserService.createUser()`, `UserService.updateUser()` | Refactor into smaller methods |
| No Exception Handling | `UserController` | Add `@ControllerAdvice` global handler |
| No Pagination | `UserService.getAllUsers()` | Add `Pageable` support |
| Magic Numbers | `UserService.getAllUsersSlowly()` | Externalize sleep time to config |
| No DTO Layer | All controllers | Use request/response DTOs, not entity directly |
| TODO Comments | Throughout codebase | Multiple TODOs marking known debt |

---

## CI/CD

The project is CI/CD ready. The Maven build produces a single executable JAR:

```bash
mvn clean package        # Produces target/app.jar
java -jar target/app.jar # Runs the application
```

Docker multi-stage build produces a minimal JRE image (~200MB).

---

## License

MIT — For educational and demo use only.
