# Payment-Service

`payment-service` processes payment requests and publishes audit log events to Kafka.

## Key features

- Payment processing endpoint
- MySQL persistence using Spring Data JPA
- Kafka audit log production on `log-topic`
- Docker support

## Endpoints

- `POST /payment/pay`

## Run locally

```bash
cd payment-service
./mvnw clean spring-boot:run
```

Default service port: `8082`

## Docker

```bash
./mvnw clean package -DskipTests

docker build -t payment-service:latest .

docker run -d --name payment-service \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/distributed_log_monitoring?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  payment-service:latest
```

## Docker Compose

When running under Compose, this service connects to:

- `mysql:3306`
- `kafka:9092`

## Example request

```bash
curl -X POST http://localhost:8082/payment/pay \
  -H "Content-Type: application/json" \
  -d '{"OrderId":123,"amount":149.99,"paymentMethod":"CREDIT_CARD"}'
```

## Notes

- Use container hostnames when the service runs inside Docker Compose.
- Replace default credentials and secrets before production deployment.
