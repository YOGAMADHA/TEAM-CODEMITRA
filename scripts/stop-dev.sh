#!/bin/bash
# Stop all running containers
set -e

echo "=== Stopping AICTE Development Containers ==="
docker compose down

echo "All services stopped."
