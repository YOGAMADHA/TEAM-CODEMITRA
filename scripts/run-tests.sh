#!/bin/bash
# Execute test suites across backend and frontend
set -e

echo "=== Running Backend Tests ==="
cd backend
if [ -f mvnw ]; then
    ./mvnw test -B
else
    mvn test -B
fi
cd ..

echo "=== Running Frontend Checks ==="
cd frontend
if [ -f package.json ]; then
    npm run build
fi
cd ..

echo "=== All Tests Completed Successfully ==="
