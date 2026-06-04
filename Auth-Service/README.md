# Auth-Service

`Auth-Service` is the authentication microservice for the Distributed Log Monitoring Platform.

It provides user registration and login, issues JWT-like authentication responses, and publishes audit log events to Kafka.

## Key features

- REST endpoints for registration and login
- MySQL persistence using Spring Data JPA
- Kafka audit log publishing via `log-topic`
- Docker support

## Endpoints

- `POST /auth/register`
- `POST /auth/login`

## Run locally

```bash
cd Auth-Service
./mvnw clean spring-boot:run
```

Default service port: `8081`

## Docker

```bash
./mvnw clean package -DskipTests

docker build -t auth-service:latest .

docker run -d --name auth-service \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/distributed_log_monitoring?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  auth-service:latest
```

## Docker Compose

When running under the root Compose stack, this service uses:

- `SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/distributed_log_monitoring?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
- `SPRING_DATASOURCE_USERNAME=root`
- `SPRING_DATASOURCE_PASSWORD=root`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092`

## Notes

- The service publishes authentication events to Kafka topic `log-topic`.
- In a production setup, replace default credentials and secure all secrets.
