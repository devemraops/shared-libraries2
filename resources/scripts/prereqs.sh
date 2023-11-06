#!/bin/bash

WORKSPACE=${WORKSPACE:-.}
DOCKER_COMPOSE=${WORKSPACE}/docker-compose
DOCKER_COMPOSE_INSTALL=https://docker.com

if [ -z "(ls -A ${DOCKER_COMPOSE})" ]; then
  echo "[WARN] docker compose not found on agent --- trying to install ...."
  curl -L "${DOCKER_COMPOSE_INSTALL}" > /usr/local/bin/docker-compose 2>/dev
  chmod +x /usr/local/bin/docker-compose
else
  echo "Docker Compose already installed."
  ${DOCKER_COMPOSE} --version
fi