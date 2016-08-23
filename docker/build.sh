#!/usr/bin/env bash

set -e

docker build -t "vertx-blueprint/api-gateway" ../api-gateway
docker build -t "vertx-blueprint/cache-infrastructure" ../cache-infrastructure
docker build -t "vertx-blueprint/inventory-microservice" ../inventory-microservice
docker build -t "vertx-blueprint/monitor-dashboard" ../monitor-dashboard
docker build -t "vertx-blueprint/order-microservice" ../order-microservice
docker build -t "vertx-blueprint/product-microservice" ../product-microservice
docker build -t "vertx-blueprint/shopping-cart-microservice" ../shopping-cart-microservice
docker build -t "vertx-blueprint/store-microservice" ../store-microservice
docker build -t "vertx-blueprint/account-microservice" ../account-microservice