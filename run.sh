#!/usr/bin/env bash

set -e

# Build the entire project and docker images
mvn clean install

# TODO: set env for docker-machine in Windows and OSX

# Stop and remove all old containers
docker-compose stop
docker-compose rm --all

# Start container cluster
docker-compose up