# Vert.x Microservice Blueprint

[![Travis Build Status](https://travis-ci.org/sczyh30/vertx-blueprint-microservice.svg?branch=master)](https://travis-ci.org/sczyh30/vertx-blueprint-microservice)

Vert.x blueprint application - An online shopping microservice application developed with Vert.x.
This repo is intended to be an illustration on how to develop microservice applications using Vert.x toolkit. 

## Content

- Microservice with Vert.x
- Asynchronous development model
- Reactive and functional pattern
- Event sourcing
- Asynchronous RPC on the clustered event bus
- Various type of services (e.g. REST, message source, data source)
- Service discovery
- Circuit breaker
- Polyglot persistence support
- API gateway
- Global authentication (OAuth 2 + Keycloak)

## Documentation

Detailed tutorials are available here!

- English Version
    - [Vert.x Blueprint - Online shopping microservice practice (Development)](http://sczyh30.github.io/vertx-blueprint-microservice/index.html)
    - [Vert.x Blueprint - Online shopping microservice practice (API Gateway)](http://sczyh30.github.io/vertx-blueprint-microservice/api-gateway.html)
- 中文版本
    - [Vert.x 蓝图 - Online Shopping 微服务实战(开发篇)](http://sczyh30.github.io/vertx-blueprint-microservice/cn/index.html)
    - [Vert.x 蓝图 - Online Shopping 微服务实战(API Gateway)](http://sczyh30.github.io/vertx-blueprint-microservice/cn/api-gateway.html)

## Architecture

![Microservice Architecture](docs/images/entire-architecture.png)

## Build/Run

To build the code:

    mvn clean install

To run the microservice with Docker Compose:

    cd docker
    sudo ./run.sh

## Contributing

Contributions are definitely welcome !
