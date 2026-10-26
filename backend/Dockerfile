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

# Inline entrypoint script that converts postgres:// or postgresql:// to jdbc:postgresql://
RUN printf '#!/bin/sh\n\
if echo "$DATABASE_URL" | grep -q "^postgres"; then\n\
  WITHOUT_PREFIX=$(echo "$DATABASE_URL" | sed -E "s|^postgres(ql)?://||")\n\
  if echo "$WITHOUT_PREFIX" | grep -q "@"; then\n\
    USERPASS=$(echo "$WITHOUT_PREFIX" | cut -d"@" -f1)\n\
    HOSTDBNAME=$(echo "$WITHOUT_PREFIX" | cut -d"@" -f2)\n\
    export DATABASE_URL="jdbc:postgresql://$HOSTDBNAME"\n\
    [ -z "$DATABASE_USERNAME" ] && export DATABASE_USERNAME=$(echo "$USERPASS" | cut -d":" -f1)\n\
    [ -z "$DATABASE_PASSWORD" ] && export DATABASE_PASSWORD=$(echo "$USERPASS" | cut -d":" -f2)\n\
  else\n\
    export DATABASE_URL="jdbc:postgresql://$WITHOUT_PREFIX"\n\
  fi\n\
  echo "Converted DATABASE_URL to JDBC format."\n\
fi\n\
exec java -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar\n' > /app/entrypoint.sh && chmod 755 /app/entrypoint.sh

USER aicte:aicte

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
