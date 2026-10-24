#!/bin/bash
# Reset PostgreSQL database and re-run all Flyway migrations
set -e

echo "=== Resetting Database Volumes ==="
docker compose down -v
docker compose up -d postgres

echo "Waiting for PostgreSQL to be ready..."
until docker exec aicte-postgres pg_isready -U aicte_user -d aicte_db >/dev/null 2>&1; do
    echo "Waiting for postgres..."
    sleep 2
done

echo "=== Database reset complete. Start backend to apply migrations. ==="
