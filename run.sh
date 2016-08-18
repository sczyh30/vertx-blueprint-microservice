#!/usr/bin/env bash

set -e

# TODO: set env for docker-machine in Windows and OSX

# Stop and remove all old containers
docker-compose stop
docker-compose rm --all

# Start container cluster
docker-compose up