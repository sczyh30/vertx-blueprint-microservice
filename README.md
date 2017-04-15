# Vert.x Microservice Blueprint

[![Travis Build Status](https://travis-ci.org/sczyh30/vertx-blueprint-microservice.svg?branch=master)](https://travis-ci.org/sczyh30/vertx-blueprint-microservice)

Vert.x blueprint application - A micro-shop microservice application developed with Vert.x.
This repo is intended to be an illustration on how to design microservice architecture and develop microservice applications using Vert.x.

This blueprint works with Vert.x **3.4.1**.

> Note: We are refactoring the whole architecture of the microservice blueprint.
This can take a long time and the improved new version is expected to be released
by the end of May. See [here](https://github.com/sczyh30/vertx-blueprint-microservice/issues/17) for details.

## Content

- Microservice with Vert.x
- Asynchronous development model
- Reactive and functional patterns
- Event sourcing patterns
- Asynchronous RPC on the clustered event bus
- Various type of services (e.g. REST, message source, event bus service)
- Configuration retriever
- Service discovery
- Circuit breaker
- Polyglot persistence support
- API gateway
- Global authentication (Local/OAuth 2)
- Centralized logging using ELK stack
- Monitoring

## Documentation

Detailed tutorials are available here!

- English Version
    - [Vert.x Blueprint - Micro Shop microservice practice (Development)](http://sczyh30.github.io/vertx-blueprint-microservice/index.html)
    - [Vert.x Blueprint - Micro Shop microservice practice (API Gateway)](http://sczyh30.github.io/vertx-blueprint-microservice/api-gateway.html)
- 中文版本
    - [Vert.x 蓝图 - Micro Shop 微服务实战 (开发篇)](http://sczyh30.github.io/vertx-blueprint-microservice/cn/index.html)
    - [Vert.x 蓝图 - Micro Shop 微服务实战 (API Gateway)](http://sczyh30.github.io/vertx-blueprint-microservice/cn/api-gateway.html)

## Architecture

![Microservice Architecture](docs/images/entire-architecture.png)
  
## Build/Run

First, for `api-gateway` and `monitor-dashboard` component, you have to enter the `src/main/resources/webroot` directory and install the frontend dependencies with **bower**:

```
bower install
```

Then build the code:

```
mvn clean install -Dmaven.test.skip=true
```

To run the microservice with Docker Compose, please refer to the [running instruction](http://www.sczyh30.com/vertx-blueprint-microservice/index.html#show-time-).

## Contributing

Contributions are definitely welcome !
