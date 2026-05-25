# log-processing-service

[![Maven Central](https://img.shields.io/badge/build-Maven-brightgreen)](https://maven.apache.org/)
[![Java 21](https://img.shields.io/badge/java-21-blue)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-3.5.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/kafka-enabled-orange)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://www.docker.com/)

## Overview

`log-processing-service` is the Kafka consumer microservice in a Distributed Log Monitoring Platform. It consumes log events from upstream services, processes and persists them into a MySQL database, and enables centralized log visibility for the architecture.

This service is designed to capture logs from other microservices such as `auth-service` and `payment-service`, and simulate a centralized logging flow similar to Datadog or Splunk.

## Features

- Kafka consumer for centralized log ingestion
- Structured log persistence using Spring Data JPA
- MySQL storage for processed log entries
- Service-level event tracking through Kafka
- Docker-ready deployment
- Beginner-friendly architecture with production-style patterns

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Kafka
- Spring Data JPA
- MySQL
- Docker
- Maven
- Lombok

## Microservices Architecture

The `log-processing-service` is part of a distributed logging pipeline:

- `auth-service` and `payment-service` publish log events to Kafka
- Apache Kafka acts as the broker for event delivery
- `log-processing-service` consumes the log events from a topic
- The service transforms and persists events into MySQL
- Logs become available for analytics, monitoring, and reporting

## Kafka Consumer Workflow

1. `log-processing-service` subscribes to Kafka topic `log-topic`.
2. When a new log event is published, Spring Kafka forwards it to the consumer method.
3. The incoming payload is mapped to the shared `LogMessage` DTO.
4. The service persists the log entry to the database.
5. The consumer acknowledges successful processing.

### Example Consumer Code

```java
@Service
@RequiredArgsConstructor
public class LogConsumer {

    private final LogService logService;

    @KafkaListener(topics = "log-topic", groupId = "log-group")
    public void consumer(LogMessage logMessage) {
        System.out.println(logMessage);
        logService.saveLog(logMessage);
    }
}
```

This code shows the primary Kafka listener. It receives a deserialized `LogMessage` and delegates persistence to `LogService`.

## Database Schema

`log-processing-service` persists log entries in a JPA entity named `LogEntity`.

### Table structure

| Column | Type | Description |
| --- | --- | --- |
| `id` | BIGINT | Auto-generated primary key |
| `service_name` | VARCHAR | Originating microservice name |
| `level` | VARCHAR | Log severity level |
| `message` | TEXT | Log message content |
| `timestamp` | DATETIME | Time the log was processed |

The database table is generated automatically using `spring.jpa.hibernate.ddl-auto=update`.

## Log Processing Flow

- A producer service publishes a log event to Kafka.
- `log-processing-service` consumes the event from `log-topic`.
- The event is mapped to `LogMessage` DTO.
- `LogService` creates a `LogEntity` and stores it in MySQL.
- The service uses the current timestamp to record processing time.

## API Endpoints

This service is primarily an event-driven Kafka consumer. There are no public REST endpoints implemented for log ingestion.

> If future extensions are added, a REST API can expose processed logs or health checks.

## Folder Structure

```text
log-processing-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/log_processing_service/
│   │   │   ├── DTO/
│   │   │   ├── Entity/
│   │   │   ├── Kafka/
│   │   │   ├── Repository/
│   │   │   ├── Service/
│   │   │   └── LogProcessingServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/java/com/example/log_processing_service/
├── dockerfile
├── pom.xml
└── HELP.md
```

## Docker Support

The repository includes a Dockerfile for containerizing the service. Docker support enables consistent local development and deployment across environments.

### Example Docker commands

```bash
./mvnw clean package -DskipTests

docker build -t log-processing-service:latest .

docker run -d --name log-processing-service \
  -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/distributed_log_monitoring \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
  log-processing-service:latest
```

## Docker Compose Usage

Use Docker Compose to start `log-processing-service` alongside MySQL, Kafka, and ZooKeeper.

```yaml
version: '3.8'
services:
  log-processing-service:
    build: ./log-processing-service
    ports:
      - "8083:8083"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/distributed_log_monitoring
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - mysql
      - kafka

  mysql:
    image: mysql:8.1
    environment:
      MYSQL_DATABASE: distributed_log_monitoring
      MYSQL_USER: root
      MYSQL_PASSWORD: root
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3307:3306"

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

## How to Run Locally

1. Install Java 21 and Maven.
2. Start MySQL on `localhost:3307` and Kafka on `localhost:9092`.
3. Update `src/main/resources/application.properties` if needed.
4. Build and run the service:

```bash
./mvnw clean spring-boot:run
```

5. Confirm the application starts on port `8083`.

## How to Run with Docker

1. Build the Docker image:

```bash
docker build -t log-processing-service:latest .
```

2. Run the image:

```bash
docker run -d --name log-processing-service \
  -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/distributed_log_monitoring \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
  log-processing-service:latest
```

3. Check container logs to ensure Kafka consumer started successfully.

## Example Kafka Configuration

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=log-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*
```

## Example application.properties

```properties
spring.application.name=log-processing-service
server.port=8083

spring.datasource.url=jdbc:mysql://localhost:3307/distributed_log_monitoring
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=log-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*
```

## Example Log JSON Payload

```json
{
  "serviceName": "auth-service",
  "level": "INFO",
  "message": "User login successful"
}
```

This payload is deserialized into the shared `LogMessage` DTO and persisted as a `LogEntity`.

## Future Improvements

- Add a REST API for querying stored logs
- Add log enrichment with metadata and trace IDs
- Add support for multiple Kafka topics
- Implement retry and dead-letter queue handling
- Add metrics and tracing with Prometheus / OpenTelemetry
- Add security and access controls for monitoring endpoints

## Notes for Recruiters

This repository represents a Kafka-based log ingestion microservice built with Spring Boot, MySQL, and Spring Kafka. It is designed for distributed systems where application logs are centralized and persisted for analytics and monitoring.
