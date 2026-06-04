# Distributed Log Monitoring Platform

A Docker-ready microservices system for authentication, payment processing, and centralized log ingestion.

This repository contains three Spring Boot services that collaborate through Apache Kafka and MySQL:

- `Auth-Service` — handles user registration, login, and audit log publishing.
- `payment-service` — accepts payment requests, stores transactions, and emits Kafka log events.
- `log-processing-service` — consumes Kafka log messages and persists them to MySQL for centralized monitoring.

## Key Technologies

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- Spring Kafka
- MySQL
- Docker
- Docker Compose
- Maven

## Architecture

The platform is composed of independent services connected by a shared Kafka messaging layer and a shared MySQL database.

- `Auth-Service` publishes authentication audit logs to Kafka
- `payment-service` publishes payment audit logs to Kafka
- `log-processing-service` consumes `log-topic` and stores processed log records
- MySQL stores application data and processed logs

## Services

| Service | HTTP Port | Purpose |
| --- | --- | --- |
| `Auth-Service` | `8081` | User authentication and Kafka audit logging |
| `payment-service` | `8082` | Payment processing and Kafka audit logging |
| `log-processing-service` | `8083` | Kafka log consumer and persistent log storage |

## Docker Compose

The root `docker-compose.yml` file starts:

- `zookeeper`
- `kafka`
- `mysql`
- `auth-service`
- `payment-service`
- `log-processing-service`

### Start the platform

```bash
docker compose up --build
```

### Stop and cleanup

```bash
docker compose down
```

## Local development

### Prerequisites

- Java 21
- Maven
- Docker
- Docker Compose

### Run individual services

```bash
cd Auth-Service
./mvnw clean spring-boot:run
```

```bash
cd payment-service
./mvnw clean spring-boot:run
```

```bash
cd log-processing-service
./mvnw clean spring-boot:run
```

### Run the full stack with Docker Compose

```bash
docker compose up --build
```

## Service endpoints

### Auth-Service

- Register: `POST http://localhost:8081/auth/register`
- Login: `POST http://localhost:8081/auth/login`

### Payment-Service

- Process payment: `POST http://localhost:8082/payment/pay`

### Log-Processing-Service

- Read logs: `GET http://localhost:8083/logs`
- Optional query parameters: `service`, `level`, `message`, `startTime`, `EndTime`

## Kafka topics

- `log-topic` — produced by `Auth-Service` and `payment-service`
- `processed-data` — created by `log-processing-service`

## Database

- MySQL database: `distributed_log_monitoring`
- Host port: `3307`
- Container port: `3306`
- Default credentials: `root` / `root`

## Working with Docker Compose

Inside Compose, services connect to each other by container name:

- Kafka: `kafka:9092`
- MySQL: `mysql:3306`

The services also use these environment variables inside the Compose stack.

## Folder structure

```text
distributed-log-monitoring-platform/
├── Auth-Service/
├── payment-service/
├── log-processing-service/
├── docker-compose.yml
└── README.md
```

## Notes

- Each service has its own Maven project and Dockerfile.
- `Auth-Service` and `payment-service` publish to the same Kafka topic for centralized log processing.
- `log-processing-service` provides a queryable log endpoint for stored audit records.
- For production, replace credentials and secrets with safe configuration management.
