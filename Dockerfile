# stage 1: build the jar file
FROM gradle:8.5-jdk21 AS builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle clean build -x test

# stage 2: run the application
FROM eclipse-temurin:21-jdk
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]