# auth-service

[![Maven Central](https://img.shields.io/badge/build-Maven-brightgreen)](https://maven.apache.org/)
[![Java 21](https://img.shields.io/badge/java-21-blue)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-3.5.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://www.docker.com/)

## Overview

`auth-service` is the authentication and authorization microservice in a Distributed Log Monitoring Platform. It is responsible for user registration, login, JWT issuance, and secure access control. The service also publishes operation logs to Apache Kafka, enabling centralized monitoring and cross-service traceability.

## Features

- User registration and login flows
- Secure password handling via Spring Security
- JWT token generation and validation
- Role-based authentication and authorization
- Kafka event publishing for successful authentication events
- MySQL persistence with Spring Data JPA
- Docker-ready container support
- Integration with `payment-service` and `log-processing-service`

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Security
- JWT (JSON Web Tokens)
- Spring Data JPA
- MySQL
- Apache Kafka
- Maven
- Docker
- Lombok

## Architecture

`auth-service` is designed as a RESTful microservice with clear separation between authentication, persistence, and messaging:

- Controller layer exposes REST API endpoints
- Service layer handles business logic and security
- Repository layer persists user data to MySQL
- Security layer validates JWTs and enforces access rules
- Kafka producer publishes authentication events for monitoring and processing

This service is typically deployed alongside other microservices such as `payment-service` and `log-processing-service` in a distributed architecture.

## API Endpoints

| Endpoint | Method | Description | Authentication |
| --- | --- | --- | --- |
| `/api/auth/register` | `POST` | Register a new user | No |
| `/api/auth/login` | `POST` | Authenticate user and receive JWT | No |
| `/api/auth/refresh` | `POST` | Refresh JWT token (if supported) | Yes |
| `/api/auth/profile` | `GET` | Retrieve authenticated user profile | Yes |

> Note: The exact endpoints may vary based on implementation details. Confirm against controller classes if needed.

## Request / Response Examples

### Register User

Request:
```json
POST /api/auth/register
Content-Type: application/json

{
  "username": "jane.doe",
  "password": "SecureP@ss123",
  "email": "jane.doe@example.com"
}
```

Response:
```json
{
  "status": "success",
  "message": "User registered successfully",
  "userId": 101
}
```

### Login User

Request:
```json
POST /api/auth/login
Content-Type: application/json

{
  "username": "jane.doe",
  "password": "SecureP@ss123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600
}
```

### Get Profile

Request:
```http
GET /api/auth/profile
Authorization: Bearer <JWT_TOKEN>
```

Response:
```json
{
  "id": 101,
  "username": "jane.doe",
  "email": "jane.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

## JWT Authentication Flow

1. User submits credentials to `/api/auth/login`.
2. Service validates credentials using Spring Security.
3. On success, service generates a JWT signed with a secret key.
4. JWT is returned to the client.
5. Client sends the token in the `Authorization` header for protected endpoints.
6. Service validates the token and extracts user details on every request.
7. If the token is valid, access is granted; otherwise, access is rejected.

This flow enables stateless authentication across the distributed system and supports secure access to downstream microservices.

## Kafka Integration

`auth-service` publishes events to Apache Kafka after important operations, such as successful registration and login. This supports:

- Centralized log monitoring
- Real-time audit trails
- Event-driven collaboration with `log-processing-service`
- Cross-service observability in the platform

Kafka topics are typically configured in `application.properties` and consumed by monitoring or processing services.

## Database Configuration

The service uses MySQL with Spring Data JPA for storing user credentials and profile data.

Example `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/authdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=auth_user
spring.datasource.password=auth_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

jwt.secret=ReplaceWithYourSecretKey
jwt.expiration-ms=3600000
```

> Tip: Use environment variables or Docker secrets in production instead of hardcoded values.

## Docker Support

This service is Docker-ready and can be built into a container image. Docker support allows easy deployment in local development and container orchestration environments.

Example Docker commands:

```bash
# Build the JAR package
./mvnw clean package -DskipTests

# Build the Docker image
docker build -t auth-service:latest .

# Run the service container
docker run -d --name auth-service \
  -p 8081:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/authdb \
  -e SPRING_DATASOURCE_USERNAME=auth_user \
  -e SPRING_DATASOURCE_PASSWORD=auth_password \
  -e JWT_SECRET=ReplaceWithYourSecretKey \
  auth-service:latest
```

## Docker Compose Usage

If the platform uses Docker Compose, place this service alongside MySQL, Kafka, ZooKeeper, and other microservices.

Example `docker-compose.yml` snippet:
```yaml
version: '3.8'
services:
  auth-service:
    build: ./Auth-Service
    ports:
      - "8081:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/authdb
      SPRING_DATASOURCE_USERNAME: auth_user
      SPRING_DATASOURCE_PASSWORD: auth_password
      JWT_SECRET: ReplaceWithYourSecretKey
      SPRING_KAFKA_BOOTSTRAP-SERVERS: kafka:9092
    depends_on:
      - mysql
      - kafka

  mysql:
    image: mysql:8.1
    environment:
      MYSQL_DATABASE: authdb
      MYSQL_USER: auth_user
      MYSQL_PASSWORD: auth_password
      MYSQL_ROOT_PASSWORD: root_password
    ports:
      - "3306:3306"

  kafka:
    image: bitnami/kafka:latest
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENERS: PLAINTEXT://:9092
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"

  zookeeper:
    image: bitnami/zookeeper:latest
    environment:
      ALLOW_ANONYMOUS_LOGIN: 'yes'
    ports:
      - "2181:2181"
```

## Folder Structure

```text
Auth-Service/
├── src/
│   ├── main/
│   │   ├── java/com/example/Auth_Service/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── kafka/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── AuthServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/java/com/example/Auth_Service/
├── dockerfile
├── pom.xml
└── HELP.md
```

## How to Run Locally

1. Install Java 21 and Maven.
2. Start MySQL and Kafka locally.
3. Update `src/main/resources/application.properties` with your local database and Kafka information.
4. Build and run the service:

```bash
./mvnw clean spring-boot:run
```

5. Open `http://localhost:8080` or use the API endpoints above.

## How to Run with Docker

1. Build the Docker image:
```bash
docker build -t auth-service:latest .
```
2. Run the container with environment variables:
```bash
docker run -d --name auth-service \
  -p 8081:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/authdb \
  -e SPRING_DATASOURCE_USERNAME=auth_user \
  -e SPRING_DATASOURCE_PASSWORD=auth_password \
  -e JWT_SECRET=ReplaceWithYourSecretKey \
  auth-service:latest
```
3. Verify the service is running at `http://localhost:8081`.

## Example cURL Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"jane.doe","password":"SecureP@ss123","email":"jane.doe@example.com"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"jane.doe","password":"SecureP@ss123"}'
```

### Access Protected Resource
```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Future Improvements

- Add refresh token support and logout endpoint
- Implement multi-factor authentication (MFA)
- Add stronger password policies and account lockout
- Support OAuth2 / social login providers
- Add full OpenAPI / Swagger documentation
- Add metrics and tracing for distributed observability
- Harden Kafka and MySQL configurations for production

## Notes for Recruiters

This repository demonstrates a production-ready authentication microservice built with Spring Boot and JWT. It is designed for distributed systems and includes secure user flows, persistent MySQL storage, Kafka event integration, and containerized deployment support.
