# Lot 6 — CI, quality, Docker and architecture notes

## Added

- GitHub Actions CI on Java 21;
- `mvn clean verify` as the canonical verification command;
- JaCoCo report and 80% line-coverage quality gate;
- Spring Boot Actuator health endpoint;
- multi-stage Dockerfile using Java 21;
- non-root runtime user;
- Docker healthcheck on `/actuator/health`;
- Spring context startup test;
- architecture document;
- two small ADRs;
- technical presentation guide.

## Deliberately not added

- Checkstyle/SpotBugs/Sonar configuration;
- new layers or interfaces in the calculator core;
- parser/evaluator libraries;
- new mathematical behavior.

The goal of this lot is delivery confidence, not architectural expansion.

## Canonical local verification

```bash
mvn clean verify
```

JaCoCo report:

```text
target/site/jacoco/index.html
```

## Run locally

```bash
mvn spring-boot:run
```

Health endpoint:

```text
GET http://localhost:8080/actuator/health
```

## Docker

```bash
docker build -t calculator-challenge:lot6 .
docker run --rm -p 8080:8080 calculator-challenge:lot6
```
