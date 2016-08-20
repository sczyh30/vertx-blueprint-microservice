# API Gateway

This component provides an API gateway. The API gateway is responsible
for dispatch the requests to the corresponding REST endpoint, load balancing,
simple heart beat check as well as failure handling (with circuit breaker).

## Configuration

- `api.gateway.http.address`: host of the gateway, by default **0.0.0.0**
- `api.gateway.http.port`: port of the gateway, by default **8787**
- `heartbeat.enable`: flag indicating whether heartbeat check is enabled, by default **true**
- `heartbeat.period`: interval of the heartbeat check, by default **60 seconds**

## Build

    mvn clean install

