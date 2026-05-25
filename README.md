# Distributed Log Monitoring Platform

[![Maven Central](https://img.shields.io/badge/build-Maven-brightgreen)](https://maven.apache.org/)
[![Java 21](https://img.shields.io/badge/java-21-blue)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-3.5.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/kafka-enabled-orange)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://www.docker.com/)

## Overview

The `distributed-log-monitoring-platform` is a microservices-based system for authentication, payment processing, and centralized log aggregation. It demonstrates how Spring Boot services can use MySQL and Apache Kafka to build a scalable, observable platform.

This repository includes three primary microservices:
- `Auth-Service` — handles user registration, login, JWT authentication, and Kafka log publishing.
- `Payment-Service` — processes payment requests, stores transactions, and publishes audit logs.
- `Log-Processing-Service` — consumes Kafka log events and persists them to MySQL for centralized monitoring.

## Features

- Distributed microservices architecture
- JWT-based authentication and authorization
- Kafka-based event-driven logging
- MySQL persistence for user, payment, and log data
- Docker and Docker Compose support
- Spring Boot, Spring Security, Spring Data JPA, Spring Kafka
- Centralized log processing and storage

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Kafka
- Spring Data JPA
- MySQL
- Docker
- Docker Compose
- Maven
- Lombok

## Architecture

The platform is organized into three collaborating services:

- `auth-service`: authenticates users, issues JWT tokens, and publishes authentication logs to Kafka.
- `payment-service`: accepts payment requests, records transactions in MySQL, and publishes payment logs to Kafka.
- `log-processing-service`: consumes logs from Kafka, transforms them, and stores them in a dedicated logs database table.

Kafka is the central messaging backbone. Services publish log events to the `log-topic` topic, and `log-processing-service` consumes and persists them.

## Service Summary

| Service | Function | Port | Notes |
| --- | --- | --- | --- |
| `Auth-Service` | User registration, login, JWT auth, Kafka log producer | `8081` | Uses Spring Security and JWT tokens |
| `Payment-Service` | Payment processing, MySQL persistence, Kafka log producer | `8082` | Exposes `/payment/pay` endpoint |
| `Log-Processing-Service` | Kafka consumer, log persistence, centralized log storage | `8083` | Consumes `log-topic` and writes to MySQL |

## Kafka Workflow

- Producers: `Auth-Service` and `Payment-Service` publish structured log events to Kafka.
- Topic: `log-topic`
- Consumer: `Log-Processing-Service` listens on `log-topic` and persists logs.
- This design supports audit logging, observability, and cross-service monitoring.

## Database Configuration

All services connect to a shared MySQL database named `distributed_log_monitoring`.

- MySQL exposed on host port `3307`
- Database name: `distributed_log_monitoring`
- Default credentials in compose: `root` / `root`

Each service uses Spring Data JPA with `spring.jpa.hibernate.ddl-auto=update` for schema generation during development.

## Docker Compose Setup

The root `docker-compose.yml` orchestrates Zookeeper, Kafka, MySQL, and all microservices.

### Services in compose

- `zookeeper`: Kafka coordination service
- `kafka`: message broker
- `mysql`: persistent relational storage
- `auth-service`: authentication service
- `payment-service`: payment service
- `log-processing-service`: centralized log consumer

### Run with Docker Compose

```bash
docker-compose up --build
```

### Stop and remove containers

```bash
docker-compose down
```

## Running Locally

### Prerequisites

- Java 21
- Maven
- Docker (optional for containers)
- Kafka and MySQL if not using Docker

### Run services individually

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

### Run with root Docker Compose

```bash
docker-compose up --build
```

This starts the full platform with the following endpoints:
- `http://localhost:8081` for `auth-service`
- `http://localhost:8082` for `payment-service`
- `http://localhost:8083` for `log-processing-service`

## Example Service Requests

### Auth-Service register/login

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"jane.doe","password":"SecureP@ss123","email":"jane.doe@example.com"}'
```

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"jane.doe","password":"SecureP@ss123"}'
```

### Payment-Service request

```bash
curl -X POST http://localhost:8082/payment/pay \
  -H "Content-Type: application/json" \
  -d '{"OrderId":123,"amount":149.99,"paymentMethod":"CREDIT_CARD"}'
```

### Consumed log payload

```json
{
  "serviceName": "PAYMENT-SERVICE",
  "level": "INFO",
  "message": "Payment Successful"
}
```

## Root Dockerfile Notes

Each service contains its own `dockerfile`. The root compose file builds each service from its local directory.

- `Auth-Service/dockerfile`
- `payment-service/dockerfile`
- `log-processing-service/dockerfile`

> Ensure the service Dockerfiles reference the correct built JAR path before building images.

## Folder Structure

```text
distributed-log-monitoring-platform/
├── Auth-Service/
├── payment-service/
├── log-processing-service/
├── docker-compose.yml
└── README.md
```

## Future Improvements

- Add API documentation with OpenAPI / Swagger
- Implement distributed tracing with OpenTelemetry
- Add service-to-service authentication and authorization
- Introduce retry and dead-letter queue handling for Kafka
- Add monitoring dashboards and alerting
- Harden production-grade security for Kafka and MySQL

## Notes for Recruiters

This repository demonstrates a practical distributed system built with Spring Boot, Kafka, and MySQL. It showcases event-driven architecture, centralized logging, secure authentication, transaction processing, and containerized deployment.
