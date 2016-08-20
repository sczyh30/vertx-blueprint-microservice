# Preface

Hi, welcome back to the Vert.x Blueprint tutorial series! Nowadays, **microservice architecture** is becoming more and more popular and everyone is willing to have a try with microservice application development.
Very excited, Vert.x provides us a very useful microservice toolbox including **service discovery**, **circuit breaker** and so on.
With the help of Vert.x microservice components, we can establish our microservice application easily.
In this tutorial, we are going to explore a complete online shopping microservice application developed with Vert.x~

What you are going to learn:

- How to develop microservices with Vert.x
- Asynchronous development model
- Reactive and functional pattern
- Event sourcing pattern
- Asynchronous RPC on the clustered event bus
- Various type of services (e.g. HTTP endpoint, message source, data source)
- Service discovery with Vert.x
- How to use Vert.x Circuit Breaker
- How to implement a simple API gateway
- How to manage global authentication using OAuth 2 and Keycloak with Vert.x-Auth

And many more things too...

This is the third part of **Vert.x Blueprint Project**. The entire code is available on [GitHub](https://github.com/sczyh30/vertx-blueprint-microservice/tree/master).

# Introduction to microservice

Aha~ You must be familar -- at least sounds familar with the word "microservice". More and more developers are embracing fine-grained microservice architecture. So what are microservices? In brief:

> Microservices are small, autonomous services that work together.

Let's step across the definition and see what makes microservices different.

1. First of all, microservices are small, individual, each of which focus on doing one specific thing. We split the monolithic application into several decoupled components. We focus our service boundaries on business boundaries so that the service won't grow too large. But you may wonder, **how small is small?** That is hard to answer and that always depends on your application. As Sam Newman says in the book *Building
Microservices*:

> We seem to have a very good sense of what is too big, and so it could be argued that once a piece of code no longer feels too big, it’s probably small enough.

2. In microservice architecture, components can interact between each other via whatever protocol, e.g REST, Thrift.
3. As components are individual, we can use different language, different technologies in different components -- that is so-called **polyglot support**.
4. Each component is developed, deployed and delivered independently, so it reduces the complexity of deployment.
5. Microservice architecture is usually inseparable from distributed systems, so we need to think of resilience and scaling.
6. Microservices are often designed as **Failure Oriented** as the faliure is more complicated in the distributed systems.

Microservices can ensure the cohension between each components and reduce the time to deployment and production. But remember: microservices are not a silver bullet as it increases the complexity of the whole distributed system so you need to think of more circumstances.

## Service discovery

In distributed systems, each components are indivial and they are not aware of the location of other services, but if we want to invoke other services, we need to know their locations. Hardcoded in the code is not a good idea so we need a mechanism to record the location of each services dynamically -- that is **service discovery**. With service discovery, we can publish various kind of services to the discovery infrastructure and other components can consume registered services via discovery infrastructure. We don't need to know the location so it could let your components react smoothly to location or environment changes. And it also enables load-balancing, health check and so on.

Vert.x provides us a service discovery component to publish and discover various resources. In Vert.x Service Discovery, services are described by a `Record`. Service provider can publish services, and the `Record` can be saved in local map, distributed map or Redis depending on `ServiceDiscoveryBackend`. Service consumer can retrieve service record from the discovery backend and get corresponding service instance. At present Vert.x provides out of box support of several service types such as **event bus service(service proxy)**, **HTTP endpoint**, **message source** and **data source**. And of course we can create our own service types. We'll elaborate the usage of service discovery soon.

## Asynchronous and reactive Vert.x

Asynchronous and reactive is very suitable for microservices, and Vert.x owes both of them! With `Future` based and Rx based asynchronous development model, we can compose asynchronous procedures in a reactive way. That's concise and nice! We'll see more usage of `Future` based and Rx based asynchronous methods later~

# The online shopping application

Ok, now that you've had a basic understanding of microservice architecture, let's discuss our microservice application in this blueprint. This is a online shopping application like eBay. People can buy things via it... The application contains a set of microservices currently:

- Account service - provides user account operation functionality. Use MySQL as persistence.
- Product service - provides product operation functionality. Use MySQL as persistence.
- Inventory service - provides product inventory operation functionality, e.g. `retrieve`, `increase` and `decrease`. Use Redis as persistence (via Cache infrastructure service).
- Store service - provides personal shop operation functionality. Use MongoDB as persistence.
- Shopping cart service - it manages the shopping cart operations (e.g. `add`, `remove` and `checkout`) and generates orders. Shopping carts are stored and retrieved with event sourcing pattern. Orders are sent on the event bus.
- Order dispatcher and processor - it receives order requests from the cart service via event bus and then dispatch the orders to the infrastructure services (e.g. processing, storage and logging).
- The shopping SPA - the frontend SPA of the microservice
- The monitor dashbord - a simple web UI to monitor the status of the microservice system
- The API gateway - it is responsible for the requests to corresponding REST endpoints. It is also responsible for authentication, simple load-balancing and failure handling (using Vert.x Circuit Breaker).

## Online shopping microservice architecture

Let's have a look to the microservice architecture:

![Microservice Architecture](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/entire-architecture.png)

The API gateway is the door of the entire system. Every requests must be first sent to the gateway and then dispatched to each service.

Let's then see the structure of every individual component.

## Component structure

Every high-level component contains at least two verticles: Service verticle and REST verticle. REST verticle provides REST endpoint of the service as well as publish it to the service discovery infrastructure. The service verticle is responsible for publishing event bus services, message sources to the service discovery infrastructure and then deploy REST verticles.

We have services in each component, for example `ProductService` for `product-microservice`. These kind of service interfaces are all event bus services, which are with `@ProxyGen` annotation. With `@ProxyGen` annotation, Vert.x can automatically generate service proxies so that we could do asynchronous RPC on event bus without any extra code. So cool!

![Component structure](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/ind-structure-01.png)

## Interaction between components

The application uses several types of services:

- HTTP endpoint (e.g. REST endpoint and gateway) - the service is located using an HTTP URL
- Event bus service - as we've mentioned above, we can do async RPC to consume event bus services (aka. service proxies) via the event bus. The service is located using an event bus address.
- Message source - this kind of service publishes messages to specific addresses on event bus. The service is located using an event bus address.

So these components can interact with each other via HTTP or event bus. For example:

![Interaction](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/rpc-inc-1.png)

# Let's start!

Now let's start our journey with this blueprint! First we clone the project from GitHub:

  git clone https://github.com/sczyh30/vertx-blueprint-microservice.git

First see the `pom.xml`. From it we can see our blueprint is composed of several subprojects:

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
  <module>shopping-ui</module>
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

# Future based service - Inventory service

The inventory service is responsible for operations about product inventory, e.g. `retrieve` inventory of a product, `increase` or `decrease` inventory amount. Different from the previous event bus service, the inventory service interface is not callback-based, but future-based. The service proxy does not support processing future-based asynchronous method, hence we'd only publish a HTTP endpoint.

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

Vert.x supports RxJava, and most of the components provide a Rx version. So here we implement the persistence service with Rx version of Vert.x JDBC. That is - the service is `Observable` based, so more reactive and functional!

In our implementation, We designed a `SimpleCrudDataSource` interface as the base interface:

```java
public interface SimpleCrudDataSource<T, ID> {

  Observable<Void> save(T entity);

  Observable<T> retrieveOne(ID id);

  Observable<Void> delete(ID id);

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
public Observable<Void> save(CartEvent cartEvent) {
  JsonArray params = new JsonArray().add(cartEvent.getCartEventType().name())
    .add(cartEvent.getUserId())
    .add(cartEvent.getProductId())
    .add(cartEvent.getAmount())
    .add(cartEvent.getCreatedAt() > 0 ? cartEvent.getCreatedAt() : System.currentTimeMillis());
  return client.getConnectionObservable()
    .flatMap(conn -> conn.updateWithParamsObservable(SAVE_STATEMENT, params))
    .map(r -> null);
}
```

Do you feel it's more concise and reactive by contrast with callback-based common Vert.x JDBC? Of course! Using RxJava can bring us a more reactive way. We can easily get a connection with `getConnectionObservable` method, then use the connection to execute save sql statement with the given parameters. Only two lines! By contrast, you have to write this in the common Vert.x JDBC:

```java
client.getConnection(ar -> {
  if (ar.succeeded) {
    SQLConnection connection = ar.result();
    connection.updateWithParams(SAVE_STATEMENT, params, ar2 -> {
      // ...
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
public Observable<CartEvent> retrieveOne(Long id) {
  return client.getConnectionObservable()
    .flatMap(conn -> conn.queryWithParamsObservable(RETRIEVE_STATEMENT, new JsonArray().add(id)))
    .map(ResultSet::getRows)
    .filter(list -> !list.isEmpty())
    .map(res -> res.get(0))
    .map(this::wrapCartEvent);
}
```

Very clear! It resembles `Future` based style we've mentioned so I won't explain this.

As the cart event database is designed as **append-only**, we won't implement `update` and `delete` method.

Next let's see another important implementation - `streamByUser`:

```java
@Override
public Observable<CartEvent> streamByUser(String userId) {
  JsonArray params = new JsonArray().add(userId).add(userId);
  return client.getConnectionObservable()
    .flatMap(conn -> conn.queryWithParamsObservable(STREAM_STATEMENT, params))
    .map(ResultSet::getRows)
    .flatMapIterable(item -> item) // list merge into observable
    .map(this::wrapCartEvent);
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

So back to `streamByUser` method. From our explanation above, we know we'll get a list of events, but the method returns `Observable<CartEvent>`. Why? That is because we use a operator `flatMapIterable`, to transform the single result into streams. So it is different from the `Future` in Vert.x or `CompletableFuture` in Java. The `Future` is more like a `Single` in Rx, which always either emits one value or an error notification. And the previous usage of `Observable` in `retrieveOne` and `save` method also resembles a `Single`. But in `streamByUser`, the `Observable` result is really a sequence of event stream. We'll consume the event stream in the `ShoppingCartService`.

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
  repository.save(event).subscribe(future::complete, future::fail);
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

## Checkout - generate order from shopping cart

## Send orders to order microservice

# Order microservice

# Online shopping SPA integration

![](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/shopping-spa-index.png)

# Monitor dashboard with metrics

![Monitor Dashboard](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/monitor-dashboard.png)

# Show time!

Wow, we've explored all of the code of the online shopping microservice, so it's show time! I recommend you run the microservice application with Docker Compose so that we needn't configure too much.

First build the code:

  mvn clean install

Then build all Docker containers:

  cd docker
  sudo ./build.sh

As soon as the build finished, run the microservice:

  sudo ./run.sh

The persistence containers (MySQL, MongoDB and Redis) will be started first because it might take some time to initialize. When the persistence is prepared okay, the other services will be started in order.

When the entire microservice is initialized successful, we can visit the shop SPA in broswer, by default the URL is [http://localhost:8080](http://localhost:8080).

![](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/shopping-spa-index.png)

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
