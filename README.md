# Vert.x Microservice Blueprint

[![Travis Build Status](https://travis-ci.org/sczyh30/vertx-blueprint-microservice.svg?branch=master)](https://travis-ci.org/sczyh30/vertx-blueprint-microservice)

Vert.x blueprint application - An online shopping microservice application.

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
    - [Vert.x Blueprint - Online shopping microservice practice (Deployment)](http://sczyh30.github.io/vertx-blueprint-microservice/deployment.html)
- 中文版本
    - [Vert.x 蓝图 - Online Shopping 微服务实战(开发篇)](http://sczyh30.github.io/vertx-blueprint-microservice/cn/index.html)
    - [Vert.x 蓝图 - Online Shopping 微服务实战(部署篇)](http://sczyh30.github.io/vertx-blueprint-microservice/cn/deployment.html)

## Architecture

![Microservice Architecture](docs/images/entire-architecture.png)

## Build/Run

To build the code:

    mvn clean install

To run the microservice with Docker Compose:

    ./run.sh

## Contributing

Contributions are definitely welcome !
