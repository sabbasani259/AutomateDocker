#!/bin/bash
IMAGE_NAME="ghcr.io/sabbasani259/wise-ear:latest"

echo "Pulling Docker image: $IMAGE_NAME"
docker pull $IMAGE_NAME

echo "Stopping and removing any existing container..."
docker stop wise_app || true
docker rm wise_app || true

echo "Running new container..."
docker run -d --name wise_app -p 8080:8080 $IMAGE_NAME
