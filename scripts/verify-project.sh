#!/bin/bash
# Comprehensive project verification script
set -e

echo "=========================================================="
echo "    AICTE Approval Workflow System Verification Script   "
echo "=========================================================="

FAILED=0

check_file() {
    if [ -f "$1" ]; then
        echo " [OK] File exists: $1"
    else
        echo " [FAIL] Missing file: $1"
        FAILED=1
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo " [OK] Directory exists: $1"
    else
        echo " [FAIL] Missing directory: $1"
        FAILED=1
    fi
}

echo ""
echo "--- 1. Checking Directory Structure ---"
check_dir ".github/workflows"
check_dir "backend/src/main/java"
check_dir "backend/src/main/resources/db/migration"
check_dir "frontend/src"
check_dir "database/seed"
check_dir "database/sample-data"
check_dir "docs"
check_dir "nginx"
check_dir "scripts"

echo ""
echo "--- 2. Checking Critical Configuration Files ---"
check_file "backend/pom.xml"
check_file "backend/Dockerfile"
check_file "frontend/package.json"
check_file "frontend/Dockerfile"
check_file "docker-compose.yml"
check_file ".env.example"
check_file ".gitignore"
check_file ".dockerignore"
check_file "LICENSE"
check_file "README.md"
check_file "CHANGELOG.md"
check_file "nginx/nginx.conf"

echo ""
echo "--- 3. Checking Git Safety (Secret Leak Prevention) ---"
if [ -f ".env" ]; then
    echo " [FAIL] Real .env file found! Must NOT be committed."
    FAILED=1
else
    echo " [OK] No real .env file present."
fi

# Secret pattern checks
SECRET_MATCHES=$(grep -rn "BEGIN RSA PRIVATE KEY\|BEGIN OPENSSH PRIVATE KEY\|aws_secret_access_key" . \
  --exclude-dir={.git,node_modules,target,dist} 2>/dev/null || true)
if [ -n "$SECRET_MATCHES" ]; then
    echo " [FAIL] Potential private key/secret found:"
    echo "$SECRET_MATCHES"
    FAILED=1
else
    echo " [OK] No private key patterns detected."
fi

echo ""
echo "--- 4. Checking Database Migrations ---"
MIGRATION_COUNT=$(ls -1 backend/src/main/resources/db/migration/V*.sql 2>/dev/null | wc -l)
if [ "$MIGRATION_COUNT" -ge 5 ]; then
    echo " [OK] Found $MIGRATION_COUNT Flyway migration files."
else
    echo " [FAIL] Insufficient migrations: $MIGRATION_COUNT found."
    FAILED=1
fi

echo ""
echo "--- 5. Checking Documentation Completeness ---"
check_file "docs/architecture.md"
check_file "docs/database-design.md"
check_file "docs/er-diagram.md"
check_file "docs/api-documentation.md"
check_file "docs/workflow-documentation.md"
check_file "docs/process-matrix.md"
check_file "docs/role-permission-matrix.md"
check_file "docs/testing-plan.md"
check_file "docs/deployment-guide.md"
check_file "docs/ai-usage-and-limitations.md"
check_file "docs/security-notes.md"
check_file "docs/user-manual.md"
check_file "docs/project-presentation-content.md"

echo ""
echo "=========================================================="
if [ "$FAILED" -eq 0 ]; then
    echo "  ALL VERIFICATION CHECKS PASSED SUCCESSFULLY (100%)"
    echo "=========================================================="
    exit 0
else
    echo "  VERIFICATION FAILED: Please address flagged issues."
    echo "=========================================================="
    exit 1
fi
