#!/usr/bin/env bash
set -euo pipefail

# Run from repository root after Docker image has been built.
minikube start --driver=docker

docker build -t task-api:1.0.0 .
minikube image load task-api:1.0.0
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl rollout status deployment/task-api --timeout=180s
kubectl get pods -l app=task-api -o wide
kubectl get svc task-api-service
