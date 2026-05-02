# GOMP Backend MVP
Group Order Management Platform backend prototype (Assignment 4): users create/join group orders, add items, see payment shares, and organizers manage order lifecycle.

## Tech Stack
Java 17, Spring Boot, Spring Web, Spring Security, JWT, Spring Data JPA/Hibernate, PostgreSQL, Flyway, Docker Compose, springdoc OpenAPI, Lombok, Gradle.

## Implemented MVP Workflow
`login -> create order -> join order -> add item -> check payments -> change order status -> receive notifications`

## Run Locally
1. Start PostgreSQL (`gomp/gomp`, db `gomp`) on `localhost:5432`.
2. Set `JWT_SECRET` env.
3. Run:
```bash
./gradlew bootRun
```

## Run with Docker
1. Build jar locally:
```bash
./gradlew clean bootJar -x test
```
2. Start containers:
```bash
docker compose up --build
```

## Environment Variables
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`

## Swagger
`http://localhost:8080/swagger-ui/index.html`

## Seed Credentials
- `admin@gomp.kz / password`
- `organizer1@gomp.kz / password`
- `organizer2@gomp.kz / password`
- `user1@gomp.kz / password`

## Main Endpoints
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/users/me`
- `DELETE /api/v1/users/me`
- `POST /api/v1/orders`
- `GET /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `POST /api/v1/orders/{id}/join`
- `POST /api/v1/orders/{id}/items`
- `GET /api/v1/orders/{id}/items`
- `PATCH /api/v1/orders/{id}/status`
- `GET /api/v1/orders/{id}/payments`
- `PATCH /api/v1/payments/{id}/status`
- `PATCH /api/v1/items/{id}/distributed`
- `GET /api/v1/notifications/my`
- `GET /actuator/health`

## Example Flow
1. Login as organizer (`/auth/login`) and copy access token.
2. Create order (`POST /orders`) with Bearer token.
3. Login as user and join (`POST /orders/{id}/join`).
4. Add item (`POST /orders/{id}/items`).
5. Check shares (`GET /orders/{id}/payments`).
6. Organizer updates status (`PATCH /orders/{id}/status`).
