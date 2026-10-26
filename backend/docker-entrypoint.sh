#!/bin/sh
# Convert Render's postgres:// connection string to JDBC format
# Render provides: postgres://user:password@host:port/dbname
# Spring Boot needs: jdbc:postgresql://host:port/dbname (with separate user/password)
if echo "$DATABASE_URL" | grep -q "^postgres://"; then
  # Extract parts from postgres://user:password@host:port/dbname
  # Strip the postgres:// prefix
  WITHOUT_PREFIX=$(echo "$DATABASE_URL" | sed 's|^postgres://||')
  # Extract user:password (before @)
  USERPASS=$(echo "$WITHOUT_PREFIX" | cut -d'@' -f1)
  # Extract host:port/dbname (after @)
  HOSTDBNAME=$(echo "$WITHOUT_PREFIX" | cut -d'@' -f2)

  DB_USER=$(echo "$USERPASS" | cut -d':' -f1)
  DB_PASS=$(echo "$USERPASS" | cut -d':' -f2)

  export DATABASE_URL="jdbc:postgresql://$HOSTDBNAME"
  export DATABASE_USERNAME="$DB_USER"
  export DATABASE_PASSWORD="$DB_PASS"

  echo "Converted DATABASE_URL to JDBC format."
fi

exec java -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar
