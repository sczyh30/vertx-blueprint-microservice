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

Vert.x provides an out-of-box implementation of Circuit Breakr pattern. In our failure-orinted API Gateway, we'll use [Vert.x Circuit Breaker](http://vertx.io/docs/vertx-circuit-breaker/java/) to handle failures.

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
  oauth2 = OAuth2Auth.createKeycloak(vertx, OAuth2FlowType.AUTH_CODE, config()); // (6)

  router.route().handler(UserSessionHandler.create(oauth2)); // (7)

  String hostURI = String.format("https://localhost:%d", port);

  // set auth callback handler
  router.route("/callback").handler(context -> authCallback(oauth2, hostURI, context)); // (8)

  router.get("/uaa").handler(this::authUaaHandler);
  router.get("/login").handler(this::loginEntryHandler);
  router.post("/logout").handler(this::logoutHandler); // (9)

  // api dispatcher
  router.route("/api/*").handler(this::dispatchRequests); // (10)

  // init heart beat check
  initHealthCheck(); // (11)

  // static content
  router.route("/*").handler(StaticHandler.create()); // (12)

  // enable HTTPS
  HttpServerOptions httpServerOptions = new HttpServerOptions()
    .setSsl(true)
    .setKeyStoreOptions(new JksOptions().setPath("server.jks").setPassword("123456")); // (13)

  // create http server
  vertx.createHttpServer(httpServerOptions)
    .requestHandler(router::accept)
    .listen(port, host, ar -> { // (14)
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

Wow! Our API Gateway has so many obligations! First we get the host and port of the gateway from `config()` (1). Then we create a router instance (2) and enable cookie and session storage (3) with `enableLocalSession` method. Look at its implementation:

```java
protected void enableLocalSession(Router router) {
  router.route().handler(CookieHandler.create());
  router.route().handler(SessionHandler.create(
    LocalSessionStore.create(vertx, "shopping.user.session")));
}
```

We first set a `CookieHandler` as the session storage depends on cookie. Then we set a `SessionHandler` to the router. The `create` method of `SessionHandler` takes one `SessionStore` as parameter. In Vert.x we have two kinds of `SessionStore`: `LocalSessionStore` and `ClusteredSessionStore`. The former one saves session in the local map, while the other saves session in a distributed map so they are available across the cluster.

After that, we set `BodyHandler` to the router so that we can get the request body (4). We also create a route demonstrating the version of the APIs (5).

As is said above, the API Gateway is responsible for authentication, so here we create a `OAuth2Auth` instance with `createKeycloak` method (6) so that we can manage authentication with Keycloak. Next we set the `UserSessionHandler` to the router (7) so that Vert.x can automatically save the user holder in the session. OAuth 2 authentication also requires a callback handler so here we create a callback route (8). We'll elaborate this in the auth section. Then we create a series routes about auth: `authUaaHandler` for getting current user, `loginEntryHandler` for login redirect and `logoutHandler` for logout (9). Vert.x Web provides us a helper handler `AuthHandler` which can also do this well, but in order to integrate with the SPA and implement fine-grained permission, here we don't use the `AuthHandler`.

Then we create the route for dispatcher (reverse proxy) (10) and enable health check with `initHealthCheck` method (11). In addition, our SPA frontend has been integrated with the API Gateway so we should also handle static content with `StaticHandler` (12). This route should be in low priority so we put it in the end. In order to illustrate usage of **HTTPS**, here we create a `HttpServerOptions` and configure it with `HTTPS` options (13). Finally we create the server with given `HttpServerOptions` (14). If successfully created, we publish the API Gateway to the service discovery layer, complete the `future` and publish success log. If failed, we need to fail the `future`.

Well, that's done. Now it's time to explore each functionality~

## Fault-tolerance support - using Circuit Breaker

> Note: this part will have a big change in next version.

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

> Note: this part will have a big change in next version.

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
        Optional<Record> client = recordList.stream()
          .filter(record -> record.getMetadata().getString("api.name") != null)
          .filter(record -> record.getMetadata().getString("api.name").equals(prefix)) // (3)
          .findAny(); // (4) simple load balance

        if (client.isPresent()) {
          doDispatch(context, newPath, discovery.getReference(client.get()).get(), future); // (5)
        } else {
          notFound(context); // (6)
          future.complete();
        }
      } else {
        future.fail(ar.cause()); // (8)
      }
    });
  }).setHandler(ar -> {
    if (ar.failed()) {
      badGateway(ar.cause(), context); // (7)
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

Then comes to the logic of getting the api name `prefix` for the path, rebuild the relative path for endpoint client. After that we need to filter the endpoints with the correct api name (3). Now we get a stream of service records so we can use `findAny` operator to get one possible service record that matches the API (4). The `findAny` can be the entry of simple load-balancing, and we can implement our own load-balancing logic to manipulate the stream and get one client. So now we get an `Optional<Record>`, then we should check whether the client exists. If exists, we get the HTTP client instance from the record via `discovery.getReference(record).get()`, then send request to the service and get response using `doDispatch` method with the client (5). And if the client does not exist, that means there aren't any services matches the API so just return **404** and complete the future (6).

Then we need to deal with the failure. The `Future` returned by the circuit breaker refers to the result that executed in the circuit breaker, so we just set a handler on it and when the future fails, we call our wrapped `badGateway` method to respond **Bad Gateway** error (7) and record logs.

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
        ServiceDiscovery.releaseServiceObject(discovery, client);
      });
    });
  // set headers
  context.request().headers().forEach(header -> { // (2)
    toReq.putHeader(header.getKey(), header.getValue());
  });
  if (context.user() != null) {
    toReq.putHeader("user-principal", context.user().principal().encode());
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

> Note: Don't forget to release the resources of the HTTP endpoint. We can call `ServiceDiscovery.releaseServiceObject(discovery, object)` method to release the resource.

Wow! A simple reverse proxy with failure handling is finished! Though this is a simple implementation, you may extend it for your concrete demand. And of course you can use Nginx for reverse proxy and load balancing if you like!

Next let's see how to manage authentication in the API Gateway.

## Authentication management

> Note: this part will have a big change in next version.

In this microservice blueprint, we use Keycloak as the security component. And with the help of Vert.x OAuth2, we can easily handle authentication with Keycloak in the routing context.

As is said above, we create a `OAuth2Auth` instance for Keycloak with `createKeycloak` method:

```java
oauth2 = OAuth2Auth.createKeycloak(vertx, OAuth2FlowType.AUTH_CODE, config());
```

Here the OAuth 2 flow type is [Authorization Code Flow](http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1). The authorization code grant type is used to obtain both access tokens and refresh tokens and is optimized for confidential clients. And we need to get the Keycloak configuration from the outside config file. To see how to configure Keycloak and get the JSON configuration, please see [here](http://www.sczyh30.com/vertx-blueprint-microservice/index.html#show-time-).

In our micro-shop application, some APIs are protected and need authentication. The routing handler of these APIs are wrapped with `requireLogin` method. For example, in shopping cart microservice the cart operations require authentication:

```java
router.post(API_CHECKOUT).handler(context -> requireLogin(context, this::apiCheckout));
```

The `requireLogin` method wraps logic about authentication principal:

```java
protected void requireLogin(RoutingContext context, BiConsumer<RoutingContext, JsonObject> biHandler) {
  Optional<JsonObject> principal = Optional.ofNullable(context.request().getHeader("user-principal"))
    .map(JsonObject::new);
  if (principal.isPresent()) {
    biHandler.accept(context, principal.get());
  } else {
    context.response()
      .setStatusCode(401)
      .end(new JsonObject().put("message", "need_auth").encode());
  }
}
```

The `requireLogin` method takes a `RoutingContext` as the context, and a `BiConsumer<RoutingContext, JsonObject>` as the routing handler. In the original route, the type of handler is `Handler<RoutingContext>`, but here there is an additional `JsonObject` parameter that refers to the user principal. We can get user authentication data from the principal.

As we've seen above from the reverse proxy part, if there is a user existing in the routing context, we'll put its principal into the header with name `user-principal`. Here we make use of the header. We first get the principal and convert it to the `JsonObject`. If the principal exists, we pass the context and principal to the routing handler. By contrast, if the principal is not present, we'll return a response with `401` status indicating that authentication is needed.

The Keycloak is responsible for login management, so we implemented a `loginEntryHandler` to redirect the request to the login entry:

```java
private void loginEntryHandler(RoutingContext context) {
  String from = Optional.ofNullable(context.request().getParam("from"))
    .orElse("https://localhost:8787");
  context.response()
    .putHeader("Location", generateAuthRedirectURI(from))
    .setStatusCode(302)
    .end();
}
```

We implement redirection by set the header `Location` with the destination and set status code with **302 Redirect**. But how can we get the destination? That is from `generateAuthRedirectURI` method:

```java
private String generateAuthRedirectURI(String from) {
  int port = config().getInteger("api.gateway.http.port", 8787);
  return oauth2.authorizeURL(new JsonObject()
    .put("redirect_uri", "https://localhost:" + port + "/callback?redirect_uri=" + from)
    .put("scope", "")
    .put("state", ""));
}
```

Here we make use of the `authorizeURL` method of `oauth2` to get the login entry. Here a `redirect_uri` parameter is needed and the format is like `host:port/callback?redirect_uri=xxx`. Notice that our auth callback path is `/callback`, let's see its implementation:

```java
private void authCallback(OAuth2Auth oauth2, String hostURL, RoutingContext context) {
  final String code = context.request().getParam("code"); // (1)
  // code is a require value
  if (code == null) {
    context.fail(400);
    return;
  }
  final String redirectTo = context.request().getParam("redirect_uri"); // (2)
  final String redirectURI = hostURL + context.currentRoute().getPath() + "?redirect_uri=" + redirectTo; // (3)
  oauth2.getToken(new JsonObject().put("code", code).put("redirect_uri", redirectURI), ar -> { // (4)
    if (ar.failed()) {
      logger.warn("Auth fail");
      context.fail(ar.cause()); // (5)
    } else {
      logger.info("Auth success");
      context.setUser(ar.result()); // (6)
      context.response()
        .putHeader("Location", redirectTo) // (7)
        .setStatusCode(302)
        .end();
    }
  });
}
```

In the OAuth 2 standard, a `code` parameter is required (1). If the `code` is not present, the request will fail. Then we get the `redirect_uri` destination URL from the URI parameter (2) and aggregate present URI (3). Then we could retrieve the user token by calling `oauth2.getToken(params, handler)` asynchronous method (4). We need to put the `code` and `redirect_uri` (actually current URI) into the parameters. If we successfully get the token, which means authentication succeeds, we'll set the result `User` to the current routing context (6) and redirect to the final destination path (7) (here is often the frontend page). If authentication failed, we should fail the request with `context.fail` method (5).

The `authCallback` and `generateAuthRedirectURI` method is actually from the `AuthHandler` class provided by Vert.x. It provides out-of-box encapsulation of simple authentication handler. You can visit the [Documentation](http://vertx.io/docs/vertx-web/java/#_authentication_authorisation) for more details.

Now that we have a login handler, there must be a logout handler. The logout operation is simple:

```java
private void logoutHandler(RoutingContext context) {
  context.clearUser();
  context.session().destroy();
  context.response().setStatusCode(204).end();
}
```

We just clear the context user with `clearUser` method, then destroy the current session and respond with *204* status.

## Heart beat check

> Note: this part will have a big change in next version.