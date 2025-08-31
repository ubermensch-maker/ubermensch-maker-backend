# stage 1: build the jar file
FROM gradle:8.5-jdk21 AS builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle clean build -x test

# stage 2: run the application
FROM eclipse-temurin:21-jdk
COPY --from=builder /app/build/libs/ /tmp/libs/
RUN find /tmp/libs -name "*[!-plain].jar" -exec cp {} app.jar \; && \
    if [ ! -f app.jar ]; then \
        echo "No executable JAR found, using the first available JAR"; \
        find /tmp/libs -name "*.jar" | head -1 | xargs -I {} cp {} app.jar; \
    fi && \
    rm -rf /tmp/libs
ENTRYPOINT ["java", "-jar", "/app.jar"]