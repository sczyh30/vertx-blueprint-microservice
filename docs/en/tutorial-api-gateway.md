# What is API Gateway?

Considering our online shopping application, when we visit the product detail page in the frontend, what services will we consume?

![Product Detail Page - SPA](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/shopping-spa-product-detail.png)

From the picture there are various information such as:

- Product details including name, price and number...
- Inventory number of the product
- Related products (recommendation)
- Customer reviews and ranks

These data are from different components like `product-microservice` and `inventory-microservice`. So we have to fetch data from numerous different services. Unfortunately, there are some challenges with this approach:

- The location of each services may change dynamically
- The service protocol might not adapt to the client (e.g. event bus service)
- We might need to refactor, aggregate or split several services, causing many changes

So consume the microservices directly is not a good idea. We need a better approach - [API Gateway](http://microservices.io/patterns/apigateway.html). The API Gateway is a single point of entry from the frontend client. Every requests from the client will first come in API Gateway. Sounds like a simple reverse proxy yeah? That is, but not only a reverse proxy. It's also responsible for load-balancing, authentication and handling failure. With service discovery, we don't need to worry about the location changes of services. Besides, if the internal microservice protocol is not web-friendly, we can adapt the protocols in the API Gateway. For example, `HTTP-Event Bus` or `HTTP-Thrift`.

The API Gateway also has some drawbacks. As the API Gateway has so many important obligations, it must be high available. In addition, we should pay attention to its performance as it might be a bottleneck. But other than that it makes sense to use the API Gateway.

# Circuit Breaker

[Circuit Breaker](http://martinfowler.com/bliki/CircuitBreaker.html) is very useful pattern to handle failure in a distributed environment. It has three states: **CLOSE**, **OPEN** and **HALF-OPEN**, by default it is closed. We can execute some logic in the circuit breaker and every time an execution fails, the failure counter will increase. Once the failure counter reaches a certain threshold, the circuit breaker will be **open**, and all future calls to the circuit breaker will fail immediately. At the same time, a reset timer will start and wait for the timeout (this period of time is for waiting recovery). Once the timeout reaches, the circuit breaker will turn into **half-open** state. In this state, next call to the circuit breaker is allowed. If the call succeeded, we can think the service has recovered from failure so reset the counter and turn into **close** state. If this try fails, the circuit breaker reverts back to the **open** state and wait for next timeout.

Vert.x provides an out-of-box implementation of Circuit Breakr pattern. In our failure-orinted API Gateway, we'll use Vert.x Circuit Breaker to handle failures.

# Implementing API Gateway with Vert.x

In this microservice blueprint, API gateway is as a individual component `api-gateway`. It only has one verticle - `APIGatewayVerticle`. This simple API gateway implementation uses **HTTP-HTTP** pattern. The gateway receives requests from outside and then dispatches them to corresponding endpoints. The gateway itself uses **HTTPS** and inside it communicates with other components using **HTTP**.

As is mentioned above, the gateway is also responsible for:

- Authentication
- Failure handling
- Simple load-balancing
- Simple heart-beat check

## Overview

Let's have a overview of the `APIGatewayVerticle` by exploring the important `start` method:

```java
@Override
public void start(Future<Void> future) throws Exception {
  super.start();

  // get HTTP host and port from configuration, or use default value
  String host = config().getString("api.gateway.http.address", "localhost");
  int port = config().getInteger("api.gateway.http.port", 8787); // (1)

  Router router = Router.router(vertx); // (2)
  // cookie and session handler
  enableLocalSession(router); // (3)

  // body handler
  router.route().handler(BodyHandler.create()); // (4)

  // version handler
  router.get("/api/v").handler(this::apiVersion); // (5)

  // create OAuth 2 instance for Keycloak
  OAuth2Auth oauth2 = OAuth2Auth
    .createKeycloak(vertx, OAuth2FlowType.AUTH_CODE, config());  // (6)

  router.route().handler(UserSessionHandler.create(oauth2)); // (7)

  String hostURI = String.format("https://%s:%d", host, port);
  authHandler = OAuth2AuthHandler.create(oauth2, hostURI)
    .setupCallback(Router.router(vertx).route("/callback")); // (8)

  // set auth callback handler
  router.route("/callback").handler(context -> authCallback(oauth2, hostURI, context)); // (9)

  router.get("/uaa").handler(this::authUaaHandler);
  router.get("/login").handler(this::loginEntryHandler);
  router.post("/logout").handler(this::logoutHandler);  // (10)

  // api dispatcher
  router.route("/api/*").handler(this::dispatchRequests); // (11)

  // init heart beat check
  initHealthCheck(); // (12)

  // static content
  router.route("/*").handler(StaticHandler.create()); // (13)

  // enable HTTPS
  HttpServerOptions httpServerOptions = new HttpServerOptions()
    .setSsl(true)
    .setKeyStoreOptions(new JksOptions().setPath("server.jks").setPassword("123456")); // (14)

  // create http server
  vertx.createHttpServer(httpServerOptions) // (15)
    .requestHandler(router::accept)
    .listen(port, host, ar -> {
      if (ar.succeeded()) {
        publishApiGateway(host, port);
        future.complete();
        logger.info("API Gateway is running on port " + port);
        // publish log
        publishGatewayLog("api_gateway_init_success:" + port);
      } else {
        future.fail(ar.cause());
      }
    });

}
```

## Failure Orinted - using circuit breaker

Let's first consider how to deal with failure in the API gateway. Seeing that we uses `HTTP-HTTP` pattern, if the internal endpoints returns a server error(e.g. **500 Internal Error**), we can think this request as failed. So we can wrap the dispatch logic in the circuit breaker. Once a server error returns, or timeout reaches, the failure counter would increase. If the counter reaches the threshold, the circuit breaker will be open and reset timer starts. As a result, the API Gateway would not accept any requests - just return **502 Bad Gateway**. When the timer triggered, the gateway could allow next request dispatching. If this dispatch succeeds, circuit breaker will be closed and the gateway is recovered from the failure. If this continues to return error, then back to **open** again and wait for next recovery time.

We can abstract the procedure as the following code:

```java
circuitBreaker.execute(future -> {
  client.request(method, path, response -> {
    if (response.statusCode() >= 500) {
      future.fail("Some error");
    } else {
      // write response...
    }
  });
}).setHandler(ar -> {
      if (ar.failed()) {
        badGateway(ar.cause(), context);
      }
    });
```

We'll see the details in the next section.

## Reverse Proxy - dispatching requests

A key part of the API Gateway is reverse proxy. Requests are sent to the API Gateway and then dispatched to corresponding endpoints. Here we make a convention to distinguish the route:

- All paths of api requests are prefixed by `api`. That is, we distinguish the api request with `/api/*` pattern.
- The path variable next to the `api` identifies the API name. For example, `/api/product/*` corresponds the `product` API.
- All HTTP endpoints should have a `api.name` when published to the service discovery layer. That should be configured in the json file and load by Vert.x. Look at the `publishHttpEndpoint` method in `BaseMicroserviceVerticle` class:

```java
protected Future<Void> publishHttpEndpoint(String name, String host, int port) {
  Record record = HttpEndpoint.createRecord(name, host, port, "/",
    new JsonObject().put("api.name", config().getString("api.name", ""))
  );
  return publish(record);
}
```

Here we get the `api.name` config from the Vert.x config(read from file) and set it to the metadata so that we can get records with corresponding api name later.

The config file should be like this:

```json
{
  "api.name": "product"
}
```

Okay, let's now aware of the convention in this API Gateway. So when one request arrives, we first resolve its API name from the path, then find the record with corresponding api name from the service discovery. If none of the records match the name, just return **404 Not Found**. If one or more records match the API name, we then take one, get its client and send a request via the client. Let's take a look of the `dispatchRequests` handler method:

```java
private void dispatchRequests(RoutingContext context) {
  int initialOffset = 5; // length of `/api/`
  // run with circuit breaker in order to deal with failure
  circuitBreaker.execute(future -> { // (1)
    getAllEndpoints().setHandler(ar -> { // (2)
      if (ar.succeeded()) {
        List<Record> recordList = ar.result();
        // get relative path and retrieve prefix to dispatch client
        String path = context.request().uri();

        if (path.length() <= initialOffset) {
          notFound(context);
          future.complete();
          return;
        }
        String prefix = (path.substring(initialOffset)
          .split("/"))[0];
        // generate new relative path
        String newPath = path.substring(initialOffset + prefix.length());
        // get one relevant HTTP client, may not exist
        Optional<HttpClient> client = recordList.stream()
          .filter(record -> record.getMetadata().getString("api.name") != null)
          .filter(record -> record.getMetadata().getString("api.name").equals(prefix)) // (3)
          .map(record -> (HttpClient) discovery.getReference(record).get()) // (4)
          .findAny(); // (5) simple load balance

        if (client.isPresent()) {
          doDispatch(context, newPath, client.get(), future); // (6)
        } else {
          notFound(context); // (7)
          future.complete();
        }
      } else {
        future.fail(ar.cause()); // (8)
      }
    });
  }).setHandler(ar -> {
    if (ar.failed()) {
      badGateway(ar.cause(), context); // (9)
    }
  });
}
```

The dispatch logic are executed in the circuit breaker in order to deal with failure (1). The `execute` method of `CircuitBreaker` accepts a `Handler<Future<T>>`. In the handler we should mark the exectuion success or failure by completing or failing the `future`.

In the dispatch logic, we first get all records of REST endpoints from the service discovery (2). It's very simple. Just get the records whose type is `HttpEndpoint.TYPE`:

```java
private Future<List<Record>> getAllEndpoints() {
  Future<List<Record>> future = Future.future();
  discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE),
    future.completer());
  return future;
}
```

Notice that this is asynchronous, when it fails, the `future` should be failed to (8).

Then comes to the logic of getting the api name `prefix` for the path, rebuild the relative path for endpoint client. After that we need to filter the endpoints with the correct api name (3). Then we map each records to the corresponding HTTP client using `discovery.getReference(record).get()` (4). Now we get a stream of clients so we can use `findAny` operator to get one possible client that matches the api (5). So now we get an `Optional<HttpClient>`, then we should check whether the client exists. If exists, send request to the service and get response using `doDispatch` method with the client (6). And if the client does not exist, that means there aren't any services matches the api so just return **404** and complete the future (7).

Then we need to deal with the failure. The `Future` returned by the circuit breaker refers to the result that executed in the circuit breaker, so we just set a handler on it and when the future fails, we call our wrapped `badGateway` method to respond **Bad Gateway** error (9) and record logs.

OK, now we step into the `doDispatch` method, where requests are dispatched:

```java
private void doDispatch(RoutingContext context, String path, HttpClient client, Future<Object> cbFuture) {
  HttpClientRequest toReq = client
    .request(context.request().method(), path, response -> { // (1)
      response.bodyHandler(body -> {
        if (response.statusCode() >= 500) { // (4) api endpoint server error, circuit breaker should fail
          cbFuture.fail(response.statusCode() + ": " + body.toString());
        } else {
          HttpServerResponse toRsp = context.response()
            .setStatusCode(response.statusCode()); // (5)
          response.headers().forEach(header -> {
            toRsp.putHeader(header.getKey(), header.getValue());
          });
          // send response
          toRsp.end(body); // (6)
          cbFuture.complete(); // (7)
        }
      });
    });
  // set headers
  context.request().headers().forEach(header -> { // (2)
    toReq.putHeader(header.getKey(), header.getValue());
  });
  if (context.user() != null) {
    toReq.putHeader("user-principle", context.user().principal().encode());
  } else {
    toReq.putHeader("redirect-saved", generateAuthRedirectURI());
  }
  // send request
  if (context.getBody() == null) { // (3)
    toReq.end();
  } else {
    toReq.end(context.getBody());
  }
}
```

The given `client` is the HTTP client for corresponding service and `context` for current route context. We send a HTTP request with `client.request(method, path, handler)` method (1). Origin HTTP method and headers from the routing `context` should be kept in the client request (2). Here we should also pass the authentication data to the request, which we'll pay attention to in the next section. The request won't be sent until the `end` method is called, so we should call the `end` method to send the request (3). If the context request has body content, it should also be sent.

Then we can get the response in the response handler of `request` method. We can get the response body via `response.bodyHandler` method. As is mentioned above, if the status code corresponds to server error (5xx), we consider the request failed so the circuit breaker future should be failed as well (4). If the status code is well, we create a server response, set status code and headers (5), then write response to the user client (6). Don't forget to `complete` the circuit breaker future.

Wow! A simple reverse proxy with failure handling is finished! Next let' see how to manage authentication.

## Authentication management

In this microservice blueprint, we use Keycloak as the security component. And with the help of Vert.x OAuth2, we can easily handle authentication with Keycloak in the routing context.

## Simple heart beat check

In this API Gateway implementation, we have a very, very simple heart-beat check mechanism. Every REST endpoints have a route `/health` for health check (simply returns the status). The API Gateway send check requests to all REST endpoints for every `period`. In normal situations, health components will respond with **200 OK** status. If any of the endpoints fails, we consider this time of the health check failed and returns the failed services name.

The logic of doing health check is also executed in the circuit breaker:

```java
private void initHealthCheck() {
  if (config().getBoolean("heartbeat.enable", true)) { // by default enabled
    int period = config().getInteger("heartbeat.period", DEFAULT_CHECK_PERIOD);
    vertx.setPeriodic(period, t -> {
      circuitBreaker.execute(future -> { // behind the circuit breaker
        sendHeartBeatRequest().setHandler(future.completer());
      });
    });
  }
}
```

Let's see the implementation of `sendHeartBeatRequest` logic:

```java
private Future<Object> sendHeartBeatRequest() {
  final String HEARTBEAT_PATH = config().getString("heartbeat.path", "/health");
  return getAllEndpoints()
    .compose(records -> {
      List<Future<JsonObject>> statusFutureList = records.stream()
        .filter(record -> record.getMetadata().getString("api.name") != null)
        .map(record -> { // for each client, send heart beat request
          String apiName = record.getMetadata().getString("api.name");
          HttpClient client = discovery.getReference(record).get();

          Future<JsonObject> future = Future.future();
          client.get(HEARTBEAT_PATH, response -> {
            future.complete(new JsonObject()
              .put("name", apiName)
              .put("status", healthStatus(response.statusCode()))
            );
          })
            .exceptionHandler(future::fail)
            .end();
          return future;
        })
        .collect(Collectors.toList());
      return Functional.sequenceFuture(statusFutureList); // get all responses
    })
    .map(List::stream)
    .compose(statusList -> {
      boolean notHealthy = statusList.anyMatch(status -> !status.getBoolean("status"));

      if (notHealthy) {
        String issues = statusList.filter(status -> !status.getBoolean("status"))
          .map(status -> status.getString("name"))
          .collect(Collectors.joining(", "));

        String err = String.format("Heart beat check fail: %s", issues);
        // publish log
        publishGatewayLog(err);
        return Future.failedFuture(new IllegalStateException(err));
      } else {
        // publish log
        publishGatewayLog("api_gateway_heartbeat_check_success");
        return Future.succeededFuture("OK");
      }
    });
}
```

We've seen a lot of `compose` again as the method is asynchronous and future-based, so very reactive!
We first get all REST endpoints from the service discovery, then for each endpoints with valid `api.name`, get corresponding HTTP client, send a health check request, check response code and set the check result. Notice that all procedures are asynchronous so we get a `List<Future<JsonObject>>`. Now we need to get each result in the list, that is - convert the `List<Future<JsonObject>>` to a `Future<List<JsonObject>>`.
How to do this? Here I implemented a helper method `sequenceFuture` to do this.  It's in the `Functional` class in `io.vertx.blueprint.microservice.common.functional` package:

```java
public static <R> Future<List<R>> sequenceFuture(List<Future<R>> futures) {
  return CompositeFutureImpl.all(futures.toArray(new Future[futures.size()])) // (1)
    .map(v -> futures.stream()
        .map(Future::result) // (2)
        .collect(Collectors.toList()) // (3)
    );
}
```

This method is useful for reducing a sequence of futures into a single `Future` with a list of the results. Here we first invoke `CompositeFutureImpl#all` method (1).
It returns a composite future, succeeds only if every result is successful and fails when any result is failed. Then we transform each `Future` to the corresponding result (2) as the `all` method have forced each `Future` to get results. Finally we collect the result list (3).

So now we've got a `Future` of `List<JsonObject>` and it's time to validate if any endpoint is not active using `anyMatch` operator. If any inactive endpoint exists, we need to collect the damaged endpoint name, publish the log and return a failed future. If all endpoints are active, we publish the success log and return a completed future.
