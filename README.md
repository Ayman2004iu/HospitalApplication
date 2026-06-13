# Hospital Management System

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-orange?style=for-the-badge&logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-black?style=for-the-badge&logo=githubactions)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

## Overview

**Hospital Management System** is a backend RESTful application built with **Spring Boot** to manage core hospital workflows in a clean, modular, and secure way.

The system exposes secure APIs for handling:

- patients
- doctors
- clinics
- departments
- visits
- prescriptions
- prescription items
- medications
- invoices
- invoice items
- authentication

It follows a layered architecture and uses JWT-based security, DTO mapping, JPA/Hibernate persistence, Dockerized deployment, and automated tests.

### Key Highlights

- Layered Architecture: **Controller → Service → Repository**
- **JWT Authentication & Authorization**
- **Role-Based Access Control (RBAC)**
- **Automatic admin account initialization**
- **MapStruct** for DTO mapping
- **OpenAPI / Swagger UI** for API docs
- **Docker** and **Docker Compose**
- **GitHub Actions** CI pipeline
- **JUnit 5 + Mockito + MockMvc**
- **H2** in-memory database for testing

---

## Features

### Authentication & Authorization

- Register new users
- Login with JWT
- Stateless security
- BCrypt password hashing
- Role-based access for Admin and Doctor

### Patient Management

- Create patients
- Update patient data by national ID
- Retrieve a patient by national ID
- List patients with pagination

### Doctor Management

- Create doctor profiles
- Update doctor data
- Get doctor details
- List doctors with pagination

### Clinic & Department Management

- Create clinics and departments
- Update and delete them
- Retrieve a single record or paginated list

### Visit Management

- Create a visit
- Cancel a visit
- Close a visit
- Retrieve visits by patient national ID
- List visits with pagination

### Prescription Management

- Create prescriptions by doctors
- Update prescriptions
- Dispense prescriptions by admin
- Retrieve prescriptions by ID
- List prescriptions with pagination

### Prescription Item Management

- Create prescription items
- Update prescription items
- Retrieve prescription item details
- List prescription items with pagination

### Medication Management

- Create medications
- Retrieve medication details
- List medications with pagination

### Invoice & Billing

- Pay invoices linked to visits
- Retrieve invoice by visit ID
- Retrieve invoice by invoice ID
- List invoices with pagination

### Invoice Item Management

- Add invoice items
- Retrieve invoice items
- List invoice items
- Delete invoice items

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.5 |
| Security | Spring Security + JWT (JJWT) |
| Persistence | Spring Data JPA + Hibernate |
| Mapping | MapStruct |
| Database | MySQL 8 |
| Testing Database | H2 |
| Testing | JUnit 5, Mockito, MockMvc |
| Build Tool | Maven |
| API Docs | OpenAPI / Swagger UI |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |

---

## Architecture

The application follows a strict layered architecture:

```text
Client
  ↓
Controller Layer
  ↓
Service Layer
  ↓
Repository Layer
  ↓
MySQL Database
```

Authentication and authorization requests pass through Spring Security and JWT filters before reaching protected endpoints.

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com/aymanibrahim/hospital
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       │   ├── request
│   │       │   └── response
│   │       ├── entity
│   │       ├── enums
│   │       ├── exception
│   │       ├── mapper
│   │       ├── repository
│   │       ├── security
│   │       └── service
│   └── resources
│       ├── application.yml
│       ├── application-test.yml
│       └── application-local-sample.yml
└── test
    └── java
        └── com/aymanibrahim/hospital
            ├── integrationTest
            └── service/impl
```

---

## API Documentation

Swagger UI is available after running the application:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

---

## API Endpoints

### Authentication

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and receive a JWT token |

### Patients

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/patients` | ADMIN | Create patient |
| GET | `/api/patients` | ADMIN | List patients |
| GET | `/api/patients/{nationalId}` | ADMIN | Get patient by national ID |
| PUT | `/api/patients/{nationalId}` | ADMIN | Update patient by national ID |

### Doctors

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/doctors` | ADMIN | Create doctor |
| GET | `/api/doctors` | ADMIN | List doctors |
| GET | `/api/doctors/{id}` | ADMIN | Get doctor by ID |
| PUT | `/api/doctors/{id}` | ADMIN | Update doctor |
| DELETE | `/api/doctors/{id}` | ADMIN | Delete doctor |
### Clinics

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/clinics` | ADMIN | Create clinic |
| GET | `/api/clinics` | ADMIN | List clinics |
| GET | `/api/clinics/{id}` | ADMIN | Get clinic by ID |
| PUT | `/api/clinics/{id}` | ADMIN | Update clinic |
| DELETE | `/api/clinics/{id}` | ADMIN | Delete clinic |

### Departments

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/departments` | ADMIN | Create department |
| GET | `/api/departments` | ADMIN | List departments |
| GET | `/api/departments/{id}` | ADMIN | Get department by ID |
| PUT | `/api/departments/{id}` | ADMIN | Update department |
| DELETE | `/api/departments/{id}` | ADMIN | Delete department |

### Medications

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/medications` | ADMIN | Create medication |
| GET | `/api/medications` | ADMIN | List medications |
| GET | `/api/medications/{id}` | ADMIN | Get medication by ID |

### Visits

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/visits` | ADMIN | Create visit |
| PATCH | `/api/visits/{id}/cancel` | ADMIN | Cancel visit |
| PATCH | `/api/visits/{id}/close` | ADMIN | Close visit |
| GET | `/api/visits` | ADMIN | List visits |
| GET | `/api/visits/{nationalId}` | ADMIN / DOCTOR | Get visits by patient national ID |

### Prescriptions

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/prescriptions` | DOCTOR | Create prescription |
| PUT | `/api/prescriptions/{id}` | DOCTOR | Update prescription |
| POST | `/api/prescriptions/{id}/dispense` | ADMIN | Dispense prescription |
| GET | `/api/prescriptions` | ADMIN | List prescriptions |
| GET | `/api/prescriptions/{id}` | ADMIN / DOCTOR | Get prescription by ID |

### Prescription Items

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/prescription-items` | DOCTOR | Add prescription item |
| PUT | `/api/prescription-items/{id}` | DOCTOR | Update prescription item |
| GET | `/api/prescription-items` | ADMIN | List prescription items |
| GET | `/api/prescription-items/{id}` | ADMIN / DOCTOR | Get prescription item by ID |

### Invoices

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/invoices/visit/{visitId}/pay` | ADMIN | Pay invoice for a visit |
| GET | `/api/invoices/visit/{visitId}` | ADMIN | Get invoice by visit ID |
| GET | `/api/invoices/{id}` | ADMIN | Get invoice by invoice ID |
| GET | `/api/invoices` | ADMIN | List invoices |

### Invoice Items

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/invoice-items` | ADMIN | Add invoice item |
| GET | `/api/invoice-items` | ADMIN | List invoice items |
| GET | `/api/invoice-items/{id}` | ADMIN | Get invoice item by ID |
| DELETE | `/api/invoice-items/{id}` | ADMIN | Delete invoice item |

---

## Request & Response Examples

The snippets below are **representative of the current API shape** and keep sensitive values out of the README.

### 1) Login

**Request**

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@example.com",
  "password": "<your-admin-password>"
}
```

**Response**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "email": "admin@example.com"
}
```

### 2) Register

**Request**

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "New User"
}
```

**Response**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "newuser",
  "email": "newuser@example.com"
}
```

### 3) Create Patient

**Request**

```http
POST /api/patients
Authorization: Bearer <admin-token>
Content-Type: application/json
```

```json
{
  "nationalId": "12345",
  "dob": "1985-03-15",
  "gender": "Female",
  "address": "Alexandria",
  "name": "Fatma Hassan",
  "phone": "01087654321"
}
```

**Response**

```json
{
  "nationalId": "12345",
  "name": "Fatma Hassan"
}
```

### 4) Create Doctor

**Request**

```http
POST /api/doctors
Authorization: Bearer <admin-token>
Content-Type: application/json
```

```json
{
  "name": "Dr Khalid",
  "email": "dr.khalid@example.com",
  "phone": "01011112222",
  "password": "pass123",
  "specialization": "Surgery",
  "licenseNumber": "LIC-K-001",
  "departmentId": 1,
  "clinicId": 2
}
```

**Response**

```json
{
  "name": "Dr Khalid",
  "email": "dr.khalid@example.com"
}
```

### 5) Create Visit

**Request**

```http
POST /api/visits
Authorization: Bearer <admin-token>
Content-Type: application/json
```

```json
{
  "nationalId": "11111111",
  "doctorId": 1,
  "departmentId": 2,
  "clinicId": 3
}
```

**Response**

```json
{
  "status": "OPEN",
  "nationalId": "11111111",
  "invoiceId": 55
}
```

### 6) Create Prescription

**Request**

```http
POST /api/prescriptions
Authorization: Bearer <doctor-token>
Content-Type: application/json
```

```json
{
  "patientId": 1,
  "visitId": 10,
  "notes": "Take after meals",
  "items": [
    {
      "medicationId": 7,
      "dosage": "1 tablet",
      "frequency": "twice daily",
      "durationDays": 7,
      "quantity": 14
    }
  ]
}
```

**Response**

```json
{
  "status": "PENDING",
  "notes": "Take after meals",
  "patientId": 1,
  "visitId": 10
}
```

### 7) Pay Invoice

**Request**

```http
POST /api/invoices/visit/10/pay
Authorization: Bearer <admin-token>
Content-Type: application/json
```

```json
{
  "visitId": 10,
  "amount": 150
}
```

**Response**

```json
{
  "total": 150,
  "paymentStatus": "PAID"
}
```

### 8) Structured Error Response

```json
{
  "statusCode": 400,
  "error": "Bad Request",
  "message": "Validation failed for the request body",
  "path": "/api/patients",
  "timestamp": "2026-06-10T12:15:00"
}
```

### 9) Paginated Response

```json
{
  "content": [
    {
      "id": 1,
      "name": "Radiology"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "empty": false
}
```

---

## Authorization Matrix

The matrix below reflects the effective authorization enforced by the current Spring Security configuration and controller-level `@PreAuthorize` rules.

| Endpoint Group | PUBLIC | DOCTOR | ADMIN | Notes |
|---|---:|---:|---:|---|
| Authentication | ✅ | ❌ | ❌ | Register and login only |
| Patients | ❌ | ❌ | ✅ | Admin only |
| Doctors | ❌ | ❌ | ✅ | Admin only |
| Clinics | ❌ | ❌ | ✅ | Admin only |
| Departments | ❌ | ❌ | ✅ | Admin only |
| Medications | ❌ | ❌ | ✅ | Admin only |
| Visits | ❌ | ✅ | ✅ | Doctors can read by patient; admin manages all |
| Prescriptions | ❌ | ✅ | ✅ | Doctors create/update, admin can dispense/list |
| Prescription Items | ❌ | ✅ | ✅ | Doctors create/update, admin can list |
| Invoices | ❌ | ❌ | ✅ | Admin only |
| Invoice Items | ❌ | ❌ | ✅ | Admin only |

### Role Summary

| Role | Description |
|---|---|
| PUBLIC | Authentication and docs endpoints only |
| DOCTOR | Manage prescriptions and read patient visits |
| ADMIN | Full system access |

---

## HTTP Status Codes

| Status Code | Meaning |
|---|---|
| 200 OK | Successful read or update operation |
| 201 Created | Resource created successfully |
| 204 No Content | Successful delete or cancel action |
| 400 Bad Request | Validation failure or business rule violation |
| 401 Unauthorized | Missing or invalid JWT token |
| 403 Forbidden | Authenticated but not allowed |
| 404 Not Found | Requested resource does not exist |
| 500 Internal Server Error | Unexpected server-side failure |

---

## Standard Error Response

All API errors follow a consistent JSON structure returned by `ApiError`.

```json
{
  "statusCode": 400,
  "error": "Bad Request",
  "message": "Patient with national ID 12345 already exists",
  "path": "/api/patients",
  "timestamp": "2026-06-10T12:15:00"
}
```

### Example Unauthorized Response

```json
{
  "statusCode": 401,
  "error": "Unauthorized",
  "message": "Authentication required: please provide a valid JWT token",
  "path": "/api/patients",
  "timestamp": "2026-06-10T12:15:00"
}
```

### Example Forbidden Response

```json
{
  "statusCode": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/doctors",
  "timestamp": "2026-06-10T12:15:00"
}
```

---

## Pagination Example

Most listing endpoints return a standard Spring Data `Page` response.

### Request

```http
GET /api/patients?page=0&size=10
Authorization: Bearer <admin-token>
```

### Response

```json
{
  "content": [
    {
      "id": 1,
      "nationalId": "12345",
      "name": "Fatma Hassan",
      "phone": "01087654321"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "first": true,
  "last": true,
  "numberOfElements": 1,
  "empty": false
}
```

## Configuration

The application reads sensitive settings from environment variables.

### Required Environment Variables

| Variable | Purpose |
|---|---|
| `SPRING_DATASOURCE_URL` | MySQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `ADMIN_EMAIL` | Seed admin email |
| `ADMIN_PASSWORD` | Seed admin password |
| `ADMIN_USERNAME` | Seed admin username |
| `APP_CORS_ALLOWED_ORIGINS` | Allowed frontend origins |

### Local Development

Copy `application-local-sample.yml` to `application-local.yml` and fill in your local values or environment variables for local development.

### Example `.env`

```env
MYSQL_ROOT_PASSWORD=your-password

SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/hospital_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your-password

JWT_SECRET=your-long-secret-min-32-chars

ADMIN_EMAIL=admin@example.com
ADMIN_USERNAME=admin
ADMIN_PASSWORD=change-me

APP_CORS_ALLOWED_ORIGINS=http://localhost:3000
```

---

## Running Locally

### Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8

### 1. Clone the repository

```bash
git clone https://github.com/Ayman2004iu/HospitalApplication.git
cd HospitalApplication
```

### 2. Create local configuration

Copy the sample file:

```bash
cp src/main/resources/application-local-sample.yml src/main/resources/application-local.yml
```

Then update `application-local.yml` with your local values:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: update

jwt:
  secret: your_secret_key_at_least_32_characters_long

ADMIN_EMAIL: admin@example.com
ADMIN_PASSWORD: admin-password
ADMIN_USERNAME: admin

app:
  cors:
    allowed-origins: http://localhost:3000
```

`application-local.yml` is in `.gitignore` and will never be committed.

### 3. Build the project

```bash
.\mvnw clean package -DskipTests
```

### 4. Run the application

```bash
java -jar target/HospitalApplication-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Application available at:

```text
http://localhost:8080
```

---

## Running with Docker

### Prerequisites

- Docker
- Docker Compose

### 1. Create `.env` file

Copy the example file:

```bash
cp example.env .env
```

Then fill in your values:

```env
MYSQL_ROOT_PASSWORD=yourpassword

SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=yourpassword

JWT_SECRET=your-secret-key-at-least-32-characters-long

ADMIN_EMAIL=admin@example.com
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin-password

APP_CORS_ALLOWED_ORIGINS=http://localhost:3000
```

`.env` is in `.gitignore` and will never be committed. Use `example.env` as a reference.

### 2. Build and start containers

```bash
docker compose up --build -d
```

First startup may take a few minutes while MySQL initializes and passes its healthcheck.

### 3. Stop containers

```bash
docker compose down
```

Application available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Testing

Tests use an H2 in-memory database — no MySQL or Docker required.

```bash
.\mvnw test "-Dspring.profiles.active=test"
```

The test profile is isolated and does not require the local MySQL instance.

---
## CI/CD

A GitHub Actions workflow is included to:

- build the project
- run the test suite
- generate coverage reports with JaCoCo
- upload build artifacts

---

## Security

- JWT authentication
- BCrypt password hashing
- Role-based authorization with `@PreAuthorize`
- Stateless session management
- Global JSON error responses
- Environment-based secret management

---

## Future Improvements

- Frontend application with React or Angular
- Email notifications
- Appointment scheduling module
- Cloud deployment
- Monitoring and logging dashboard

---

## Author

**Ayman Ibrahim Seddik**

- Email: ayman.ibrahim.seddik@gmail.com
- GitHub: https://github.com/Ayman2004iu
- LinkedIn: https://www.linkedin.com/in/ayman-ibrahim-dev/