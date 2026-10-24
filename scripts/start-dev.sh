#!/bin/bash
# Start all development infrastructure containers
set -e

echo "=== Starting AICTE Development Infrastructure ==="
docker compose up -d postgres minio mailhog

echo "=== Infrastructure started successfully ==="
echo "PostgreSQL: localhost:5432"
echo "MinIO Console: http://localhost:9001 (minioadmin / minioadmin123)"
echo "MailHog UI: http://localhost:8025"
echo ""
echo "Next steps:"
echo "1. Backend: cd backend && ./mvnw spring-boot:run"
echo "2. Frontend: cd frontend && npm run dev"
