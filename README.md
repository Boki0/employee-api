# Employee API

Simple Spring Boot API for employees.

The application supports employee creation, fetching employees, salary update, salary analytics and audit logs.

Production API:

```text
https://employee-api-tok8.onrender.com/api
```

Base URL:

```text
https://employee-api-tok8.onrender.com
```

Note: the app is deployed on a free Render instance. If it was not used for some time, the first request can be slower because the service needs to wake up.

## Tech stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Docker / Docker Compose
- Render for deployment
- Neon PostgreSQL for production database
- Postman for testing

## How it is organized

The project uses a simple layered structure:

```text
controller -> service -> repository -> database
```

Main idea:

- controller receives HTTP requests
- service contains the main business logic
- repository communicates with PostgreSQL
- DTO classes are used for request and response data
- audit log stores important actions, for example employee creation and salary update

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api` | API overview page |
| POST | `/api/employees` | Create employee |
| GET | `/api/employees` | Get all employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| GET | `/api/employees?department=QA` | Filter employees by department |
| GET | `/api/employees?query=test` | Search employees |
| PATCH | `/api/employees/{id}/salary` | Update employee salary |
| GET | `/api/employees/analytics/salary` | Salary analytics |
| GET | `/api/audit-logs` | Get audit logs |

## Run locally

### 1. Clone project

```bash
git clone https://github.com/Boki0/employee-api.git
cd employee-api
```

### 2. Start local database

```bash
docker compose up -d
```

This starts:

- PostgreSQL on `localhost:5433`
- Adminer on `http://localhost:8081`

### 3. Start application

```bash
./mvnw spring-boot:run
```

Or run `EmployeeApiApplication` from IntelliJ.

Local API:

```text
http://localhost:8080/api
```

Check employees endpoint:

```text
http://localhost:8080/api/employees
```

If the database is empty, the response should be:

```json
[]
```

## Local database

Default local database settings:

```text
Database: employee_db
Username: bojan
Password: bojan
Port: 5433
```

Adminer login:

```text
System: PostgreSQL
Server: postgres
Username: bojan
Password: bojan
Database: employee_db
```

Adminer URL:

```text
http://localhost:8081
```

Stop local containers:

```bash
docker compose down
```

Remove local database data too:

```bash
docker compose down -v
```

## Test with Postman

The same Postman collection can be used for local and production testing.

For local testing set:

```text
baseUrl = http://localhost:8080
```

For production testing set:

```text
baseUrl = https://employee-api-tok8.onrender.com
```

Recommended test flow:

1. Create employee
2. Get all employees
3. Get employee by ID
4. Try to create duplicate employee and expect `409 Conflict`
5. Update salary
6. Get salary analytics
7. Get audit logs

## Example create employee request

```json
{
  "employeeCode": "EMP001",
  "firstName": "Marko",
  "lastName": "Markovic",
  "department": "QA",
  "salary": 120000
}
```

## Example update salary request

```json
{
  "newSalary": 150000
}
```

## Production

The backend is deployed on Render.

The production database is Neon PostgreSQL.

Render uses environment variables for database configuration, so database password and connection data are not stored in the repository.

After a push to the main branch, Render can automatically deploy the latest version.

To check production:

```text
https://employee-api-tok8.onrender.com/api
```

To check if employees endpoint works:

```text
https://employee-api-tok8.onrender.com/api/employees
```

## Status codes

| Status | Meaning |
|---|---|
| 200 | Request successful |
| 201 | Employee created |
| 400 | Invalid request |
| 404 | Resource not found |
| 409 | Duplicate employee |
| 500 | Server error |
