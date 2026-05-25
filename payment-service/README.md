# payment-service

[![Maven Central](https://img.shields.io/badge/build-Maven-brightgreen)](https://maven.apache.org/)
[![Java 21](https://img.shields.io/badge/java-21-blue)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-3.5.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/kafka-enabled-orange)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://www.docker.com/)

## Overview

`payment-service` is a payment processing microservice in a Distributed Log Monitoring Platform. It accepts payment requests, stores transactions to MySQL, and publishes audit logs to Apache Kafka for centralized monitoring.

This service integrates with other platform components such as `auth-service` and `log-processing-service` to provide transactional auditing and scalable observability.

## Features

- Payment request processing
- MySQL persistence with Spring Data JPA
- Kafka event production for audit logs
- REST API endpoint for payment operations
- Docker-compatible deployment
- Clear separation of controller, service, repository, and Kafka producer

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Kafka
- MySQL
- Docker
- Maven
- Lombok

## Architecture

`payment-service` is structured into three main responsibilities:

- Controller layer: exposes HTTP payment endpoints
- Service layer: processes payment logic and persists transactions
- Kafka layer: publishes structured payment events to `log-topic`

The service is part of a distributed system where Kafka acts as the event bus, and `log-processing-service` consumes payment logs for central monitoring.

## API Endpoints

| Endpoint | Method | Description | Payload |
| --- | --- | --- | --- |
| `/payment/pay` | `POST` | Process a payment and emit a Kafka log event | `PaymentRequest` |

### Request / Response Example

#### Payment Request
```json
POST /payment/pay
Content-Type: application/json

{
  "OrderId": 123,
  "amount": 149.99,
  "paymentMethod": "CREDIT_CARD"
}
```

#### Successful Response
```json
"Payment Successful"
```

## Payment Workflow

1. Client sends a payment request to `/payment/pay`.
2. `paymentController` receives the request and delegates to `paymentService`.
3. `paymentService` creates a `payment` entity and saves it to MySQL.
4. After successful persistence, the service publishes a `LogMessage` event to Kafka.
5. `log-processing-service` or other consumers can process the event from `log-topic`.

## Kafka Event Flow

`payment-service` sends audit logs to Kafka using `LogProducer`:

- Topic: `log-topic`
- Producer value type: `LogMessage`
- Fields published: `serviceName`, `level`, `message`

### Producer Example

```java
@Service
@RequiredArgsConstructor
public class LogProducer {
    private final KafkaTemplate<String, LogMessage> kafkaTemplate;

    public void sendlog(LogMessage logMessage) {
        kafkaTemplate.send("log-topic", logMessage);
    }
}
```

## Database Schema

`payment-service` persists payment transactions in a JPA entity named `payment`.

### Table structure

| Column | Type | Description |
| --- | --- | --- |
| `id` | INT | Auto-generated primary key |
| `order_id` | INT | External order identifier |
| `amount` | DOUBLE | Payment amount |
| `payment_method` | VARCHAR | Payment method used |
| `status` | VARCHAR | Transaction status |

## Example application.properties

```properties
spring.application.name=payment-service
server.port=8082

spring.datasource.url=jdbc:mysql://localhost:3307/distributed_log_monitoring?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

logging.level.org.springframework.kafka=INFO
logging.level.org.hibernate.SQL=DEBUG
```

## Docker Support

This service includes a Dockerfile for building a container image.

### Build and run commands

```bash
./mvnw clean package -DskipTests

docker build -t payment-service:latest .

docker run -d --name payment-service \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/distributed_log_monitoring?createDatabaseIfNotExist=true \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
  payment-service:latest
```

> Note: The current Dockerfile contains a build path issue and may need adjustment to target the generated JAR correctly.

## Docker Compose Usage

Example `docker-compose.yml` snippet:

```yaml
version: '3.8'
services:
  payment-service:
    build: ./payment-service
    ports:
      - "8082:8082"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/distributed_log_monitoring?createDatabaseIfNotExist=true
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

## Folder Structure

```text
payment-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/payment_service/
│   │   │   ├── Controller/
│   │   │   ├── DTO/
│   │   │   ├── Entity/
│   │   │   ├── Repository/
│   │   │   ├── Service/
│   │   │   ├── kafka/
│   │   │   └── PaymentServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/java/com/example/payment_service/
├── dockerfile
├── pom.xml
└── HELP.md
```

## How to Run Locally

1. Install Java 21 and Maven.
2. Start MySQL and Kafka locally.
3. Update `src/main/resources/application.properties` with your database and Kafka settings.
4. Run the service:

```bash
./mvnw clean spring-boot:run
```

5. Confirm the service is available at `http://localhost:8082`.

## How to Run with Docker

1. Build the image:

```bash
docker build -t payment-service:latest .
```

2. Run the container:

```bash
docker run -d --name payment-service \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/distributed_log_monitoring?createDatabaseIfNotExist=true \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
  payment-service:latest
```

## Example cURL Request

```bash
curl -X POST http://localhost:8082/payment/pay \
  -H "Content-Type: application/json" \
  -d '{"OrderId":123,"amount":149.99,"paymentMethod":"CREDIT_CARD"}'
```

## Future Improvements

- Add payment validation and failure handling
- Expose payment history REST endpoints
- Add security for payment APIs
- Add retry logic for Kafka delivery failures
- Add metrics and health checks
- Add integration tests for Kafka and database interactions

## Notes for Recruiters

This repository demonstrates a payment microservice built with Spring Boot, Kafka, and MySQL. It is designed for distributed logging architectures and supports payment persistence plus event-driven auditing.
