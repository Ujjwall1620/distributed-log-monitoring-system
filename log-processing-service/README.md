# Log-Processing-Service

`log-processing-service` consumes Kafka log events and persists them in MySQL, providing centralized visibility for the platform.

## Key features

- Kafka consumer for `log-topic`
- Log persistence via Spring Data JPA
- Queryable log endpoint
- Docker support

## Endpoints

- `GET /logs`

Optional query parameters:

- `service`
- `level`
- `message`
- `startTime` (ISO date-time)
- `EndTime` (ISO date-time)

## Run locally

```bash
cd log-processing-service
./mvnw clean spring-boot:run
```

Default service port: `8083`

## Docker

```bash
./mvnw clean package -DskipTests

docker build -t log-processing-service:latest .

docker run -d --name log-processing-service \
  -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/distributed_log_monitoring \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  log-processing-service:latest
```

## Docker Compose

When running under Compose, this service connects to:

- `mysql:3306`
- `kafka:9092`

## Notes

- The service consumes Kafka topic `log-topic` and stores logs in MySQL.
- It also creates Kafka topic `processed-data` as part of its configuration.
- Use container hostnames inside Compose and secure credentials in production.
