# Preface

Hi, welcome back to the Vert.x Blueprint tutorial series! Nowadays, **microservice architecture** is becoming more and more popular and everyone is willing to have a try with microservice application development.
Very excited, Vert.x provides us a very useful microservice toolbox including **service discovery**, **circuit breaker** and so on.
With the help of Vert.x microservice components, we can establish our microservice application easily.
In this tutorial, we are going to explore a complete online shopping microservice application developed with Vert.x~

What you are going to learn:

- How to develop microservices with Vert.x
- Asynchronous development pattern
- Reactive and functional pattern
- Event sourcing pattern
- Asynchronous RPC on the clustered event bus
- Various type of services (e.g. HTTP endpoint, message source, data source)
- Service discovery with Vert.x
- How to configure the microservice more flexibly
- How to use Vert.x Circuit Breaker
- How to implement a simple API Gateway
- How to manage global authentication using OAuth 2
- How to configure and use SockJS - Event Bus Bridge

And many more things too...

This is the third part of [**Vert.x Blueprint Project**](http://vertx.io/blog/vert-x-blueprint-tutorials/). The entire code is available on [GitHub](https://github.com/sczyh30/vertx-blueprint-microservice/tree/master).

# Introduction to microservice

Aha~ You must be familiar -- at least sounds familiar with the word "microservice". More and more developers are embracing fine-grained microservice architecture. So what are microservices? In brief:

> Microservices are small, autonomous services that work together.

Let's step across the definition and see what makes microservices different.

- First of all, microservices are small, individual, each of which focus on doing one specific thing. We split the monolithic application into several decoupled components. We focus our service boundaries on business boundaries so that the service won't grow too large. But you may wonder, **how small is small?** That is hard to answer and that always depends on your application. As Sam Newman says in the book *Building
Microservices*:

> We seem to have a very good sense of what is too big, and so it could be argued that once a piece of code no longer feels too big, it’s probably small enough.

- In microservice architecture, components can interact between each other via whatever protocol, e.g REST, Thrift.
- As components are individual, we can use different language, different technologies in different components -- that is so-called **polyglot support**.
- Each component is developed, deployed and delivered independently, so it reduces the complexity of deployment.
- Microservice architecture is usually inseparable from distributed systems, so we need to think of resilience and scaling.
- Microservices are often designed as **Failure Oriented** as the failure is more complicated in the distributed systems.

Microservices can ensure the cohesion between each components and reduce the time to deployment and production. But remember: microservices are not a silver bullet as it increases the complexity of the whole distributed system so you need to think of more circumstances.

## Service discovery

In distributed systems, each components are individual and they are not aware of the location of other services, but if we want to invoke other services, we need to know their locations. Hardcoded in the code is not a good idea so we need a mechanism to record the location of each services dynamically -- that is **service discovery**. With service discovery, we can publish various kind of services to the discovery infrastructure and other components can consume registered services via discovery infrastructure. We don't need to know the location so it could let your components react smoothly to location or environment changes.

Vert.x provides us a service discovery component to publish and discover various resources. In Vert.x Service Discovery, services are described by a `Record`. Service provider can publish services, and the `Record` can be saved in local map, distributed map or Redis depending on `ServiceDiscoveryBackend`. Service consumer can retrieve service record from the discovery backend and get corresponding service instance. At present Vert.x provides out of box support of several service types such as **event bus service(service proxy)**, **HTTP endpoint**, **message source** and **data source**. And of course we can create our own service types. We'll elaborate the usage of service discovery soon.

## Asynchronous and reactive Vert.x

Asynchronous and reactive is very suitable for microservices, and Vert.x owes both of them! With `Future` based and Rx based asynchronous development model, we can compose asynchronous procedures in a reactive way. That's concise and nice! We'll see more usage of `Future` based and Rx based asynchronous methods later~

# The Micro Shop application

Ok, now that you've had a basic understanding of microservice architecture, let's discuss our microservice application in this blueprint. This is a micro-shop application like eBay. People can buy things via it... The application contains a set of microservices currently:

- **Account microservice** - provides user account operation functionality. Use MySQL as persistence.
- **Product microservice** - provides product operation functionality. Use MySQL as persistence.
- **Inventory microservice** - provides product inventory operation functionality, e.g. `retrieve`, `increase` and `decrease`. Use Redis as persistence.
- **Store microservice** - provides personal shop operation functionality. Use MongoDB as persistence.
- **Shopping cart microservice** - it manages the shopping cart operations (e.g. `add`, `remove` and `checkout`) and generates orders. Shopping carts are stored and retrieved with event sourcing pattern. Orders are sent on the event bus.
- **Order microservice(dispatcher and processor)** - it receives order requests from the cart service via event bus and then dispatch the orders to the infrastructure services (e.g. processing, storage and logging).
- **The Micro Shop SPA** - the frontend SPA of the microservice (now integrated with `api-gateway`)
- **The monitor dashboard** - a simple web UI to monitor the status of the microservice system
- **The API Gateway** - The API gateway is the door of the entire system. Every requests must be first sent to the gateway and then dispatched to each service endpoints (reverse proxy). It is also responsible for authentication, simple load-balancing and failure handling (using Vert.x Circuit Breaker).

## Online shopping microservice architecture

Let's have a look to the microservice architecture:

![Microservice Architecture](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/entire-architecture.png)

Let's then see the structure of every individual component.

## Component structure

Every high-level component contains at least two verticles: Service verticle and REST verticle. REST verticle provides REST endpoint of the service as well as publish it to the service discovery infrastructure. The service verticle is responsible for publishing event bus services, message sources to the service discovery infrastructure and then deploy REST verticles.

We have services in each component, for example `ProductService` for `product-microservice`. These kind of service interfaces are all event bus services, which are with `@ProxyGen` annotation. With `@ProxyGen` annotation, Vert.x can automatically generate service proxies so that we could do asynchronous RPC on event bus without any extra code. So cool!

![Component structure](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/ind-structure-01.png)

## Interaction between components

The application uses several types of services:

- **HTTP endpoint** (e.g. REST endpoint and gateway) - the service is located using an HTTP URL
- **Event bus service** - as we've mentioned above, we can do async RPC to consume event bus services (aka. service proxies) via the event bus. The service is located using an event bus address.
- **Message source** - this kind of service publishes messages to specific addresses on event bus. The service is located using an event bus address.

So these components can interact with each other via HTTP or event bus like this picture:

![Interaction](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/rpc-inc-1.png)

# Let's start!

Now let's start our journey with this blueprint! First we clone the project from GitHub:

```
git clone https://github.com/sczyh30/vertx-blueprint-microservice.git
```

In this tutorial we use **Maven** as the build tool. Let's first look at the `pom.xml` config file. From it we can see our blueprint is composed of several subprojects:

```xml
<modules>
  <module>microservice-blueprint-common</module>
  <module>account-microservice</module>
  <module>product-microservice</module>
  <module>inventory-microservice</module>
  <module>store-microservice</module>
  <module>shopping-cart-microservice</module>
  <module>order-microservice</module>
  <module>api-gateway</module>
  <module>cache-infrastructure</module>
  <module>monitor-dashboard</module>
</modules>
```

Wow! So many! Don't worry, I'll explain most of them. Now let's first take a loot on the `microservice-blueprint-common` module.

# Blueprint common module

`microservice-blueprint-common` module provides some helper classes and base verticles. Let's first see two base verticles - `BaseMicroserviceVerticle` and `RestAPIVerticle`.

## Base microservice verticle

`BaseMicroserviceVerticle` is a base verticle that provides helper methods and support for various microservice functionality like service discovery, circuit breaker and simple log publisher. Every other verticle will extend this verticle.

We have three fields in the verticle:

```java
protected ServiceDiscovery discovery;
protected CircuitBreaker circuitBreaker;
protected Set<Record> registeredRecords = new ConcurrentHashSet<>();
```

The `registeredRecords` is a set of records that published by current verticle. We need to save the records in order to unpublish them when the verticle is undeployed.

The `start` methods does initialization for the `discovery` and `circuitBreaker`. And we fetch configurations from `config()`. It's very simple:

```java
@Override
public void start() throws Exception {
  // init service discovery instance
  discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));

  // init circuit breaker instance
  JsonObject cbOptions = config().getJsonObject("circuit-breaker") != null ?
    config().getJsonObject("circuit-breaker") : new JsonObject();
  circuitBreaker = CircuitBreaker.create(cbOptions.getString("name", "circuit-breaker"), vertx,
    new CircuitBreakerOptions()
      .setMaxFailures(cbOptions.getInteger("max-failures", 5))
      .setTimeout(cbOptions.getLong("timeout", 10000L))
      .setFallbackOnFailure(true)
      .setResetTimeout(cbOptions.getLong("reset-timeout", 30000L))
  );
}
```

Then we provide several helper methods to publish various kind of services. These methods are all asynchronous and future-based:

```java
protected Future<Void> publishHttpEndpoint(String name, String host, int port) {
  Record record = HttpEndpoint.createRecord(name, host, port, "/",
    new JsonObject().put("api.name", config().getString("api.name", ""))
  );
  return publish(record);
}

protected Future<Void> publishMessageSource(String name, String address) {
  Record record = MessageSource.createRecord(name, address);
  return publish(record);
}

protected Future<Void> publishJDBCDataSource(String name, JsonObject location) {
  Record record = JDBCDataSource.createRecord(name, location, new JsonObject());
  return publish(record);
}

protected Future<Void> publishEventBusService(String name, String address, Class serviceClass) {
  Record record = EventBusService.createRecord(name, address, serviceClass);
  return publish(record);
}
```

As we've mentioned above, a record represent a service. Service types are distinguished by the `type` field in the record. Every service types provided by Vert.x contains several `createRecord` methods so we can create service records easily. Generally we need to give every service a proper name so that we can retrieve it by name. We can also set extra data using `Record#setMetadata(metadata)` method.

You may notice that we provide meta data with `api-name` in `publishHttpEndpoint` method. This data is for API gateway component. We'll explain that later.

Next let's see `publish` method:

```java
private Future<Void> publish(Record record) {
  Future<Void> future = Future.future();
  // publish the service
  discovery.publish(record, ar -> {
    if (ar.succeeded()) {
      registeredRecords.add(record);
      logger.info("Service <" + ar.result().getName() + "> published");
      future.complete();
    } else {
      future.fail(ar.cause());
    }
  });
  return future;
}
```

Inside the `publish` method, we use `discovery.publish(record, handler)` method to publish service record into the discovery infrastructure. This is also an asynchronous method and when it succeeds, we save the record to the record set, print the log info and then complete the `future`. Finally return the `future`.

You may wonder why we wrap the `publish` method as future-based. That is because `Future` is composable so we can compose several futures in a reactive way. This is really more convenient and concise than the callback-based asynchronous model. In the following chapters you'll see more of the benefits.

In the current design of Vert.x Service Discovery, it's the responsibility for the publisher verticle to remove the service when necessary. So we need to unpublish the service records when the verticle is undeployed:

```java
@Override
public void stop(Future<Void> future) throws Exception {
  // In current design, the publisher is responsible for removing the service
  List<Future> futures = new ArrayList<>();
  for (Record record : registeredRecords) {
    Future<Void> unregistrationFuture = Future.future();
    futures.add(unregistrationFuture);
    discovery.unpublish(record.getRegistration(), unregistrationFuture.completer());
  }

  if (futures.isEmpty()) {
    discovery.close();
    future.complete();
  } else {
    CompositeFuture.all(futures)
      .setHandler(ar -> {
        discovery.close();
        if (ar.failed()) {
          future.fail(ar.cause());
        } else {
          future.complete();
        }
      });
  }
}
```

In `stop` method, we traverse the `registeredRecords` and try to unpublish every record then add the `Future` to the future list. Then we invoke `CompositeFuture.all(futures)` to fold all futures. The method return a composite future, succeeded when all futures are succeeded, failed when any future is failed. We set a handler to the composite future and only if every unpublish result is successful, the `discovery` can be closed directly, or the `stop` procedure will fail.

## REST API verticle

The `RestAPIVerticle` is an abstract base `BaseMicroserviceVerticle` that supplies several helper methods for REST API. We encapsulated some useful methods such as create server, enable cookie and session, enable heart-beat check, context result handlers and auth handler. We'll explain some of the methods in the upcoming chapters.

So now that we're aware of the two base verticles, it's time to march into every component! But before we explore the shopping components, let's first take a look at the essential component - API Gateway.

# API Gateway

See here: [Vert.x Blueprint - Online Shopping Microservice Practice (API Gateway)](http://sczyh30.github.io/vertx-blueprint-microservice/api-gateway.html).

# Event bus services - Account, store and product service

## Asynchronous RPC on event bus

We have introduced asynchronous RPC (aka. service proxy) in the previous blueprint [Vert.x Blueprint - Vert.x Kue (Core)](http://www.sczyh30.com/vertx-blueprint-job-queue/kue-core/index.html#async-rpc) and here let's have a recall~

With RPC, a component can send messages to another component by doing a local procedure call. But traditional RPC has a drawback: the caller has to wait until the response from the callee has been received, which does not fit for Vert.x asynchronous model. In addition, the traditional RPC isn't really **Failure-Oriented**. Thanks to Vert.x, we can do **asynchronous RPC** on the (clustered) event bus. With async RPC, we don't have to wait for the response, but only need to pass a `Handler<AsyncResult<R>>` to the method and when the result arrives, the handler will be called. That corresponds to the asynchronous model of Vert.x.

Vert.x Service Proxy can automatically generate service proxy classes from the service interface with `@ProxyGen` (aka. event bus service). It can prevent us from sending and consuming data from the event bus, handle timeout and decoding data manually. What we should care is to obey [the constraints](http://vertx.io/docs/vertx-service-proxy/java/#_restrictions_for_service_interface) of `@ProxyGen` annotation.

For example, there is a event bus service interface:

```java
@ProxyGen
public interface MyService {
  @Fluent
  MyService retrieveData(String id, Handler<AsyncResult<JsonObject>> resultHandler);
}
```

We can register the service on event bus by calling `registerService` method in `ProxyHelper` class:

```java
MyService myService = MyService.createService(vertx, config);
ProxyHelper.registerService(MyService.class, vertx, myService, SERVICE_ADDRESS);
```

With service discovery, it's convenient to publish the event bus service to the discovery infrastructure using our wrapped `publishEventBusService` method or original `publish` method in `ServiceDiscovery`:

```java
publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, MyService.class)
```

Well, the register has been done. Now we can do asynchronous RPC in another component like this:

```java
EventBusService.<MyService>getProxy(discovery, new JsonObject().put("name", SERVICE_NAME), ar -> {
  if (ar.succeeded()) {
    MyService myService = ar.result();
    myService.retrieveData(...);
  }
});
```

With `EventBusService.getProxy` method, we could easily get our service via the discovery infrastructure, then call it like a *local procedure call* (in fact RPC)!

## Common features

The account, store and product microservices have some features and regulations in common. Let's give a summary.

Every component of the three microservices contains:

- An event bus service interface. The service defines entity operations for database.
- Implementation of the service interface.
- A REST API verticle that exposes HTTP server and publishes REST endpoints to the discovery infrastructure.
- A main verticle that deploys the REST verticle and publishes event bus services to the discovery infrastructure.

The product microservice and account microservice uses **MySQL** as backend persistence, while the store microservice uses **MongoDB**. In this chapter we'll have a look of `product-microservice` and `store-microservice` to illustrate the component structure and operations for database. The structure of `account-microservice` is similar to `product-microservice` and you can refer to the code on [GitHub](https://github.com/sczyh30/vertx-blueprint-microservice/tree/master/account-microservice).

## Product microservice with MySQL

The product microservice provides functionality for managing products. The key part is `ProductService` interface and its implementation. Let's first take a look on the service interface:

```java
@VertxGen
@ProxyGen
public interface ProductService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "product-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.product";

  /**
   * Initialize the persistence.
   */
  @Fluent
  ProductService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

  /**
   * Add a product to the persistence.
   */
  @Fluent
  ProductService addProduct(Product product, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Retrieve the product with certain `productId`.
   */
  @Fluent
  ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler);

  /**
   * Retrieve the product price with certain `productId`.
   */
  @Fluent
  ProductService retrieveProductPrice(String productId, Handler<AsyncResult<JsonObject>> resultHandler);

  /**
   * Retrieve all products.
   */
  @Fluent
  ProductService retrieveAllProducts(Handler<AsyncResult<List<Product>>> resultHandler);

  /**
   * Retrieve products by page.
   */
  @Fluent
  ProductService retrieveProductsByPage(int page, Handler<AsyncResult<List<Product>>> resultHandler);

  /**
   * Delete a product from the persistence
   */
  @Fluent
  ProductService deleteProduct(String productId, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Delete all products from the persistence
   */
  @Fluent
  ProductService deleteAllProducts(Handler<AsyncResult<Void>> resultHandler);

}
```

As we've mentioned above, this is an **event bus service** so it is annotated with `@ProxyGen` annotation. These methods are asynchronous so they need to accept a `Handler<AsyncResult<T>>`. When the invocation is ready, the handler will be called. Notice that there is also a `@VertxGen` annotation. As we've explained in the previous blueprint tutorial, this is for **polyglot support**. Vert.x Codegen will process the class with `@VertxGen` annotation and generate polyglot code such as JavaScript, Ruby... This is very useful and fit for the microservice architecture!

The illustrations of the logic methods are given in the comment.

The implementation of the product service is in `ProductServiceImpl` class. The products are stored in MySQL so we can operate the database with **Vert.x-JDBC**! We have introduced the detail usage of Vert.x JDBC in [the first blueprint tutorial](http://sczyh30.github.io/vertx-blueprint-todo-backend/) so here we don't take about more details. Here we pay attention to reducing the code! Recall the procedure of a database operation:

1. Get the `SQLConnection` from the JDBC client
2. Execute the SQL statement and attach a result handler
3. Don't forget to close the `SQLConnection` in the end

So for common `CRUD` operations, the implementations are very similar so let's encapsulate these operations in a `JdbcRepositoryWrapper` class. It is in `io.vertx.blueprint.microservice.common.service` package:

![JdbcRepositoryWrapper class structure](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/jdbc-repo-wrapper-class-structure.png)

We provide the following encapsulated methods:

- `executeNoResult`: execute the prepared statement with parameters (using `updateWithParams`). The result is not needed so just accepts a `Handler<AsyncResult<Void>>`. This is useful for operations such as `insert`.
- `retrieveOne`: retrieve one entity with the given prepared statement and one query parameter (always the primary key or index key) using `queryWithParams`. This method returns a `Future<Optional<JsonObject>>` as the asynchronous result. If the result list is empty, returns an `empty` optional monad, or returns the first item wrapped with `Optional` monad.
- `retrieveMany`: retrieve several entities and returns a `Future<List<JsonObject>>`.
- `retrieveByPage`: similar to `retrieveMany` method but with pagination.
- `retrieveAll`: similar to `retrieveMany` method but does not require query parameters as it simply executes statement such as `SELECT * FROM xx_table`.
- `removeOne` and `removeAll`: remove entity from the database.

So the `JdbcRepositoryWrapper` could reduce many duplicate codes of components using Vert.x JDBC. For example, our `ProductServiceImpl` class could simply extend `JdbcRepositoryWrapper` class and make use of these encapsulated methods. Take a look of `retrieveProduct` method:

```java
@Override
public ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler) {
  this.retrieveOne(productId, FETCH_STATEMENT)
    .map(option -> option.map(Product::new).orElse(null))
    .setHandler(resultHandler);
  return this;
}
```

The only thing we need is to map the result to our expected type. Very convenient yeah?

This is only a simple approach to eliminate duplicate code. In the following section, you'll see a more reactive solution - use Rx version of Vert.x JDBC Client. And using `vertx-sync` is also a good idea!

As we've mentioned above, every basic component in this blueprint has a REST verticle and their structures are similar. Let's take `RestProductAPIVerticle` as example:

```java
public class RestProductAPIVerticle extends RestAPIVerticle {

  public static final String SERVICE_NAME = "product-rest-api";

  private static final String API_ADD = "/add";
  private static final String API_RETRIEVE = "/:productId";
  private static final String API_RETRIEVE_BY_PAGE = "/products";
  private static final String API_RETRIEVE_PRICE = "/:productId/price";
  private static final String API_RETRIEVE_ALL = "/products";
  private static final String API_DELETE = "/:productId";
  private static final String API_DELETE_ALL = "/all";

  private final ProductService service;

  public RestProductAPIVerticle(ProductService service) {
    this.service = service;
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // API route handler
    router.post(API_ADD).handler(this::apiAdd);
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.get(API_RETRIEVE_BY_PAGE).handler(this::apiRetrieveByPage);
    router.get(API_RETRIEVE_PRICE).handler(this::apiRetrievePrice);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdate);
    router.delete(API_DELETE).handler(this::apiDelete);
    router.delete(API_DELETE_ALL).handler(context -> requireLogin(context, this::apiDeleteAll));

    enableHeartbeatCheck(router, config());

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("product.http.address", "0.0.0.0");
    int port = config().getInteger("product.http.port", 8082);

    // create HTTP server and publish REST service
    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiAdd(RoutingContext context) {
    try {
      Product product = new Product(new JsonObject(context.getBodyAsString()));
      service.addProduct(product, resultHandler(context, r -> {
        String result = new JsonObject().put("message", "product_added")
          .put("productId", product.getProductId())
          .encodePrettily();
        context.response().setStatusCode(201)
          .putHeader("content-type", "application/json")
          .end(result);
      }));
    } catch (DecodeException e) {
      badRequest(context, e);
    }
  }

  private void apiRetrieve(RoutingContext context) {
    String productId = context.request().getParam("productId");
    service.retrieveProduct(productId, resultHandlerNonEmpty(context));
  }

  private void apiRetrievePrice(RoutingContext context) {
    String productId = context.request().getParam("productId");
    service.retrieveProductPrice(productId, resultHandlerNonEmpty(context));
  }

  private void apiRetrieveByPage(RoutingContext context) {
    try {
      String p = context.request().getParam("p");
      int page = p == null ? 1 : Integer.parseInt(p);
      service.retrieveProductsByPage(page, resultHandler(context, Json::encodePrettily));
    } catch (Exception ex) {
      badRequest(context, ex);
    }
  }

  private void apiRetrieveAll(RoutingContext context) {
    service.retrieveAllProducts(resultHandler(context, Json::encodePrettily));
  }

  private void apiDelete(RoutingContext context) {
    String productId = context.request().getParam("productId");
    service.deleteProduct(productId, deleteResultHandler(context));
  }

  private void apiDeleteAll(RoutingContext context, JsonObject principle) {
    service.deleteAllProducts(deleteResultHandler(context));
  }

}
```

The verticle extends `RestAPIVerticle` so that we can make use of helper methods in it. In the important `start` method, we first call `super.start()` to initialize discovery, then we create the `Router`, set `BodyHandler` to operate request body, and create API routes. Next we `enableHeartbeatCheck` so that the API gateway can ensure if the endpoint is active. Then we use the `createHttpServer` method to create HTTP server and publish REST endpoint by `publishHttpEndpoint` method.

The `createHttpServer` method is simple. It just wraps `vertx.createHttpServer` method as future-based style:

```java
protected Future<Void> createHttpServer(Router router, String host, int port) {
  Future<HttpServer> httpServerFuture = Future.future();
  vertx.createHttpServer()
    .requestHandler(router::accept)
    .listen(port, host, httpServerFuture.completer());
  return httpServerFuture.map(r -> null);
}
```

The REST content is about Vert.x Web and you can refer to [Vert.x Blueprint - Todo Backend Tutorial](http://www.sczyh30.com/vertx-blueprint-todo-backend/#rest-api-with-vert-x-web) for more tutorials.

Finally let's open `ProductVerticle` class - the main verticle Vert.x Launcher will run. As is mentioned above, the main verticle is responsible for publishing services and deploying REST verticles, so let's see the `start` method:

```java
@Override
public void start(Future<Void> future) throws Exception {
  super.start();

  // create the service instance
  ProductService productService = new ProductServiceImpl(vertx, config()); // (1)
  // register the service proxy on event bus
  ProxyHelper.registerService(ProductService.class, vertx, productService, SERVICE_ADDRESS); // (2)
  // publish the service in the discovery infrastructure
  initProductDatabase(productService) // (3)
    .compose(databaseOkay -> publishEventBusService(ProductService.SERVICE_NAME, SERVICE_ADDRESS, ProductService.class)) // (4)
    .compose(servicePublished -> deployRestService(productService)) // (5)
    .setHandler(future.completer()); // (6)
}
```

In (1), we first create a product service instance (1). Then we use `registerService` method to register the service on event bus so that other components could consume the service remotely (2). Next we initialize the database table (3), publish the product service into discovery infrastructure (4) and deploy the REST verticle (5). This is a sequence of composition of asynchronous procedure :-) We set `future.completer()` to the composed future (6) so when all are ready, the future will be assigned so the deployment of the main verticle finishes.

## Store microservice with MongoDB

Our microservice application is an online shopping application like eBay, so every one can open online shops to sell their favorite things! The store microservice is responsible for online shops management. The structure of the store microservice is quite the same as the product microservice, so we don't elaborate more here. We just simply have a glimpse on how to use Vert.x Mongo Client to operate Mongo.

Using Vert.x Mongo Client is quite simple. We first need to create a `MongoClient`:

```java
private final MongoClient client;

public StoreCRUDServiceImpl(Vertx vertx, JsonObject config) {
  this.client = MongoClient.createNonShared(vertx, config);
}
```

Ok, now we can operate the MongoDB via the client. For example, we want to save an online shop into the db, we can write:

```java
@Override
public void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler) {
  client.save(COLLECTION, new JsonObject().put("_id", store.getSellerId())
      .put("name", store.getName())
      .put("description", store.getDescription())
      .put("openTime", store.getOpenTime()),
    ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    }
  );
}
```

Like other Vert.x APIs, the methods in `MongoClient` are asynchronous so you must be familar with this! The original API is also callback-based. But almost every component in Vert.x provides a Rx version API, wo if you want to be more reactive, you can also use Rx-fied APIs!

For the details of Vert.x Mongo Client, please refer to the [Documentation](http://vertx.io/docs/vertx-mongo-client/java/).

# Inventory microservice with Redis

The inventory service is responsible for operations about product inventory, e.g. `retrieve` inventory of a product, `increase` or `decrease` inventory amount.

Different from the previous event bus service, the inventory service interface is not callback-based, but future-based. The service proxy does not support processing future-based asynchronous method, hence we'd only publish a HTTP endpoint.

We use **Redis** as the persistence of product inventory. Let's first look at `InventoryService` interface:

```java
public interface InventoryService {

  /**
   * Create a new inventory service instance.
   *
   * @param vertx  Vertx instance
   * @param config configuration object
   * @return a new inventory service instance
   */
  static InventoryService createService(Vertx vertx, JsonObject config) {
    return new InventoryServiceImpl(vertx, config);
  }

  /**
   * Increase the inventory amount of a certain product.
   *
   * @param productId the id of the product
   * @param increase  increase amount
   * @return the asynchronous result of current amount
   */
  Future<Integer> increase(String productId, int increase);

  /**
   * Decrease the inventory amount of a certain product.
   *
   * @param productId the id of the product
   * @param decrease  decrease amount
   * @return the asynchronous result of current amount
   */
  Future<Integer> decrease(String productId, int decrease);

  /**
   * Retrieve the inventory amount of a certain product.
   *
   * @param productId the id of the product
   * @return the asynchronous result of current amount
   */
  Future<Integer> retrieveInventoryForProduct(String productId);

}
```

It's very simple and explanations are in the comment. Then we step into the implementation, `InventoryServiceImpl`. All invetory amounts are stored in Redis with the namespace `inventory:v1`, for instance, product `A123456` will be stored in `inventory:v1:A123456`.

Vert.x Redis provides `incrby` and `decrby` operations so it's easy to implement `increase` and `decrease` method. Let's only have a look at `increase` method as their implementations are similar:

```java
@Override
public Future<Integer> increase(String productId, int increase) {
  Future<Long> future = Future.future();
  client.incrby(PREFIX + productId, increase, future.completer());
  return future.map(Long::intValue);
}
```

As our inventory amount is not very big, `Integer` is enough so we need to convert the `Long` result into `Integer` using `Long::intValue` method reference.

The `retrieveInventoryForProduct` method is also very short:

```java
@Override
public Future<Integer> retrieveInventoryForProduct(String productId) {
  Future<String> future = Future.future();
  client.get(PREFIX + productId, future.completer());
  return future.map(r -> r == null ? "0" : r)
    .map(Integer::valueOf);
}
```

We use `get` command to get the value. As the result type is string, we need to convert it to number using `Integer::valueOf` method reference. If the result is null, that indicates that there is no inventory for the product so just return zero amount.

Okay, the inventory service is finished, so the next step is `InventoryRestAPIVerticle`. We have three REST APIs here:

- GET `/:productId` - Get product inventory for certain product
- PUT `/:productId/increase` - Increase inventory amount for certain product
- PUT `/:productId/decrease` - Decrease inventory amount for certain product

The `start` method is similar that previous REST verticles we've seen: create service, create router, set body handler and api handler, enable herat-beat check, create HTTP server and publish HTTP endpoint.

```java
@Override
public void start(Future<Void> future) throws Exception {
  super.start();

  this.inventoryService = InventoryService.createService(vertx, config());

  final Router router = Router.router(vertx);
  // body handler
  router.route().handler(BodyHandler.create());
  // API handler
  router.get(API_RETRIEVE).handler(this::apiRetrieve);
  router.put(API_INCREASE).handler(this::apiIncrease);
  router.put(API_DECREASE).handler(this::apiDecrease);

  // enable heart beat check
  enableHeartbeatCheck(router, config());

  // get HTTP host and port from configuration, or use default value
  String host = config().getString("inventory.http.address", "0.0.0.0");
  int port = config().getInteger("inventory.http.port", 8086);

  createHttpServer(router, host, port)
    .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
    .setHandler(future.completer());
}
```

Let's explain only one of the routing handler methods `apiIncrease` and others are similar:

```java
private void apiIncrease(RoutingContext context) {
  try {
    String productId = context.request().getParam("productId"); // (1)
    int increase = Integer.valueOf(context.request().getParam("n")); // (2)
    if (increase <= 0) {
      badRequest(context, new IllegalStateException("Negative increase"));
    } else {
      inventoryService.increase(productId, increase) // (3)
        .setHandler(rawResultHandler(context));
    }
  } catch (Exception ex) {
    context.fail(400);
  }
}
```

First we get the path parameter `productId` (1) and the URI parameter `n` (2) with `context.request().getParam(param)` method. As the URI parameter might be null, or not integer, we need to wrap the procedure in try-catch block. If the increase number is lower than zero, we return `400 Bad Request` status to the response. If the increase number is valid, we invoke the `increase` method of `inventoryService` and then set a handler with `rawResultHandler(context)` (3).

The `rawResultHandler` method generates a `Handler<AsyncResult<T>>` that send raw string response:

```java
protected <T> Handler<AsyncResult<T>> rawResultHandler(RoutingContext context) {
  return ar -> {
    if (ar.succeeded()) { // result successful
      T res = ar.result();
      context.response()
        .end(res == null ? "" : res.toString()); // invoke toString as result
    } else {
      internalError(context, ar.cause()); // result fail, return 500 status
      ar.cause().printStackTrace();
    }
  };
}
```

Finally, we need to identify `api.name` in `config.json`:

```JSON
{
  "api.name": "inventory",
  "redis.host": "redis",
  "inventory.http.address": "inventory-microservice",
  "inventory.http.port": 8086
}
```

# Event sourcing pattern - Shopping cart microservice

> Note: this part will have a big change in next version.

Well, as we've completed the journey with base backend components, let's step into another important backend component - `shopping-cart-microservice`. This component is responsible for shopping cart retrieve and checkout. Differently, the shopping cart service uses **Event Sourcing** rather than the traditional storage.

## Introduction to event sourcing

In the traditional data storage mechanisms, we often save data directly into the persistence. Thus bringing an issue - we can only see the data result. But there are times when we want to know how the data is modified - that is what [event sourcing](http://martinfowler.com/eaaDev/EventSourcing.html) could solve.

Event sourcing ensures that all changes to application state are stored as a sequence of events. Not only can we query the events, but also rebuild past state with the previous events! That's wonderful! And one thing is important: we can't modify the saved events or their orders - that is, the persistence of the sequence of events is **append-only** and events should be **immutable**.

There are numerous benefits of using event sourcing in a microservice architecture (or distributed systems):

- We can construct the consistent state of an entity at any time
- It enables [compensating transactions](https://en.wikipedia.org/wiki/Compensating_transaction)
- We can process the event streams in asynchronous and reactive way. That's perfectly suitable for Vert.x
- The event persistence can also considered as entity logs

The choice of event persistence should also be a concern. **Apache Kafka** seems to be a good choice. In this tutorial we use MySQL to simplify the implementation and we may add content about Apache Kafka in next version.

## Shopping cart events

Let's see the shopping `CartEvent` data object:

```java
@DataObject(generateConverter = true)
public class CartEvent {

  private Long id;
  private CartEventType cartEventType;
  private String userId;
  private String productId;
  private Integer amount;

  private long createdAt;

  public CartEvent() {
    this.createdAt = System.currentTimeMillis();
  }

  public CartEvent(JsonObject json) {
    CartEventConverter.fromJson(json, this);
  }

  public CartEvent(CartEventType cartEventType, String userId, String productId, Integer amount) {
    this.cartEventType = cartEventType;
    this.userId = userId;
    this.productId = productId;
    this.amount = amount;
    this.createdAt = System.currentTimeMillis();
  }

  public static CartEvent createCheckoutEvent(String userId) {
    return new CartEvent(CartEventType.CHECKOUT, userId, "all", 0);
  }

  public static CartEvent createClearEvent(String userId) {
    return new CartEvent(CartEventType.CLEAR_CART, userId, "all", 0);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    CartEventConverter.toJson(this, json);
    return json;
  }

  public static boolean isTerminal(CartEventType eventType) {
    return eventType == CartEventType.CLEAR_CART || eventType == CartEventType.CHECKOUT;
  }
}
```

A `CartEvent` stores the time and type of the event, user id, corresponding product id and amount changed. `CartEvent` has four types. They are in `CartEventType` enum class:

```java
public enum CartEventType {
  ADD_ITEM, // add an item to the cart
  REMOVE_ITEM, // remove an item from the cart
  CHECKOUT, // shopping cart checkout
  CLEAR_CART // clear the cart
}
```

The event with `CHECKOUT` or `CLEAR_CART` processes the entire cart, so we write two static methods to create these two types of cart event.

There is another static method `isTerminal`, which is responsible for checking whether the current event is a **terminal** event. It's useful when aggregate shopping cart from the events.

## Shopping cart entity

A `ShoppingCart` data object represents a shopping cart of certain user. It contains a list of product items:

```java
private List<ProductTuple> productItems = new ArrayList<>();
```

A `ProductTuple` contains the following data: the `productId`, `sellerId` and unit `price` of a product, and current amount in the corresponding shopping cart.

There is also an `amountMap` in `ShoppingCart` class:

```java
private Map<String, Integer> amountMap = new HashMap<>();
```

This is only as a helper field to save the amounts temporarily, so the setter and getter method of it is annotated with `@GenIgnore`.

We'll generate shopping cart from a sequence of cart events, so we need a `incorporate` method that incorporates the cart event into the shopping cart with amount changes:

```java
public ShoppingCart incorporate(CartEvent cartEvent) {
  // the cart event must be a add or remove command event
  boolean ifValid = Stream.of(CartEventType.ADD_ITEM, CartEventType.REMOVE_ITEM)
    .anyMatch(cartEventType ->
      cartEvent.getCartEventType().equals(cartEventType));

  if (ifValid) {
    amountMap.put(cartEvent.getProductId(),
      amountMap.getOrDefault(cartEvent.getProductId(), 0) +
        (cartEvent.getAmount() * (cartEvent.getCartEventType()
          .equals(CartEventType.ADD_ITEM) ? 1 : -1)));
  }

  return this;
}
```

First we judge the cart event type. If it is an `ADD_ITEM` or `REMOVE_ITEM` command, we'll calculate current amount of product in the shopping cart and then modify. The amount is saved in the `amountMap`.

## Using Rx version of Vert.x JDBC

As we've created relevant data objects, it's time to see shopping cart event persistence service.

Vert.x supports RxJava, and most of the components provide a Rx version. So here we implement the persistence service with Rx version of Vert.x JDBC. That is - the service is `Single`/`Observable` based, so more reactive and functional!

In our implementation, We designed a `SimpleCrudDataSource` interface as the base interface:

```java
public interface SimpleCrudDataSource<T, ID> {

  Single<Void> save(T entity);

  Single<Optional<T>> retrieveOne(ID id);

  Single<Void> delete(ID id);

}
```

And `CartEventDataSource` interface as the specific service interface:

```java
public interface CartEventDataSource extends SimpleCrudDataSource<CartEvent, Long> {

  Observable<CartEvent> streamByUser(String userId);

}
```

There is only one method - the `streamByUser` method will return event streams of a certain user.

Now let's see the implementation of the `CartEventDataSource` interface. First is the `save` method, which saves a cert event to the persistence:

```java
@Override
public Single<Void> save(CartEvent cartEvent) {
  JsonArray params = new JsonArray().add(cartEvent.getCartEventType().name())
    .add(cartEvent.getUserId())
    .add(cartEvent.getProductId())
    .add(cartEvent.getAmount())
    .add(cartEvent.getCreatedAt() > 0 ? cartEvent.getCreatedAt() : System.currentTimeMillis());
  return client.rxGetConnection()
    .flatMap(conn -> conn.rxUpdateWithParams(SAVE_STATEMENT, params)
      .map(r -> (Void) null)
      .doAfterTerminate(conn::close)
    );
}
```

> Note: Don't forget to close the database connection.

Do you feel it's more concise and reactive by contrast with callback-based common Vert.x JDBC? Of course! Using RxJava can bring us a more reactive way. We can easily get a connection with `rxGetConnection` method, then use the connection to execute save sql statement with the given parameters. Only two lines! By contrast, you have to write this in the common Vert.x JDBC:

```java
client.getConnection(ar -> {
  if (ar.succeeded) {
    SQLConnection connection = ar.result();
    connection.updateWithParams(SAVE_STATEMENT, params, ar2 -> {
      // ...
      connection.close();
    })
  } else {
    resultHandler.handle(Future.failedFuture(ar.cause()));
  }
})
```

So it's nice to use Rx version of Vert.x components. And `vertx-sync` may also be a good choice.

And don't forget that the returned `Observable` is cold so only emits data when it is subscribed.

Then let's see the `retrieveOne` method, which retrieves a cart event with certain `id` from the database:

```java
@Override
public Single<Optional<CartEvent>> retrieveOne(Long id) {
  return client.rxGetConnection()
    .flatMap(conn ->
      conn.rxQueryWithParams(RETRIEVE_STATEMENT, new JsonArray().add(id))
        .map(ResultSet::getRows)
        .map(list -> {
          if (list.isEmpty()) {
            return Optional.<CartEvent>empty();
          } else {
            return Optional.of(list.get(0))
              .map(this::wrapCartEvent);
          }
        })
        .doAfterTerminate(conn::close)
    );
}
```

Very clear! It resembles `Future` based style we've mentioned so I won't explain this.

As the cart event database is designed as **append-only**, we won't implement `update` and `delete` method.

Next let's see another important implementation - `streamByUser`:

```java
@Override
public Observable<CartEvent> streamByUser(String userId) {
  JsonArray params = new JsonArray().add(userId).add(userId);
  return client.rxGetConnection()
    .flatMapObservable(conn ->
      conn.rxQueryWithParams(STREAM_STATEMENT, params)
        .map(ResultSet::getRows)
        .flatMapObservable(Observable::from)
        .map(this::wrapCartEvent)
        .doOnTerminate(conn::close)
    );
}
```

One key part is the SQL statement `STREAM_STATEMENT`:

```sql
SELECT * FROM cart_event c
WHERE c.user_id = ? AND c.created_at > coalesce(
    (SELECT created_at FROM cart_event
	   WHERE user_id = ? AND (`type` = "CHECKOUT" OR `type` = "CLEAR_CART")
     ORDER BY cart_event.created_at DESC
     LIMIT 1),
    0)
ORDER BY c.created_at ASC;
```

It retrieves all cart events relevant to current cart. There are many cart events of many users, and each user can have mutiple cart events of different cart, so how to identify? The approach is - First get create time of the most recent **terminal** event, then retrieve all cart events of the user after the time.

So back to `streamByUser` method. From our explanation above, we know we'll get a list of events, but the method returns `Observable<CartEvent>`. Why? That is because we use a operator `flatMapIterable`, to transform the single result into streams. So it is different from the `Future` in Vert.x or `Single` in RxJava. The `Future` is more like a `Single` in Rx, which always either emits one value or an error notification. Here the `Observable` result is really a sequence of event stream. We'll consume the event stream in the `ShoppingCartService`.

Wow, you must have been attracted by the reactive style in Rx! In the next section, we'll explore the implementation of `ShoppingCartService`, which is also reactive with future-based pattern.

## Aggregate shopping cart from events

First we look at the `ShoppingCartService` interface. It's also an event bus service:

```java
@VertxGen
@ProxyGen
public interface ShoppingCartService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "shopping-cart-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.shopping.cart";

  @Fluent
  ShoppingCartService addCartEvent(CartEvent event, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  ShoppingCartService getShoppingCart(String userId, Handler<AsyncResult<ShoppingCart>> resultHandler);

}
```

We defined two methods here: `addCartEvent` for adding cart event to the database, and `getShoppingCart` for getting current shopping cart of a certain user.

Now let's step into the implementation class `ShoppingCartServiceImpl`. First see `addCartEvent` method. It's very simple:

```java
@Override
public ShoppingCartService addCartEvent(CartEvent event, Handler<AsyncResult<Void>> resultHandler) {
  Future<Void> future = Future.future();
  repository.save(event).toSingle().subscribe(future::complete, future::fail);
  future.setHandler(resultHandler);
  return this;
}
```

As we've mentioned above, the `Observable` returned by `repository.save()` method is more like a `Single` so we can simply convert into `Future` using `subscribe(future::complete, future::fail)` so that we can set a `Handler<AsyncResult<Void>>` to it.

The logic of `getShoppingCart` method is in `aggregateCartEvents` method. It's of vital importance and it's future-based. Let's see its code:

```java
private Future<ShoppingCart> aggregateCartEvents(String userId) {
  Future<ShoppingCart> future = Future.future();
  // aggregate cart events into raw shopping cart
  repository.streamByUser(userId) // (1)
    .takeWhile(cartEvent -> !CartEvent.isTerminal(cartEvent.getCartEventType())) // (2)
    .reduce(new ShoppingCart(), ShoppingCart::incorporate) // (3)
    .subscribe(future::complete, future::fail); // (4)

  return future.compose(cart ->
    getProductService() // (5)
      .compose(service -> prepareProduct(service, cart)) // (6) prepare product data
      .compose(productList -> generateCurrentCartFromStream(cart, productList)) // (7) prepare product items
  );
}
```

Let's elaborate this step by step. First we create a `Future`. We first invoke `repository.streamByUser(userId)` to get an `Observable<CartEvent>`, representing a series of event stream (1).

Then we use `takeWhile` operator to get cart events of `ADD_ITEM` and `REMOVE_ITEM` type (2). The `takeWhile` operator discards items emitted by an Observable after a specified condition becomes false, so as soon as the stream meets a *terminal event*, it will end up and emit to downstream.

Next we fold the event stream into a shopping cart using `reduce` operator (3). The `reduce` operator apply a function to each item emitted by an Observable, sequentially, and emit the final value. The procedure can be sumed up as the following: We create a new empty shopping cart, and then call `incorporate` method with each event in event stream on the shopping cart. The final cart contains complete amount map of the products in the shopping cart.

Currently, as the `Observable` containing initial shopping cart resembles a `Single` producer, we complete the cart future with `subscribe(future::complete, future::fail)` (4).

The shopping cart we got initially is incomplete and need more data. So we first compose the cart future with `getProductService` async method (5). We retrieve the `ProductService` from the discovery infrastructure, then prepare products data for shopping cart (6) and finally aggregate the complete shopping cart with `generateCurrentCartFromStream` async method. There are several composition of the asynchronous methods here and we'll take a look for each.

First let's see the `getProductService` method. It retrieves `ProductService` from the discovery infrastructure and returns a `Future<ProductService>`:

```java
private Future<ProductService> getProductService() {
  Future<ProductService> future = Future.future();
  EventBusService.getProxy(discovery,
    new JsonObject().put("name", ProductService.SERVICE_NAME),
    future.completer());
  return future;
}
```

Now we get the service, so next step is to retrieve necessary product data from the `ProductService`. We call this method `prepareProduct`:

```java
private Future<List<Product>> prepareProduct(ProductService service, ShoppingCart cart) {
  List<Future<Product>> futures = cart.getAmountMap().keySet() // (1)
    .stream()
    .map(productId -> {
      Future<Product> future = Future.future();
      service.retrieveProduct(productId, future.completer());
      return future; // (2)
    })
    .collect(Collectors.toList()); // (3)
  return Functional.sequenceFuture(futures); // (4)
}
```

In this implementation, first we get the list of product number in the current cart (1) and then for each product number, we retrieve corresponding `Product` entity via the `retrieveProduct` asynchronous method. The procedure is asynchronous so we map the result as `Future<Product>`. Then we collect the future stream as `List<Future<Product>>` (3). Now that we have a list of futures, how can we transform the `List<Future<Product>>` into `Future<List<Product>>`? Here I implemented a helper method `sequenceFuture` (4) to do this. It's in the `Functional` class in `io.vertx.blueprint.microservice.common.functional` package:

```java
public static <R> Future<List<R>> sequenceFuture(List<Future<R>> futures) {
  return CompositeFutureImpl.all(futures.toArray(new Future[futures.size()])) // (1)
    .map(v -> futures.stream()
        .map(Future::result) // (2)
        .collect(Collectors.toList()) // (3)
    );
}
```

This method is useful for reducing a sequence of futures into a single `Future` with a list of the results. Here we first invoke `CompositeFutureImpl#all` method (1). It returns a composite future, succeeds only if every result is successful and fails when any result is failed. Then we transform each `Future` to the corresponding result (2) as the `all` method have forced each `Future` to get results. Finally we collect the result list (3).

Get back! Now we've got products from the `ProductService`, it's time to aggregate the complete shopping cart! Let's see `generateCurrentCartFromStream` method:

```java
private Future<ShoppingCart> generateCurrentCartFromStream(ShoppingCart rawCart, List<Product> productList) {
  Future<ShoppingCart> future = Future.future();
  // check if any of the product is invalid
  if (productList.stream().anyMatch(e -> e == null)) { // (1)
    future.fail("Error when retrieve products: empty");
    return future;
  }
  // construct the product items
  List<ProductTuple> currentItems = rawCart.getAmountMap().entrySet() // (2)
    .stream()
    .map(item -> new ProductTuple(getProductFromStream(productList, item.getKey()), // (3)
      item.getValue())) // (4) amount value
    .filter(item -> item.getAmount() > 0) // (5) amount must be greater than zero
    .collect(Collectors.toList());

  ShoppingCart cart = rawCart.setProductItems(currentItems); // (6)
  return Future.succeededFuture(cart); // (7)
}
```

Notice that the method doesn't require asynchronous, but we need to demonstrate a failure or success state, so here we still return a `Future`. First we create a `Future`, then we check if any of the products is invalid using `anyMatch` method (1). If invalid, return a failed future. Then we construct the product items `ProductTuple` from the retrieved data. Here we use this constructor of `ProductTuple` class:

```java
public ProductTuple(Product product, Integer amount) {
  this.productId = product.getProductId();
  this.sellerId = product.getSellerId();
  this.price = product.getPrice();
  this.amount = amount;
}
```

The first parameter is a `Product`, and we could get corresponding product from the given product list using `getProductFromStream` method:

```java
private Product getProductFromStream(List<Product> productList, String productId) {
  return productList.stream()
    .filter(product -> product.getProductId().equals(productId))
    .findFirst()
    .get();
}
```

After constructing the `ProductTuple`, we set it to the given `ShoppingCart`  (6) and then return the cart future (7). Now we finally made a complete shopping cart!

## Checkout - generate order from shopping cart

> Note: this part will have a big change in next version.

Now that we have chosen our favorite products and the shopping cart is prepared, it's time to checkout! The `CheckoutService` interface only contains one method: `checkout`:

```java
@VertxGen
@ProxyGen
public interface CheckoutService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "shopping-checkout-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.shopping.cart.checkout";

  /**
   * Order event source address.
   */
  String ORDER_EVENT_ADDRESS = "events.service.shopping.to.order";

  /**
   * Create a shopping checkout service instance
   */
  static CheckoutService createService(Vertx vertx, ServiceDiscovery discovery) {
    return new CheckoutServiceImpl(vertx, discovery);
  }

  void checkout(String userId, Handler<AsyncResult<CheckoutResult>> handler);

}
```

Let's step into the implementation, `CheckoutServiceImpl`. Even though it only contains a checkout logic, it's a bit complex and includes various kind of component interaction... First see `checkout` method:

```java
@Override
public void checkout(String userId, Handler<AsyncResult<CheckoutResult>> resultHandler) {
  if (userId == null) { // (1)
    resultHandler.handle(Future.failedFuture(new IllegalStateException("Invalid user")));
    return;
  }
  Future<ShoppingCart> cartFuture = getCurrentCart(userId); // (2)
  Future<CheckoutResult> orderFuture = cartFuture.compose(cart ->
    checkAvailableInventory(cart).compose(checkResult -> { // (3)
      if (checkResult.getBoolean("res")) { // (3)
        double totalPrice = calculateTotalPrice(cart); // (4)
        // create order instance
        Order order = new Order().setBuyerId(userId) // (5)
          .setPayId("TEST") // reserved field
          .setProducts(cart.getProductItems())
          .setTotalPrice(totalPrice);
        // set id and then send order, wait for reply
        return retrieveCounter("order") // (6)
          .compose(id -> sendOrderAwaitResult(order.setOrderId(id))) // (7)
          .compose(result -> saveCheckoutEvent(userId).map(v -> result)); // (8)
      } else {
        // has insufficient inventory, fail
        return Future.succeededFuture(new CheckoutResult()
          .setMessage(checkResult.getString("message"))); // (9)
      }
    })
  );

  orderFuture.setHandler(resultHandler); // (10)
}
```

You've seen a lot of `compose`... Yes, here we composed many future-based asynchronous methods reactively! First we check whether the given `userId` is valid (1), if not then return a failed future. Then we get current shopping cart of the user using `getCurrentCart` method (2). It's asynchronous so returns a `Future<ShoppingCart>`:

```java
private Future<ShoppingCart> getCurrentCart(String userId) {
  Future<ShoppingCartService> future = Future.future();
  EventBusService.getProxy(discovery,
    new JsonObject().put("name", ShoppingCartService.SERVICE_NAME),
    future.completer());
  return future.compose(service -> {
    Future<ShoppingCart> cartFuture = Future.future();
    service.getShoppingCart(userId, cartFuture.completer());
    return cartFuture.compose(c -> {
      if (c == null || c.isEmpty())
        return Future.failedFuture(new IllegalStateException("Invalid shopping cart"));
      else
        return Future.succeededFuture(c);
    });
  });
}
```

In `getCurrentCart` method we get the `ShoppingCartService` from the discovery infrastructure using `EventBusService.getProxy` method. Then we call `ShoppingCartService#getShoppingCart` to get current cart for the user. We need also to validate if the cart is empty, then return the future result.

You may also notice that the `checkout` method will return a `CheckoutResult`, which refers to the async result of checkout:

```java
@DataObject(generateConverter = true)
public class CheckoutResult {
  private String message; // result message
  private Order order; // order entity
}
```

So we'll generate a `Future<CheckoutResult>` from the retrieved `cartFuture`. We first compose it with `checkAvailableInventory` method (3). The `checkAvailableInventory` is responsible for checking whether the inventory is sufficient and we'll explain it later. Then comes another compose. We check the inventory result and judge if all inventories are sufficient (3). If not, we return a `CheckoutResult` with the insufficient product message (9). Or we calculate the total price of the products (4) and generate new `Order` (5). The order contains:

- buyer id
- amount, seller, unit price for each selected products
- total price

Next, we retrieve new order id from the global counter (6) and then send the order to order components and await for `CheckoutResult` (7). After done, we save the checkout cart event to the event store (8).

Finally we set `resultHandler` on the `orderFuture` (10). When it is assigned, the handler will be called.

Now let's see the `checkAvailableInventory` async method we've mentioned above:

```java
private Future<JsonObject> checkAvailableInventory(ShoppingCart cart) {
  Future<List<JsonObject>> allInventories = getInventoryEndpoint().compose(client -> { // (1)
    List<Future<JsonObject>> futures = cart.getProductItems() // (2)
      .stream()
      .map(product -> getInventory(product, client)) // (3)
      .collect(Collectors.toList());
    return Functional.sequenceFuture(futures); // (4)
  });
  return allInventories.map(inventories -> {
    JsonObject result = new JsonObject();
    // get the list of products whose inventory is lower than the demand amount
    List<JsonObject> insufficient = inventories.stream()
      .filter(item -> item.getInteger("inventory") - item.getInteger("amount") < 0) // (5)
      .collect(Collectors.toList());
    // insufficient inventory exists
    if (insufficient.size() > 0) {
      String insufficientList = insufficient.stream()
        .map(item -> item.getString("id"))
        .collect(Collectors.joining(", ")); // (6)
      result.put("message", String.format("Insufficient inventory available for product %s.", insufficientList))
        .put("res", false); // (7)
    } else {
      result.put("res", true); // (8)
    }
    return result;
  });
}
```

Wow! A bit complex! First we get the inventory REST endpoint with `getInventoryEndpoint` method (1). It simply get the REST endpoint from the discovery infrastructure using `HttpEndpoint.getClient` method:

```java
private Future<HttpClient> getInventoryEndpoint() {
  Future<HttpClient> future = Future.future();
  HttpEndpoint.getClient(discovery,
    new JsonObject().put("name", "inventory-rest-api"),
    future.completer());
  return future;
}
```

Then we'll compose another `Future`. We get the product item list from the shopping cart (2) and map each items to corresponding product number and inventory amount (3). In the previous we have got the `HttpClient` for the inventory REST endpoint, so we retrieve each inventory via the client. The logic is in `getInventory` method:

```java
private Future<JsonObject> getInventory(ProductTuple product, HttpClient client) {
  Future<Integer> future = Future.future(); // (A)
  client.get("/" + product.getProductId(), response -> { // (B)
    if (response.statusCode() == 200) { // (C)
      response.bodyHandler(buffer -> {
        try {
          int inventory = Integer.valueOf(buffer.toString()); // (D)
          future.complete(inventory);
        } catch (NumberFormatException ex) {
          future.fail(ex);
        }
      });
    } else {
      future.fail("not_found:" + product.getProductId()); // (E)
    }
  })
    .exceptionHandler(future::fail)
    .end();
  return future.map(inv -> new JsonObject()
    .put("id", product.getProductId())
    .put("inventory", inv)
    .put("amount", product.getAmount())); // (F)
}
```

The procedure is clear. First we create a `Future<Integer>` to store the inventory amount result (A). Then we use `client.get(path, responseHandler)` method to consume inventory endpoint (B). In the response handler, if the status code is **200 OK** (C), we can then get the inventory from the response body via `bodyHandler` and convert it to `Integer` (D). As soon as every process is successful, the future will be assigned with the inventory amount. If the status code is not **200** (e.g. **400** or **404**), we think that there is something wrong so fail the future (E).

Only containing inventory number is not enough. For convenient, we design the result as a `JsonObject` containing product id, inventory amount and product amount in shopping cart, so we finally map the `future` to the `JsonObject` (F).

Now back to the `checkAvailableInventory` method. After line (3) we get a list of futures again, so we map it into a future of list with `Functional.sequenceFuture` method (4). Now we get a `Future<List<JsonObject>>`, it's time to check each inventory amount! We created a list `insufficient` specificly saving insufficient products (5). If the insufficient list is not empty, that means insufficient inventory exists so we need to get each id of sufficient product. Here we implemented this by using `collect(Collectors.joining(", "))` (6). This trick is very useful. For example, product id list `[TST-0001, TST-0002, BK-16623]` will be reduced to single string "TST-0001, TST-0002, BK-16623".

After getting insufficient product message, we put the message into the `JsonObject` result. Meanwhile, whether all inventories are sufficient or not is identified by a boolean field `res` in the `JsonObject` so we set it to false (7).

If we got an empty insufficient list before, it means that all inventories are available so set the `res` field to true (8). Finally we return the resut future.

Get back. The `calculateTotalPrice` method calculate total price from the shopping cart, which can be regarded as a map-reduce procedure:

```java
return cart.getProductItems().stream()
  .map(p -> p.getAmount() * p.getPrice()) // join by product id
  .reduce(0.0d, (a, b) -> a + b);
```

As we've mentioned above in the `checkout` method, after generating new raw order, we have three asynchronous method composition: `retrieveCounter -> sendOrderAwaitResult -> saveCheckoutEvent`. Let's have a look.

we first retrieve the counter of order id from `CounterService` in the `cache-infrastructure`:

```java
private Future<Long> retrieveCounter(String key) {
  Future<Long> future = Future.future();
  EventBusService.<CounterService>getProxy(discovery,
    new JsonObject().put("name", "counter-eb-service"),
    ar -> {
      if (ar.succeeded()) {
        CounterService service = ar.result();
        service.addThenRetrieve(key, future.completer());
      } else {
        future.fail(ar.cause());
      }
    });
  return future;
}
```

Of course you can also use the original auto-increment counter in RDBMS but if you have several database nodes, you will have to maintain the consistency of the counter manually.

Then we save the cart checkout event. The `saveCheckoutEvent` method is quite similar to `getCurrentCart` method as they all get `ShoppingCartService` from the discovery infrastructure and then call an asynchronous method of the service:

```java
private Future<Void> saveCheckoutEvent(String userId) {
  Future<ShoppingCartService> future = Future.future();
  EventBusService.getProxy(discovery,
    new JsonObject().put("name", ShoppingCartService.SERVICE_NAME),
    future.completer());
  return future.compose(service -> {
    Future<Void> resFuture = Future.future();
    CartEvent event = CartEvent.createCheckoutEvent(userId);
    service.addCartEvent(event, resFuture.completer());
    return resFuture;
  });
}
```

## Send orders to order microservice

As the new order id has been retrieved, we can set the id to the order entity and then, as a key part, send the order to *order microservice*. Let's see the `sendOrderAwaitResult` asynchronous method:

```java
private Future<CheckoutResult> sendOrderAwaitResult(Order order) {
  Future<CheckoutResult> future = Future.future();
  vertx.eventBus().send(CheckoutService.ORDER_EVENT_ADDRESS, order.toJson(), reply -> {
    if (reply.succeeded()) {
      future.complete(new CheckoutResult((JsonObject) reply.result().body()));
    } else {
      future.fail(reply.cause());
    }
  });
  return future;
}
```

We `send` the order to a specific address on the event bus, then the order microservice could consume orders on the another side. Notice that the `send` method also accepts a `Handler<AsyncResult<Message<T>>>`, which means that we should wait for the reply message from the consumer. This is actually called **request-response** pattern. If we successfully received the reply, we'll complete the future with the received `CheckoutResult`. If we received an error or timeout triggered, we'll fail the future.

So, having experienced a long composition chain, we've finally finished our exploration with `checkout`! Feel a bit more reactive? That's cool!

As we've mentioned above, the order microservice will consume our orders from the event bus. As they don't know where we sent the orders, we need to publish a **message source** into the service discovery infrastructure. Thus, the order component can get a corresponding `MessageConsumer` from the discovery layer so that they can consume the orders. We'll publish services in the `CartVerticle`. Before we have an encounter with `CartVerticle`, let's have a glimpse of `RestShoppingAPIVerticle`.

## Shopping cart REST API

As we've mentioned above, the REST API verticles are responsible for creating server and publish HTTP endpoint to the discovery infrastructure. That's the same as `RestShoppingAPIVerticle`. We have three shopping cart APIs here:

- GET `/cart` - get current shopping cart of current user in session scope
- POST `/events` - add a new cart event for current user in session scope
- POST `/checkout` - issue a checkout request for current user's shopping cart

All of three APIs require login so their routes are wrapped with `requireLogin`:

```java
// api route handler
router.post(API_CHECKOUT).handler(context -> requireLogin(context, this::apiCheckout));
router.post(API_ADD_CART_EVENT).handler(context -> requireLogin(context, this::apiAddCartEvent));
router.get(API_GET_CART).handler(context -> requireLogin(context, this::apiGetCart));
```

As for implementations, they are very simple and here we only describe one `apiAddCartEvent` method:

```java
private void apiAddCartEvent(RoutingContext context, JsonObject principal) {
  String userId = Optional.ofNullable(principal.getString("userId"))
    .orElse(TEST_USER); // (1)
  CartEvent cartEvent = new CartEvent(context.getBodyAsJson()); // (2)
  if (validateEvent(cartEvent, userId)) {
    shoppingCartService.addCartEvent(cartEvent, resultVoidHandler(context, 201)); // (3)
  } else {
    context.fail(400); // (4)
  }
}
```

First we get the user id from the principal and use `TEST_USER` as test id if `userId` field does not exists (1). Then we create `CartEvent` from the request body (2). We need to validate if the user id in request cart event equals to current user id. If equals, invoke `addCartEvent` to add cart event to the event store and returing **201** status when successful (3). If the request cart event is invalid, end the response with **400** status (4).

## Cart verticle

The shopping cart verticle is responsible for publishing services. Here we publish three services:

- `shopping-checkout-eb-service`: Checkout service. This is an **event bus service**.
- `shopping-cart-eb-service`: Shopping cart service. This is an **event bus service**.
- `shopping-order-message-source`: Order message source where we send orders. This is a **message source service**.

Simultaneously, the `CartVerticle` will deploy the `RestShoppingAPIVerticle`. The **api name** of the REST endpoint is `cart` by default.

![Cart Page](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/spa-cart.png)

# Order microservice

Well, now we have submitted checkout request and an order is sent to the order microservice. So the next thing we need to to is dispatching and processing the order. In current version of the blueprint, we simply save orders to database and modify the inventory amount. In real production, we might need to publish the orders into MQ then consume them in the upstream components.

As the implementation of database operation service is similar to others, we won't explain the detail of `OrderService`. You can look up the implementation on [GitHub](https://github.com/sczyh30/vertx-blueprint-microservice/blob/master/order-microservice/src/main/java/io/vertx/blueprint/microservice/order/impl/OrderServiceImpl.java).

Our simple order processing logic is in `RawOrderDispatcher` verticle so let's have a look on it.

## Consume the Cart-To-Order message source

It's easy to comsume an message source using `getConsumer` static method in `MessageSource` interface. In `RawOrderDispatcher`, we'll consume the `shopping-order-message-source` message source published in shopping cart microservice:

```java
@Override
public void start(Future<Void> future) throws Exception {
  super.start();
  MessageSource.<JsonObject>getConsumer(discovery,
    new JsonObject().put("name", "shopping-order-message-source"),
    ar -> {
      if (ar.succeeded()) {
        MessageConsumer<JsonObject> orderConsumer = ar.result();
        orderConsumer.handler(message -> {
          Order wrappedOrder = wrapRawOrder(message.body());
          dispatchOrder(wrappedOrder, message);
        });
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });
}
```

The result is a `MessageConsumer<T>` and you can consume the messages by calling `handler` method to attach a `Handler<Message<T>>` on it. Here our message body is an order in `JsonObject` format so we can create the order with the message body then dispatch and process the order by `dispatchOrder` method.

## "Process" the order

Let's see our simple "dispatch and process" method `dispatchOrder`:

```java
private void dispatchOrder(Order order, Message<JsonObject> sender) {
  Future<Void> orderCreateFuture = Future.future();
  orderService.createOrder(order, orderCreateFuture.completer()); // (1)
  orderCreateFuture
    .compose(orderCreated -> applyInventoryChanges(order)) // (2)
    .setHandler(ar -> {
      if (ar.succeeded()) {
        CheckoutResult result = new CheckoutResult("checkout_success", order); // (3)
        sender.reply(result.toJson()); // (4)
        publishLogEvent("checkout", result.toJson(), true); // (5)
      } else {
        sender.fail(5000, ar.cause().getMessage()); // (6)
        ar.cause().printStackTrace();
      }
    });
}
```

First we create a future indicating the result of adding an order into database. Then we call `createOrder` method of `orderService` to save the order into the database (1). As we set `orderCreateFuture.completer()` as the result handler, the `orderCreateFuture` will be assigned as soon as the save procedure is finished (or failed). Then we compose the future with `applyInventoryChanges` method to modify inventory amount changes (2). If the two procedures are successful, we then create a success `CheckoutResult` (3) and reply to the sender with the result by calling `reply` method (4). After that we publish the event to notify the log component (5). If the procedures failed, we should notify the sender of the failure with `fail` method (6).

Very easy yeah? Let's then take a look at `applyInventoryChanges` method:

```java
private Future<Void> applyInventoryChanges(Order order) {
  Future<Void> future = Future.future();
  // get REST endpoint
  Future<HttpClient> clientFuture = Future.future();
  HttpEndpoint.getClient(discovery,
    new JsonObject().put("name", "inventory-rest-api"),
    clientFuture.completer());
  // modify the inventory changes via REST API
  return clientFuture.compose(client -> {
    List<Future> futures = order.getProducts()
      .stream()
      .map(item -> {
        Future<Void> resultFuture = Future.future();
        String url = String.format("/%s/decrease?n=%d", item.getProductId(), item.getAmount());
        client.put(url, response -> {
          if (response.statusCode() == 200) {
            resultFuture.complete(); // need to check result?
          } else {
            resultFuture.fail(response.statusMessage());
          }
        })
          .exceptionHandler(resultFuture::fail)
          .end();
        return resultFuture;
      })
      .collect(Collectors.toList());
    // composite async results, all must be complete
    CompositeFuture.all(futures).setHandler(ar -> {
      if (ar.succeeded()) {
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });
    return future;
  });
}
```

You shouldn't feel unfamiliar with this implementation as it's similar to the previous `getInventory` method in shopping cart microservice. We first get the inventory client, then for each products in the order, apply corresponding inventory changes with the `amount`. Then comes a `List<Future>`, but here we don't need the actual results of each procedures so we just call `CompositeFuture.all` method to check if all futures are successful.

As for `OrderVerticle`, it just does three small things: publish order event bus, deploy the dispatcher verticle and deploy the REST verticle.

# Micro Shop SPA integration

In this blueprint, we provide a simple micro shop SPA frontend written in Angular.js. So a question is: How to integrate the SPA frontend with the microservice?

In current version, we integrate the SPA into the `api-gateway` for convenience. All we need to do is configure the route so that it can handle static resources. Just one line:

```java
router.route("/*").handler(StaticHandler.create());
```

The default mapped directory for the static resources is `webroot` directory. For example, `webroot/index.html` corresponds to the url `http://host:port/index.html`. You can also configure the directory when  you create the `StaticHandler`.

![](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/shopping-spa-product-detail.png)

# Monitor dashboard with metrics

Similarly, the monitor dashboard is also an SPA frontend. In this section we'll learn:

- How to configure SockJS - EventBus bridge
- How to consume messages from the event bus in the browser
- How to use **Vert.x Dropwizard Metrics** to get some metrics data

## SockJS - Event bus bridge

There are times when we want to consume messages from event bus in the browser. This seems to be magic, and you can imagine, Vert.x can do this! Vert.x provides a [SockJS - Event bus bridge](http://vertx.io/docs/vertx-sockjs-service-proxy/java/) to enable interaction on event bus between the server and client (usually the browser).

To enable the bridge we need to configure the `SockJSHandler` and router:

```java
// event bus bridge
SockJSHandler sockJSHandler = SockJSHandler.create(vertx); // (1)
BridgeOptions options = new BridgeOptions()
  .addOutboundPermitted(new PermittedOptions().setAddress("microservice.monitor.metrics")) // (2)
  .addOutboundPermitted(new PermittedOptions().setAddress("events.log"));

sockJSHandler.bridge(options); // (3)
router.route("/eventbus/*").handler(sockJSHandler); // (4)
```

First we create the `SockJSHandler` (1). By default, it doesn't allow any interactions on event bus for security, so we need to configure the handler. We can create a `BridgeOptions` and set a set of permitted addresses on it. There are two kinds of addresses: **Outbound** and **Inbound**. Outbound addresses are for messages from the event bus to the browser, while inbound addresses are for messages from the browser to the event bus. And here we only need outbound addresses, `microservice.monitor.metrics` for metrics data and `events.log` for log (2). Then we set the options on the bridge (3) and finally create a route in the router (4). The path `/eventbus/*` is required by the SockJS client in the browser side for interactions.

## Send metrics data to event bus

Monitoring is an essential part of a microservice system. With **Vert.x Dropwizard Metrics** or **Vert.x Hawkular Metrics**, we can easily retrieve metrics data from the metrics library.

Here we use **Vert.x Dropwizard Metrics**. It's very easy to create a `MetricsService`:

```java
MetricsService service = MetricsService.create(vertx);
```

Then we could call `getMetricsSnapshot` method to get metrics for corresponding data. The method takes a parameter in `Measured` type, which can be a `Vertx` instance, an `EventBus` instance or any other classes implementing `Measured` interface. The retrieved metrics data is in `JsonObject` format. Here we get metrics data of `vertx` instance:

```java
// send metrics message to the event bus
vertx.setPeriodic(metricsInterval, t -> {
  JsonObject metrics = service.getMetricsSnapshot(vertx);
  vertx.eventBus().publish("microservice.monitor.metrics", metrics);
});
```

We set an interval timer to publish metrics data on the event bus in every interval.

For the detail of the metrics data, please reference [Documentation - Vert.x Dropwizard metrics](http://vertx.io/docs/vertx-dropwizard-metrics/java/#_the_metrics).

Now it's time to consume the metrics and log data in the browser side.

## Consume event bus messages in the browser

In order to consume messages in the browser, first we need the `vertx3-eventbus-client` and `sockjs` library. You can get them via npm or bower. Then we can create an `EventBus` object and register handlers:

```javascript
var eventbus = new EventBus('/eventbus');

eventbus.onopen = () => {
  eventbus.registerHandler('microservice.monitor.metrics', (err, message) => {
      $scope.metrics = message.body;
      $scope.$apply();
  });
}
```

You can get the message data via `message.body`.

Later we'll run the dashboard and inspect the status of the microservices.

# Show time!

Wow, we've explored all of the code of the online shopping microservice, so it's show time! We'll run the microservice application with Docker Compose as it's very convenient.

> Note: It's highly recommended to reserve at least 4GB memory for the microservice.

## Docker Machine setup (macOS/Windows)

If you are using Docker Machine on macOS or Windows, there are some configurations required for running the micorservice.

First add a new entry to your `hosts` file (in macOS, the location is `/etc/hosts`):

```
192.168.99.100 dockernet
```

Where the IP here is your `docker-machine ip`. You can also use any other hostname to specify the lookup name for docker-machine host.

Then you also need to set Docker external IP for API Gateway. Edit `api-gateway/src/config/docker.json` and set `api.gateway.http.address.external` property to `dockernet`.
If you choose to use a different lookup name than you'll have to update `docker/docker-compose.yml` line `- "dockernet:${EXTERNAL_IP}"` and replace `dockernet` with your hostname of choice.

## Configuration for ELK stack

To make ELK stack work correctly, we need to modify some kernel properties. First the `vm.max_map_count` should be at least **262144**:

```shell
sudo sysctl -w vm.max_map_count=262144
```

Configuration for Docker Machine:

```shell
docker-machine ssh
sudo sysctl net.ipv4.ip_forward
sudo sysctl -w vm.max_map_count=262144
```

And for Docker Machine users, memory might be an issue as well. If applications start failing because memory could not be allocated, then you'll need to increase your docker-machine memory using the following steps:

1. Stop docker-machine: `docker-machine stop`
2. Start VirtualBox
3. Select `default` VM
4. Choose Settings->System
5. Increase `Base Memory` (at least **4096** MB)

## Build the code and containers

Before we build the code, we have to install the frontend dependencies with **bower** for `api-gateway` and `monitor-dashboard` component. Enter to each `src/main/resources/webroot` directory and execute:

```
bower install
```

Then we can build the code:

```
mvn clean install -Dmaven.test.skip=true
```

After that, we build all Docker containers:

```
cd docker
sudo ./build.sh
```

As soon as the build finished, run the microservice:

```
sudo ./run.sh
```

The persistence and middleware containers (e.g. MySQL, MongoDB, Redis, Keycloak, ELK) will be started first because it might take some time to initialize. When the initialization has been done, the other services will be started in order.

When the entire microservice is running successful, we can visit the shop SPA in browser, by default the URL is [https://localhost:8787](https://localhost:8787).

## Some configuration for the first time

If we run the microservice for the first time, we must configure the **Keycloak** server manually. First we need to map the `keycloak-server` to the local host. Modify the `hosts` file (for Linux it's in `/etc` directory) and add:

```
0.0.0.0	keycloak-server
```

Then we should visit `http://keycloak-server:8080` and enter the admin console. By default the user and password is all **admin**. Now we enter into the admin dashboard. First we should create a new realm with any name. Then in this realm, we create a new client like this:

![Keycloak configuration](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/keycloak-client-config.png)

After created, we shep into the **Installation** tab and copy the JSON configuration. Replace the corresponding part of `api-gateway/src/config/docker.json` file with the copied configuration. For example:

```json
{
  "api.gateway.http.port": 8787,
  "api.gateway.http.address": "localhost",
  "circuit-breaker": {
    "name": "api-gateway-cb",
    "timeout": 10000,
    "max-failures": 5
  },
  // from here is the config of keycloak
  "realm": "Vert.x",
  "realm-public-key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkto9ZZm69cmdA9e7X4NUSo8T4CyvrYzlRiJdhr+LMqELdfN3ghEY0EBpaROiOueva//iUc/KViYGiAHVXEQ3nr3kytF6uZs9iwqkshKvltpxkOm2Qpj/FSRsCyHlB8Ahbt5xBmzH2mI1VDIxmVTdEBze4u6tLoi4ieo72b2q/dz09yrEokRm/sSYqzNgfE0i1JY6DI8C7FaKszKTK5DRGMIAib8wURrTyf8au0iiisKEXOHKEjo/g0uHCFGSOKqPOprNNIWYwedV+qaQa9oSah2IpwNgFNRLtHpvbcanftMLQOQIR0iufIJ+bHrNhH0RISZhTzcGX3pSIBw/HaERwQIDAQAB",
  "auth-server-url": "http://127.0.0.1:8180/auth",
  "ssl-required": "external",
  "resource": "vertx-blueprint",
  "credentials": {
    "secret": "ea99a8e6-f503-4bdb-afbd-9ae322ee7089"
  },
  "use-resource-role-mappings": true
}
```

You should also create a user or allow user register so that you can login as the user later.

For the details of configuring Keycloak, here is a wonderful tutorial: [Vertx 3 and Keycloak tutorial](http://vertx.io/blog/vertx-3-and-keycloak-tutorial/).

After modifying the config file, you have to rebuild the container of `api-gateway` and then restart with `docker-compose`.

## Enjoy our shopping!

As soon as you have finished the configuration, you can visit the URL of the frontend. By default it is `https://localhost:8787`:

![](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/shopping-spa-index.png)

Now we can login with Keycloak via `https://localhost:8787/login`. This will carry us to the login page. As soon as the authentication is successful, we'll be brought back to the home page. Now we can choose our favorite products and buy! Nice!

And you can also visit the monitor dashboard. By default the URL is [http://localhost:9100](http://localhost:9100).

![Monitor Dashboard](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/monitor-dashboard.png)

So wonderful!

# The end

So we finally arrive at the destination of the journey with this microservice blueprint! Cheers! We hope you can enjoy this blueprint and really learn something about Vert.x and microservice :-)

Some recommended readings about microservice and distributed systems:

- [Microservices - a definition of this new architectural term](http://martinfowler.com/articles/microservices.html)
- [Event Sourcing](http://martinfowler.com/eaaDev/EventSourcing.html)
- [Cloud Design Patterns: Prescriptive Architecture Guidance for Cloud Applications](https://msdn.microsoft.com/en-us/library/dn568099.aspx)

Enjoy building microservices!
