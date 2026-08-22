FROM maven:3.9.16-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S calculator \
    && adduser -S calculator -G calculator

COPY --from=build \
    --chown=calculator:calculator \
    /workspace/target/calculator-challenge-0.0.1-SNAPSHOT.jar \
    /app/calculator-challenge.jar

USER calculator

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
    CMD wget -q -O /dev/null http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/calculator-challenge.jar"]
