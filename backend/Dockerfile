# Multi-stage build for Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app
COPY pom.xml .
# Download dependencies in layer for caching
RUN mvn dependency:go-offline -B || true

COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true -B

# Production runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN apk add --no-cache curl

# Create non-root system user and storage directory
RUN addgroup -S aicte && adduser -S aicte -G aicte && mkdir -p /app/uploads && chown -R aicte:aicte /app

# Copy entrypoint script (must be done as root before switching user)
COPY --chmod=755 docker-entrypoint.sh /app/docker-entrypoint.sh

USER aicte:aicte

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Use the entrypoint script to convert Render's postgres:// URL to JDBC format
ENTRYPOINT ["/app/docker-entrypoint.sh"]
