#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

docker build -t "vertx-blueprint/api-gateway" $DIR/../api-gateway
docker build -t "vertx-blueprint/cache-infrastructure" $DIR/../cache-infrastructure
docker build -t "vertx-blueprint/inventory-microservice" $DIR/../inventory-microservice
docker build -t "vertx-blueprint/monitor-dashboard" $DIR/../monitor-dashboard
docker build -t "vertx-blueprint/order-microservice" $DIR/../order-microservice
docker build -t "vertx-blueprint/product-microservice" $DIR/../product-microservice
docker build -t "vertx-blueprint/shopping-cart-microservice" $DIR/../shopping-cart-microservice
docker build -t "vertx-blueprint/store-microservice" $DIR/../store-microservice
docker build -t "vertx-blueprint/account-microservice" $DIR/../account-microservice