#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${1:-http://127.0.0.1:8080}"
echo 'GET /'
curl --fail --silent "$BASE_URL/"; echo
echo 'GET /tasks'
curl --fail --silent "$BASE_URL/tasks"; echo
echo 'GET /actuator/health'
curl --fail --silent "$BASE_URL/actuator/health"; echo
echo 'POST /tasks'
curl --fail --silent -X POST "$BASE_URL/tasks" -H 'Content-Type: application/json' -d '{"title":"Capture real evidence"}'; echo
