#!/usr/bin/env bash

set -e

# TODO: set env for docker-machine in Windows and OSX

# Stop and remove all old containers
docker-compose stop
docker-compose rm --all -f

# Start container cluster
# First start persistence container and wait for it
docker-compose up -d mysql mongo redis
echo "Waiting for persistence init..."
sleep 25

# Start other containers
docker compose up