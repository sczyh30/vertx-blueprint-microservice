#!/usr/bin/env bash

set -e

# TODO: set env for docker-machine in Windows and OSX

# Stop
docker-compose stop

# Start container cluster
# First start persistence and auth container and wait for it
docker-compose up -d mysql mongo redis keycloak-server
echo "Waiting for persistence init..."
sleep 30

# Start other containers
docker-compose up