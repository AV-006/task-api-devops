#!/usr/bin/env bash
set -euo pipefail
for replicas in 5 2 3; do
  echo "Scaling task-api to ${replicas} replicas"
  kubectl scale deployment/task-api --replicas="${replicas}"
  kubectl rollout status deployment/task-api --timeout=120s
  kubectl get pods -l app=task-api
  echo
 done
