# 前言

欢迎回到Vert.x 蓝图系列！当今，**微服务架构** 变得越来越流行，开发者们都想尝试一下微服务应用的开发和架构设计。令人激动的是，Vert.x给我们提供了一系列用于微服务开发的组件，包括 **Service Discovery** (服务发现)、**Circuit Breaker** (断路器)以及其它的一些组件。有了Vert.x微服务组件的帮助，我们就可以快速利用Vert.x搭建我们的微服务应用。在这篇蓝图教程中，我们一起来探索一个利用Vert.x的各个组件开发的 Micro-Shop 微服务应用～

通过本教程，你将会学习到以下内容：

- 如何设计微服务架构
- 如何利用Vert.x来开发微服务应用
- 异步开发模式
- 响应式、函数式编程
- 事件溯源 (Event Sourcing)
- 通过分布式 Event Bus 进行异步RPC调用
- 各种各样的服务类型（例如REST、数据源、Event Bus服务等）
- 如何更灵活地配置Vert.x应用
- 如何使用服务发现模块 (Vert.x Service Discovery)
- 如何使用断路器模块 (Vert.x Circuit Breaker)
- 如何利用Vert.x实现API Gateway
- 如何进行微服务权限认证 (OAuth 2)
- 如何配置及使用 SockJS - Event Bus Bridge
- 如何利用Vert.x Metrics获取监控数据

以及其它的一些东西。。。

本教程是 [**Vert.x 蓝图系列**](http://vertx.io/blog/vert-x-blueprint-tutorials/) 的第三篇教程，对应的Vert.x版本为 **3.4.1** 。本教程中的完整代码已托管至[GitHub](https://github.com/sczyh30/vertx-blueprint-microservice)。

> 注：本项目微服务架构正在重构，重构后的架构将支持高可用、高容错性、可靠消息驱动等，敬请期待！

# 踏入微服务之门

哈～你一定对“微服务”这个词很熟悉——至少听起来很熟悉！越来越多的开发者开始拥抱微服务架构，那么微服务究竟是什么呢？一句话总结一下：

> Microservices are small, autonomous services that work together.

我们来深入一下微服务的各种特性，来看看微服务为何如此出色：

- 首先，微服务的重要的一点是“微”。每个微服务都是独立的，每个单独的微服务组件都注重某一特定的逻辑。在微服务架构中，我们将传统的单体应用拆分成许多互相独立的组件。每个组件都由其特定的“逻辑边界”，因此组件不会过于庞大。不过话又说回来了，每个组件应该有多小呢？这个问题可不好回答，它通常取决与我们的业务与负载。正如Sam Newman在其《Building
Microservices》书中所讲的那样：

> We seem to have a very good sense of what is too big, and so it could be argued that once a piece of code no longer feels too big, it’s probably small enough.

因此，当我们觉得每个组件不是特别大的时候，组件的大小可能就刚刚好。

- 在微服务架构中，组件之间可以通过任意协议进行通信，比如 **HTTP** 或 **AMQP**。
- 每个组件是独立的，因此我们可以在不同的组件中使用不同的编程语言，不同的技术 —— 这就是所谓的 **polyglot support** （不错，Vert.x也是支持多语言的！）
- 每个组件都是独立开发、部署以及发布的，所以这减少了部署及发布的难度。
- 微服务架构通常与分布式系统形影不离，所以我们还需要考虑分布式系统中的方方面面，包括可用性、弹性以及可扩展性。
- 微服务架构通常被设计成为 **面向失败的**，因为在分布式系统中失败的场景非常复杂，我们需要有效地处理失败的手段。

虽然微服务有如此多的优点，但是不要忘了，微服务可不是银弹，因为它引入了分布式系统中所带来的各种问题，因此设计架构时我们都要考虑这些情况。

## 服务发现

在微服务架构中，每个组件都是独立的，它们都不知道其他组件的位置，但是组件之间又需要通信，因此我们必须知道各个组件的位置。然而，把位置信息写死在代码中显然不好，因此我们需要一种机制可以动态地记录每个组件的位置 —— 这就是 **服务发现**。有了服务发现模块，我们就可以将服务位置发布至服务发现模块中，其它服务就可以从服务发现模块中获取想要调用的服务的位置并进行调用。在调用服务的过程中，我们不需要知道对应服务的位置，所以当服务位置或环境变动时，服务调用可以不受影响，这使得我们的架构更加灵活。

Vert.x提供了一个服务发现模块用于发布和获取服务记录。在Vert.x 服务发现模块，每个服务都被抽象成一个`Record`（服务记录）。服务提供者可以向服务发现模块中发布服务，此时`Record`会根据底层`ServiceDiscoveryBackend`的配置存储在本地Map、分布式Map或Redis中。服务消费者可以从服务发现模块中获取服务记录，并且通过服务记录获取对应的服务实例然后进行服务调用。目前Vert.x原生支持好几种服务类型，比如 **Event Bus 服务**（即服务代理）、**HTTP 端点**、**消息源** 以及 **数据源**。当然我们也可以实现自己的服务类型，可以参考[相关的文档](http://vertx.io/docs/vertx-service-discovery/java/#_implementing_your_own_service_type)。在后面我们还会详细讲述如何使用服务发现模块，这里先简单做个了解。

## 异步的、响应式的Vert.x

异步与响应式风格都很适合微服务架构，而Vert.x兼具这两种风格！异步开发模式相信大家已经了然于胸了，而如果大家读过前几篇蓝图教程的话，响应式风格大家一定不会陌生。有了基于Future以及基于RxJava的异步开发模式，我们可以随心所欲地对异步过程进行组合和变换，这样代码可以非常简洁，非常优美！在本蓝图教程中，我们会见到大量基于`Future`和RxJava的异步方法。

## Mirco Shop 微服务应用

好啦，现在大家应该对微服务架构有了一个大致的了解了，下面我们来讲一下本蓝图中的微服务应用。这是一个简单的 Micro-Shop 微服务应用 （目前只完成了基本功能），人们可以进行网上购物以及交易。。。当前版本的微服务应用包含下列组件：

- 账户服务：提供用户账户的操作服务，使用MySQL作为后端存储。
- 商品服务：提供商品的操作服务，使用MySQL作为后端存储。
- 库存服务：提供商品库存的操作服务，如查询库存、增加库存即减少库存。使用Redis作为后端存储。
- 网店服务：提供网店的操作即管理服务，使用MongoDB作为后端存储。
- 购物车服务：提供购物车事件的生成以及购物车操作（添加、删除商品以及结算）服务。我们通过此服务来讲述 **事件溯源**。
- 订单服务：订单服务从Event Bus接收购物车服务发送的订单请求，接着处理订单并将订单发送至下层服务（本例中仅仅简单地存储至数据库中）。
- Micro Shop 前端：此微服务的前端部分(SPA)，目前已整合至API Gateway组件中。
- 监视仪表板：用于监视微服务系统的状态以及日志、统计数据的查看。
- API Gateway：整个微服务的入口，它负责将收到的请求按照一定的规则分发至对应的组件的REST端点中（相当于反向代理）。它也负责权限认证与管理，负载均衡，心跳检测以及失败处理（使用Vert.x Circuit Breaker）。

## Micro Shop 微服务架构

我们来看一下Micro Shop微服务应用的架构：

![Microservice Architecture](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/entire-architecture.png)

用户请求首先经过API Gateway，再经其处理并分发至对应的业务端点。

我们再来看一下每个基础组件内部的结构（基础组件即图中最下面的各个业务组件）。

## 组件结构

每个基础组件至少有两个Verticle：服务Verticle以及REST Verticle。REST Vertice提供了服务对应的REST端点，并且也负责将此端点发布至服务发现层。而服务Verticle则负责发布其它服务（如Event Bus服务或消息源）并且部署REST Verticle。

每个基础组件中都包含对应的服务接口，如商品组件中包含`ProductService`接口。这些服务接口都是Event Bus 服务，由`@ProxyGen`注解修饰。上篇蓝图教程中我们讲过，Vert.x Service Proxy可以自动为`@ProxyGen`注解修饰的接口生成服务代理类，因此我们可以很方便地在Event Bus上进行异步RPC调用而不用写额外的代码。很酷吧！并且有了服务发现组件以后，我们可以非常方便地将Event Bus服务发布至服务发现层，这样其它组件可以更方便地调用服务。

![Component structure](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/ind-structure-01.png)

## 组件之间的通信

我们先来看一下我们的微服务应用中用到的服务类型：

- **HTTP端点** (e.g. REST 端点以及API Gateway) - 此服务的位置用URL描述
- **Event Bus服务** - 此服务的位置用Event Bus上的一个特定地址描述
- **事件源** - 事件源服务对应Event Bus上某个地址的事件消费者。此服务的位置用Event Bus上的一个特定地址描述

因此，我们各个组件之间可以通过HTTP以及Event Bus（本质是TCP）进行通信，例如：

![Interaction](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/rpc-inc-1.png)

API Gateway与其它组件通过HTTP进行通信。

# 让我们开始吧！

好啦，现在开始我们的微服务蓝图旅程吧！首先我们从GitHub上clone项目：

```
git clone https://github.com/sczyh30/vertx-blueprint-microservice.git
```

在本蓝图教程中，我们使用 **Maven** 作为构建工具。我们首先来看一下`pom.xml`配置文件。我们可以看到，我们的蓝图应用由许多模块构成：

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

每个模块代表一个组件。看着配置文件，似乎有不少组件呢！不要担心，我们将会一一探究这些组件。下面我们先来看一下所有组件的基础模块 -  `microservice-blueprint-common`。

# 微服务基础模块

`microservice-blueprint-common`模块提供了一些微服务功能相关的辅助类以及辅助Verticle。我们先来看一下两个base verticles - `BaseMicroserviceVerticle` 和 `RestAPIVerticle`。

## Base Microservice Verticle

`BaseMicroserviceVerticle`提供了与微服务相关的初始化函数以及各种各样的辅助函数。其它每一个Verticle都会继承此Verticle，因此这个基础Verticle非常重要。

首先我们来看一下其中的成员变量：

```java
protected ServiceDiscovery discovery;
protected CircuitBreaker circuitBreaker;
protected Set<Record> registeredRecords = new ConcurrentHashSet<>();
```

`discovery`以及`circuitBreaker`分别代表服务发现实例以及断路器实例，而`registeredRecords`代表当前已发布的服务记录的集合，用于在结束Verticle时注销服务。

`start`函数中主要是对服务发现实例和断路器实例进行初始化，配置文件从`config()`中获取。它的实现非常简单：

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

下面我们还提供了几个辅助函数用于发布各种各样的服务。这些函数都是异步的，并且基于Future：

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

之前我们提到过，每个服务记录`Record`代表一个服务，其中服务类型由记录中的`type`字段标识。Vert.x原生支持的各种服务接口中都包含着好几个`createRecord`方法因此我们可以利用这些方法来方便地创建服务记录。通常情况下我们需要给每个服务都指定一个`name`，这样之后我们就可以通过名称来获取服务了。我们还可以通过`setMetadata`方法来给服务记录添加额外的元数据。

你可能注意到在`publishHttpEndpoint`方法中我们就提供了含有`api-name`的元数据，之后我们会了解到，API Gateway在进行反向代理时会用到它。

下面我们来看一下发布服务的通用方法 —— `publish`方法：

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

在`publish`方法中，我们调用了服务发现实例`discovery`的`publish`方法来将服务发布至服务发现模块。它同样也是一个异步方法，当发布成功时，我们将此服务记录存储至`registeredRecords`中，输出日志然后通知`future`操作已完成。最后返回对应的`future`。

注意，在Vert.x Service Discovery当前版本(3.3.3)的设计中，服务发布者需要在必要时手动注销服务，因此当Verticle结束时，我们需要将注册的服务都注销掉：

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

在`stop`方法中，我们遍历`registeredRecords`集合并且尝试注销每一个服务，并将异步结果`future`添加至`futures`列表中。之后我们调用`CompositeFuture.all(futures)`来依次获取每个异步结果的状态。`all`方法返回一个组合的`Future`，当列表中的所有Future都成功赋值时方为成功状态，反之只要有一个异步结果失败，它就为失败状态。因此，我们给它绑定一个`Handler`，当所有服务都被注销时，服务发现模块就可以安全地关闭了，否则结束函数会失败。

## REST API Verticle

`RestAPIVerticle`抽象类继承了`BaseMicroserviceVerticle`抽象类。从名字上就可以看出，它提供了诸多的用于REST API开发的辅助方法。我们在其中封装了诸如创建服务端、开启Cookie和Session支持，开启心跳检测支持（通过HTTP），各种各样的路由处理封装以及用于权限验证的路由处理器。在之后的章节中我们将会见到这些方法。


好啦，现在我们已经了解了整个蓝图应用中的两个基础Verticle，下面是时候探索各个模块了！在探索逻辑组件之前，我们先来看一下其中最重要的组件之一 —— API Gateway。

# API Gateway


我们把API Gateway的内容**单独归为一篇教程**，请见：[Vert.x 蓝图 - Micro Shop 微服务实战 (API Gateway)](http://sczyh30.github.io/vertx-blueprint-microservice/cn/api-gateway.html)。


# Event Bus 服务 - 账户、网店及商品服务

## 在Event Bus上进行异步RPC

在之前的 [Vert.x Kue 蓝图教程](http://www.sczyh30.com/vertx-blueprint-job-queue/cn/kue-core/index.html#异步rpc) 中我们已经介绍过Vert.x中的异步RPC（也叫服务代理）了，这里我们再来回顾一下，并且说一说如何利用服务发现模块更方便地进行异步RPC。

传统的RPC有一个缺点：消费者需要阻塞等待生产者的回应。这是一种阻塞模型，和Vert.x推崇的异步开发模式不相符。并且，传统的RPC不是真正面向失败设计的。还好，Vert.x提供了一种高效的、响应式的RPC —— **异步RPC**。我们不需要等待生产者的回应，而只需要传递一个`Handler<AsyncResult<R>>`参数给异步方法。这样当收到生产者结果时，对应的`Handler`就会被调用，非常方便，这与Vert.x的异步开发模式相符。并且，`AsyncResult`也是面向失败设计的。

Vert.x Service Proxy(服务代理组件)可以自动处理含有`@ProxyGen`注解的服务接口，生成相应的服务代理类。生成的服务代理类可以帮我们将数据封装好后发送至Event Bus、从Event Bus接收数据，以及对数据进行编码和解码，因此我们可以省掉不少代码。我们需要做的就是遵循`@ProxyGen`注解的[一些限定](http://vertx.io/docs/vertx-service-proxy/java/#_restrictions_for_service_interface)。

比如，这里有一个Event Bus服务接口：

```java
@ProxyGen
public interface MyService {
  @Fluent
  MyService retrieveData(String id, Handler<AsyncResult<JsonObject>> resultHandler);
}
```

我们可以通过Vert.x Service Proxy组件生成对应的代理类。然后我们就可以通过`ProxyHelper`类的`registerService`方法将此服务注册至Event Bus上：

```java
MyService myService = MyService.createService(vertx, config);
ProxyHelper.registerService(MyService.class, vertx, myService, SERVICE_ADDRESS);
```

有了服务发现组件之后，将服务发布至服务发现层就非常容易了。比如在我们的蓝图应用中我们使用封装好的方法：

```java
publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, MyService.class)
```

OK，现在服务已经成功地发布至服务发现模块。现在我们就可以通过`EventBusService`接口的`getProxy`方法来从服务发现层获取发布的Event Bus服务，并且像调用普通异步方法那样进行异步RPC：

```java
EventBusService.<MyService>getProxy(discovery, new JsonObject().put("name", SERVICE_NAME), ar -> {
  if (ar.succeeded()) {
    MyService myService = ar.result();
    myService.retrieveData(...);
  }
});
```

## 几个服务模块的通用特性

在我们的Micro Shop微服务应用中，账户、网店及商品服务有几个通用的特性及约定。现在我们来解释一下。

在这三个模块中，每个模块都包含：

- 一个Event Bus服务接口。此服务接口定义了对实体存储的各种操作
- 服务接口的实现
- REST API Verticle，用于创建服务端并将其发布至服务发现模块
- Main Verticle，用于部署其它的verticles以及将Event Bus服务和消息源发布至服务发现层

其中，用户账户服务以及商品服务都使用 MySQL 作为后端存储，而网店服务则以 MongoDB 作为后端存储。这里我们只挑两个典型的服务介绍如何通过Vert.x操作不同的数据库：`product-microservice`和`store-microservice`。`account-microservice`的实现与`product-microservice`非常相似，大家可以查阅 [GitHub](https://github.com/sczyh30/vertx-blueprint-microservice/tree/master/account-microservice) 上的代码。

## 基于MySQL的商品服务

商品微服务模块提供了商品的操作功能，包括添加、查询（搜索）、删除与更新商品等。其中最重要的是`ProductService`服务接口以及其实现了。我们先来看一下此服务接口的定义：

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

正如我们之前所提到的那样，这个服务接口是一个Event Bus服务，所以我们需要给它加上`@ProxyGen`注解。这些方法都是异步的，因此每个方法都需要接受一个`Handler<AsyncResult<T>>`参数。当异步操作完成时，对应的`Handler`会被调用。注意到我们还给此接口加了`@VertxGen`注解。上篇蓝图教程中我们提到过，这是为了开启多语言支持(polyglot language support)。Vert.x Codegen注解处理器会自动处理含有`@VertxGen`注解的类，并生成支持的其它语言的代码，如Ruby、JS等。。。这是非常适合微服务架构的，因为不同的组件可以用不同的语言进行开发！

每个方法的含义都在注释中给出了，这里就不解释了。

商品服务接口的实现位于`ProductServiceImpl`类中。商品信息存储在MySQL中，因此我们可以通过 **Vert.x-JDBC** 对数据库进行操作。我们在 [第一篇蓝图教程](http://sczyh30.github.io/vertx-blueprint-todo-backend/cn) 中已经详细讲述过Vert.x JDBC的使用细节了，因此这里我们就不过多地讨论细节了。这里我们只关注如何减少代码量。因为通常简单数据库操作的过程都是千篇一律的，因此做个封装是很有必要的。

首先来回顾一下每次数据库操作的过程：

1. 从`JDBCClient`中获取数据库连接`SQLConnection`，这是一个异步过程
2. 执行SQL语句，绑定回调`Handler`
3. 最后不要忘记关闭数据库连接以释放资源

对于正常的CRUD操作来说，它们的实现都很相似，因此我们封装了一个`JdbcRepositoryWrapper`类来实现这些通用逻辑。它位于`io.vertx.blueprint.microservice.common.service`包中：

![JdbcRepositoryWrapper class structure](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/jdbc-repo-wrapper-class-structure.png)

我们提供了以下的封装方法：

- `executeNoResult`: 执行含参数的SQL语句 (通过`updateWithParams`方法)。执行结果是不需要的，因此只需要接受一个 `Handler<AsyncResult<Void>>` 类型的参数。此方法通常用于`insert`之类的操作。
- `retrieveOne`: 执行含参数的SQL语句，用于获取某一特定实体（通过 `queryWithParams`方法）。此方法是基于`Future`的，它返回一个`Future<Optional<JsonObject>>`类型的异步结果。如果结果集为空，那么返回一个空的`Optional` monad。如果结果集不为空，则返回第一个结果并用`Optional`进行包装。
- `retrieveMany`: 获取多个实体，返回`Future<List<JsonObject>>`作为异步结果。
- `retrieveByPage`: 与`retrieveMany` 方法相似，但包含分页逻辑。
- `retrieveAll`: similar to `retrieveMany` method but does not require query parameters as it simply executes statement such as `SELECT * FROM xx_table`.
- `removeOne` and `removeAll`: remove entity from the database.

当然这与Spring JPA相比的不足之处在于SQL语句得自己写，自己封装也不是很方便。。。考虑到Vert.x JDBC底层也只是使用了Worker线程池包装了原生的JDBC（不是真正的异步），我们也可以结合Spring Data的相关组件来简化开发。另外，Vert.x JDBC使用C3P0作为默认的数据库连接池，C3P0的性能我想大家应该都懂。。。因此换成性能更优的HikariCP是很有必要的。

回到`JdbcRepositoryWrapper`中来。这层封装可以大大地减少代码量。比如，我们的`ProductServiceImpl`实现类就可以继承`JdbcRepositoryWrapper`类，然后利用这些封装好的方法。看个例子 —— `retrieveProduct`方法的实现：

```java
@Override
public ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler) {
  this.retrieveOne(productId, FETCH_STATEMENT)
    .map(option -> option.map(Product::new).orElse(null))
    .setHandler(resultHandler);
  return this;
}
```

我们唯一需要做的只是将结果变换成需要的类型。是不是很方便呢？

当然这不是唯一方法。在下面的章节中，我们将会讲到一种更Reactive，更Functional的方法 —— 利用Rx版本的Vert.x JDBC。另外，用`vertx-sync`也是一种不错的选择（类似于async/await）。

好啦！看完服务实现，下面轮到REST API了。我们来看看`RestProductAPIVerticle`的实现：

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

此Verticle继承了`RestAPIVerticle`，因此我们可以利用其中诸多的辅助方法。首先来看一下启动过程，即`start`方法。首先我们先调用`super.start()`来初始化服务发现组件，然后创建`Router`，绑定`BodyHandler`以便操作请求正文，然后创建各个API路由并绑定相应的处理函数。接着我们调用`enableHeartbeatCheck`方法开启简单的心跳检测支持。最后我们通过封装好的`createHttpServer`创建HTTP服务端，并通过`publishHttpEndpoint`方法将HTTP端点发布至服务发现模块。

其中`createHttpServer`方法非常简单，我们只是把`vertx.createHttpServer`方法变成了基于`Future`的：

```java
protected Future<Void> createHttpServer(Router router, String host, int port) {
  Future<HttpServer> httpServerFuture = Future.future();
  vertx.createHttpServer()
    .requestHandler(router::accept)
    .listen(port, host, httpServerFuture.completer());
  return httpServerFuture.map(r -> null);
}
```

至于各个路由处理逻辑如何实现，可以参考 [Vert.x Blueprint - Todo Backend Tutorial](http://www.sczyh30.com/vertx-blueprint-todo-backend/cn) 获取相信信息。

最后我们打开此微服务模块中的Main Verticle - `ProductVerticle`类。正如我们之前所提到的，它负责发布服务以及部署REST Verticle。我们来看一下其`start`方法：

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

首先我们创建一个`ProductService`服务实例（1），然后通过`registerService`方法将服务注册至Event Bus（2）。接着我们初始化数据库表（3），将商品服务发布至服务发现层（4）然后部署REST Verticle（5）。这是一系列的异步方法的组合操作，很溜吧！最后我们将`future.completer()`绑定至组合后的`Future`上，这样当所有异步操作都OK的时候，`Future`会自动完成。

当然，不要忘记在配置里指定`api.name`。之前我们在 [API Gateway章节](http://sczyh30.github.io/vertx-blueprint-microservice/cn/api-gateway.html) 提到过，API Gateway的反向代理部分就是通过对应REST服务的 `api.name` 来进行请求分发的。默认情况下`api.name`为`product`:

```json
{
  "api.name": "product"
}
```

# 基于Redis的商品库存服务

> TODO: Redis + MySQL高可用架构

商品库存服务负责操作商品的库存数量，比如添加库存、减少库存以及获取当前库存数量。库存使用Redis来存储。

与之前的Event Bus服务不同，我们这里的商品库存服务是基于`Future`的，而不是基于回调的。由于服务代理模块不支持处理基于`Future`的服务接口，因此这里我们就不用异步RPC了，只发布一个REST API端点，所有的调用都通过REST进行。

首先来看一下`InventoryService`服务接口：

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

接口定义非常简单，含义都在注释中给出了。接着我们再看一下服务的实现类`InventoryServiceImpl`类。在Redis中，所有的库存数量都被存储在`inventory:v1`命名空间中，并以商品号`productId`作为标识。比如商品`A123456`会被存储至`inventory:v1:A123456`键值对中。

Vert.x Redis提供了`incrby`和`decrby`命令，可以很方便地实现库存增加和减少功能，代码类似。这里我们只看库存增加功能：

```java
@Override
public Future<Integer> increase(String productId, int increase) {
  Future<Long> future = Future.future();
  client.incrby(PREFIX + productId, increase, future.completer());
  return future.map(Long::intValue);
}
```

由于库存数量不会非常大，`Integer`就足够了，因此我们需要通过`Long::intValue`方法引用来将`Long`结果变换成`Integer`类型的。

`retrieveInventoryForProduct`方法的实现也非常短小精悍：

```java
@Override
public Future<Integer> retrieveInventoryForProduct(String productId) {
  Future<String> future = Future.future();
  client.get(PREFIX + productId, future.completer());
  return future.map(r -> r == null ? "0" : r)
    .map(Integer::valueOf);
}
```

我们通过`get`命令来获取值。由于结果是`String`类型的，因此我们需要自行将其转换为`Integer`类型。如果结果为空，我们就认为商品没有库存，返回`0`。

至于REST Verticle（在此模块中也为Main Verticle），其实现模式与前面的大同小异，这里就不展开说了。不要忘记在`config.json`中指定`api.name`:

```JSON
{
  "api.name": "inventory",
  "redis.host": "redis",
  "inventory.http.address": "inventory-microservice",
  "inventory.http.port": 8086
}
```

# 事件溯源 - 购物车服务

> 注：此部分在日后会有较大变动。

好了，现在我们与基础服务模块告一段落了。下面我们来到了另一个重要的服务模块 —— 购物车微服务。此模块负责购物车的获取、购物车事件的添加以及结算功能。与传统的实现不同，这里我们要介绍一种不同的开发模式 —— 事件溯源(**Event Sourcing**)。

## 解道Event Sourcing

在传统的数据存储模式中，我们通常直接将数据本身的状态存储至数据库中。这在一般场景中是没有问题的，但有些时候，我们不仅想获取到数据，还想获取数据操作的过程（即此数据是经过怎样的操作生成的），这时候我们就可以利用事件溯源(Event Sourcing)来解决这个问题。

[事件溯源](http://martinfowler.com/eaaDev/EventSourcing.html)保证了数据状态的变换都以一系列的事件的形式存储在数据库中。所以，我们不仅可以获取每个变换的事件，而且可以通过过去的事件来组合出过去任意时刻的数据状态！这真是极好的～注意，有一点很重要，我们不能更改已经保存的事件以及它们的序列 —— 也就是说，事件存储是只能添加而不能删除的，并且需要不可变。是不是感觉和数据库事务日志的原理差不多呢？

在微服务架构中，事件溯源模式可以带来以下的好处：

- 我们可以从过去的事件序列中组建出任意时刻的数据状态
- 每个过去的事件都得以保存，因此这使得[补偿事务](https://en.wikipedia.org/wiki/Compensating_transaction)成为可能
- 我们可以从事件存储中获取事件流，并且以异步、响应式风格对其进行变换和处理
- 事件存储同样可以当作为数据日志

事件存储的选择也需要好好考虑。**Apache Kafka**非常适合这种场景，在此版本的Micro Shop微服务中，为了简化其实现，我们简单地使用了MySQL作为事件存储。下个版本我们将把Kafka整合进来。

> 注：在实际生产环境中，购物车通常被存储于Session或缓存内。本章节仅为介绍事件溯源而使用事件存储模式。

## 购物车事件

我们来看一下代表购物车事件的`CartEvent`数据对象：

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

一个购物车事件存储着事件的类型、发生的时间、操作用户、对应的商品ID以及商品数量变动。在我们的蓝图应用中，购物车事件一共有四种，它们用`CartEventType`枚举类表示：

```java
public enum CartEventType {
  ADD_ITEM, // 添加商品至购物车
  REMOVE_ITEM, // 从购物车中删除商品
  CHECKOUT, // 结算并清空
  CLEAR_CART // 清空
}
```

其中`CHECKOUT`和`CLEAR_CART`事件是对整个购物车实体进行操作，对应的购物车事件参数类似，因此我们写了两个静态方法来创建这两种事件。

另外我们还注意到一个静态方法`isTerminal`，它用于检测当前购物车事件是否为一个“终结”事件。所谓的“终结”，指的是到此就对整个购物车进行操作（结算或清空）。在从购物车事件流构建出对应的购物车状态的时候，此方法非常有用。

## 购物车实体

看完了购物车事件，我们再来看一下购物车。购物车实体用`ShoppingCart`数据对象表示，它包含着一个商品列表表示当前购物车中的商品即数量：

```java
private List<ProductTuple> productItems = new ArrayList<>();
```

其中`ProductTuple`数据对象包含着商品号、商品卖家ID、单价以及当前购物车中次商品的数目`amount`。

为了方便，我们还在`ShoppingCart`类中放了一个`amountMap`用于暂时存储商品数量：

```java
private Map<String, Integer> amountMap = new HashMap<>();
```

由于它只是暂时存储，我们不希望在对应的JSON数据中看到它，所以把它的getter和setter方法都注解上`@GenIgnore`。

在事件溯源模式中，我们要从一系列的购物车事件构建对应的购物车状态，因此我们需要一个`incorporate`方法将每个购物车事件“合并”至购物车内以变更对应的商品数目：

```java
public ShoppingCart incorporate(CartEvent cartEvent) {
  // 此事件必须为添加或删除事件
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

实现倒是比较简单，我们首先来检查要合并的事件是不是添加商品或移除商品事件，如果是的话，我们就根据事件类型以及对应的数量变更来改变当前购物车中该商品的数量(`amountMap`)。

## 使用Rx版本的Vert.x JDBC

我们现在已经了解购物车微服务中的实体类了，下面该看看购物车事件存储服务了。

之前用callback-based API写Vert.x JDBC操作总感觉心累，还好Vert.x支持与RxJava进行整合，并且几乎每个Vert.x组件都有对应的Rx版本！是不是瞬间感觉整个人都变得Reactive了呢～(⊙o⊙) 这里我们就来使用Rx版本的Vert.x JDBC来写我们的购物车事件存储服务。也就是说，里面所有的异步方法都将是基于`Single`/`Observable`的，很有FRP风格！

我们首先定义了一个简单的CRUD接口`SimpleCrudDataSource`：

```java
public interface SimpleCrudDataSource<T, ID> {

  Single<Void> save(T entity);

  Single<Optional<T>> retrieveOne(ID id);

  Single<Void> delete(ID id);

}
```

这里的`Single`和我们平常所用的`Future`语义类似。接着我们定义了一个`CartEventDataSource`接口，定义了购物车事件获取的相关方法：

```java
public interface CartEventDataSource extends SimpleCrudDataSource<CartEvent, Long> {

  Observable<CartEvent> streamByUser(String userId);

}
```

可以看到这个接口只有一个方法 —— `streamByUser`方法。它会返回某一用户对应的购物车事件流，这样后面我们就可以对其进行流式变换操作了！

下面我们来看一下服务的实现类`CartEventDataSourceImpl`。首先是`save`方法，它将一个事件存储至事件数据库中：

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

> **注意**：在数据库操作执行结束时，我们要及时调用`close`方法关闭数据库连接以释放资源。在RxJava中我们可以利用`doOnTerminate`和`doAfterTerminate`操作执行关闭数据库连接的逻辑。

看看我们的代码，在对比对比普通的callback-based的Vert.x JDBC，是不是更加简洁，更加reactive呢？我们可以非常简单地通过`rxGetConnection`方法获取数据库连接，然后组合`rxUpdateWithParams`方法执行对应的含参SQL语句。只需要两行有木有！而如果用 callback-based 的风格的话，你只能这么写：

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

因此，使用RxJava是非常愉快的一件事！当然，不要忘记返回的`Single`/`Observable`是 **cold** 的，因此只有在它被`subscribe`的时候，数据才会被消费。

不过话说回来了，Vert.x JDBC底层本质还是阻塞型的调用。要实现真正的异步数据库操作，我们可以利用 [Vert.x MySQL / PostgreSQL Client](http://vertx.io/docs/vertx-mysql-postgresql-client/java/) 这个组件，底层使用Scala写的异步数据库操作库，不过目前功能还不是非常全，大家可以自己尝尝鲜。

下面我们再来看一下`retrieveOne`方法，它从数据存储中获取特定ID的事件：

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

非常简洁明了，就像之前我们的基于`Future`的范式相似，因此这里就不再详细解释了～

下面我们来看一下里面最重要的方法 —— `streamByUser`方法：

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

其核心在于它的SQL语句`STREAM_STATEMENT`：

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

此SQL语句执行时会获取与当前购物车相关的所有购物车事件。注意到我们有许多用户，每个用户可能会有许多购物车事件，它们属于不同时间的购物车，那么如何来获取相关的事件呢？方法是 —— 首先我们获取最近一次“终结”事件发生对应的时间，那么当前购物车相关的购物车事件就是在此终结事件发生后所有的购物车事件。

明白了这一点，我们再回到`streamByUser`方法中来。既然此方法是从数据库中获取一个事件列表，那么为什么此方法返回`Observable<CartEvent>`而不是`Observable<List<CartEvent>>`呢？我们来看看其中的奥秘 —— `flatMapIterable`操作，它将一个序列变换为一串数据流。你可以把`Observable`看作管道，数据源源不断地流入并被消费。所以，这里的`Observable<CartEvent>`与Java 8的`CompletableFuture`以及RxJava的`Single`就有些不同了。`CompletableFuture`更像是RxJava中的`Single`，它仅仅发送一个值或一个错误信息，而`Observable`本身则就像是一个数据流，数据源源不断地从发布者流向订阅者。我们将会在购物车服务`ShoppingCartService`中处理事件流。

现在你一定又被Rx这种函数响应式风格所吸引了～在下面的部分中，我们将探索购物车服务及其实现，基于`Future`，同样非常Reactive！

## 根据购物车事件序列构建对应的购物车状态

我们首先来看一下`ShoppingCartService` —— 购物车服务接口，它也是一个Event Bus服务：

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

这里我们定义了两个方法：`addCartEvent`用于将购物车事件存储至事件存储中；`getShoppingCart`方法用于获取某个用户当前购物车的状态。

下面我们来看一下其实现类 —— `ShoppingCartServiceImpl`。首先是`addCartEvent`方法，它非常简单：

```java
@Override
public ShoppingCartService addCartEvent(CartEvent event, Handler<AsyncResult<Void>> resultHandler) {
  Future<Void> future = Future.future();
  repository.save(event).toSingle().subscribe(future::complete, future::fail);
  future.setHandler(resultHand
  return this;
}
```

正如之前我们所提到的，这里`save`方法返回的`Observable`其实更像个`Single`，因此我们将其通过`toSingle`方法变换为`Single`，然后通过`subscribe(future::complete, future::fail)`将其转化为`Future`以便于给其绑定一个`Handler<AsyncResult<Void>>`类型的处理函数。

而`getShoppingCart`方法的逻辑位于`aggregateCartEvents`方法中，此方法非常重要，并且是基于`Future`的。我们先来看一下代码：

```java
private Future<ShoppingCart> aggregateCartEvents(String userId) {
  Future<ShoppingCart> future = Future.future();
  // aggregate cart events into raw shopping cart
  repository.streamByUser(userId) // (1)
    .takeWhile(cartEvent -> !CartEvent.isTerminal(cartEvent.getCartEventType())) // (2)
    .reduce(new ShoppingCart(), ShoppingCart::incorporate) // (3)
    .toSingle()
    .subscribe(future::complete, future::fail); // (4)

  return future.compose(cart ->
    getProductService() // (5)
      .compose(service -> prepareProduct(service, cart)) // (6) prepare product data
      .compose(productList -> generateCurrentCartFromStream(cart, productList)) // (7) prepare product items
  );
}
```

我们来详细地解释一下。首先我们先创建个`Future`，然后先通过`repository.streamByUser(userId)`方法获取事件流（1），然后我们使用`takeWhile`算子来获取所有的`ADD_ITEM`和`REMOVE_ITEM`类型的事件（2）。`takeWhile`算子在判定条件变为假时停止发射新的数据，因此当事件流遇到一个终结事件时，新的事件就不再往外发送了，之前的事件将会继续被传递。

下面就是产生购物车状态的过程了！我们通过`reduce`算子将事件流来“聚合”成购物车实体（3）。这个过程可以总结为以下几步：首先我们先创建一个空的购物车，然后依次将各个购物车事件“合并”至购物车实体中。最后聚合而成的购物车实体应该包含一个完整的`amountMap`。

现在此`Observable`已经包含了我们想要的初始状态的购物车了。我们将其转化为`Single`然后通过`subscribe(future::complete, future::fail)`转化为`Future`（4）。

现在我们需要更多的信息以组件一个完整的购物车，所以我们首先组合`getProductService`异步方法来从服务发现层获取商品服务（5），然后通过`prepareProduct`方法来获取需要的商品数据（6），最后通过`generateCurrentCartFromStream`异步方法组合出完整的购物车实体（7）。这里面包含了好几个组合过程，我们来一一解释。

首先来看`getProductService`异步方法。它用于从服务发现层获取商品服务，然后返回其异步结果：

```java
private Future<ProductService> getProductService() {
  Future<ProductService> future = Future.future();
  EventBusService.getProxy(discovery,
    new JsonObject().put("name", ProductService.SERVICE_NAME),
    future.completer());
  return future;
}
```

现在我们获取到商品服务了，那么下一步自然是获取需要的商品数据了。这个过程通过`prepareProduct`异步方法实现：

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

在此实现中，首先我们从`amountMap`中获取购物车中所有商品的ID（1），然后我们根据每个ID异步调用商品服务的`retrieveProduct`方法并且以`Future`包装（2），然后将此流转化为`List<Future<Product>>`类型的列表（3）。我们这里想获得的是所有商品的异步结果，即`Future<List<Product>>`，那么如何转换呢？这里我写了一个辅助函数`sequenceFuture`来实现这样的变换，它位于`io.vertx.blueprint.microservice.common.functional`包下的`Functional`类中：

```java
public static <R> Future<List<R>> sequenceFuture(List<Future<R>> futures) {
  return CompositeFutureImpl.all(futures.toArray(new Future[futures.size()])) // (1)
    .map(v -> futures.stream()
        .map(Future::result) // (2)
        .collect(Collectors.toList()) // (3)
    );
}
```

此方法对于想将一个Future序列变换成单个Future的情况非常有用。这里我们首先调用`CompositeFutureImpl`类的`all`方法（1），它返回一个组合的Future，当且仅当序列中所有的Future都成功完成时，它为成功状态，否则为失败状态。下面我们就对此组合Future做变换：获取每个`Future`对应的结果（因为`all`方法已经强制获取所有结果），然后归结成列表（3）。

回到之前的组合中来！现在我们得到了我们需要的商品信息列表`List<Product>`，接下来就根据这些信息来构建完整的购物车实体了！我们来看一下`generateCurrentCartFromStream`方法的实现：

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

看起来非常混乱的样子。。。不要担心，我们慢慢来～注意这个方法本身不是异步的，但我们需要表示此方法成功或失败两种状态(即`AsyncResult`)，所以此方法仍然返回`Future`。首先我们创建一个`Future`，然后通过`anyMatch`方法检查商品列表是否合法（1）。若不合法，返回一个失败的`Future`；若合法，我们对每个商品依次构建出对应的`ProductTuple`。在（3）中，我们通过这个构造函数来构建`ProductTuple`:

```java
public ProductTuple(Product product, Integer amount) {
  this.productId = product.getProductId();
  this.sellerId = product.getSellerId();
  this.price = product.getPrice();
  this.amount = amount;
}
```

其中第一个参数是对应的商品实体。为了从列表中获取对应的商品实体，我们写了一个`getProductFromStream`方法：

```java
private Product getProductFromStream(List<Product> productList, String productId) {
  return productList.stream()
    .filter(product -> product.getProductId().equals(productId))
    .findFirst()
    .get();
}
```

当每个商品的`ProductTuple`都构建完毕的时候，我们就可以将列表赋值给对应的购物车实体了（6），并且返回购物车实体结果（7）。现在我们终于整合出一个完整的购物车了！

## 结算 - 根据购物车产生订单

现在我们已经选好了自己喜爱的商品，把购物车填的慢慢当当了，下面是时候进行结算了！我们这里同样定义了一个结算服务接口`CheckoutService`，它只包含一个特定的方法：`checkout`：

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

接口非常简单，下面我们来看其实现 —— `CheckoutServiceImpl`类。尽管接口只包含一个`checkout`方法，但我们都知道结算过程可不简单。。。它包含库存检测、付款（这里暂时省掉了）以及生成订单的逻辑。我们先来看看`checkout`方法的源码：

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
        // 创建订单实体
        Order order = new Order().setBuyerId(userId) // (5)
          .setPayId("TEST")
          .setProducts(cart.getProductItems())
          .setTotalPrice(totalPrice);
        // 设置订单流水号，然后向订单组件发送订单并等待回应
        return retrieveCounter("order") // (6)
          .compose(id -> sendOrderAwaitResult(order.setOrderId(id))) // (7)
          .compose(result -> saveCheckoutEvent(userId).map(v -> result)); // (8)
      } else {
        // 库存不足，结算失败
        return Future.succeededFuture(new CheckoutResult()
          .setMessage(checkResult.getString("message"))); // (9)
      }
    })
  );

  orderFuture.setHandler(resultHandler); // (10)
}
```

好吧，我们又看到了大量的`compose`。。。是的，这里我们又组合了很多基于`Future`的异步方法。首先我们先来判断给定的`userId`是否合法（1），如果不合法的话立刻让`Future`失败掉；若用户合法，我们就通过`getCurrentCart`方法获取给定用户的当前购物车状态（2）。这个过程是异步的，所以此方法返回`Future<ShoppingCart>`类型的异步结果：

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

在`getCurrentCart`方法中，我们通过`EventBusService`接口的`getProxy`方法从服务发现层获取购物车服务；然后我们调用购物车服务的`getShoppingCart`方法获取购物车。这里我们还需要检验购物车是否为空，购物车不为空的话就返回异步结果，为空的话结算显然不合适，返回不合法错误。

你可能已经注意到了`checkout`方法会产生一个`CheckoutResult`类型的异步结果，这代表结算的结果：

```java
@DataObject(generateConverter = true)
public class CheckoutResult {
  private String message; // 结算结果信息
  private Order order; // 若成功，此项为订单实体
}
```

回到我们的`checkout`方法中来。现在我们要从获取到的`cartFuture`进行一系列的操作，最终得到`Future<CheckoutResult>`类型的结算结果。那么进行哪些操作呢？首先我们组合`checkAvailableInventory`异步方法，它用于获取商品库存检测数据，后面我们讲详细讨论其实现。接着我们检查获取到的商品库存数据，判断是否所有库存都充足（3）。如果不充足的话，我们直接返回一个`CheckoutResult`并标记库存不足的信息（9）。如果库存充足，我们就计算出此订单的总价（4）然后生成订单`Order`（5）。订单用`Order`数据对象表示，它包含以下信息：

- 买家ID
- 每个所选商品的数量、单价以及卖家ID
- 商品总价

生成初始订单之后，我们需要从计数器服务中生成该订单的流水号（6），接着通过Event Bus向订单组件中发送订单数据，并且等待结账结果`CheckoutResult`（7）。这些都做完以后，我们向事件存储中添加购物车结算事件（8）。最后我们向最终得到的`orderFuture`绑定`resultHandler`处理函数（10）。当结账结果回复过来的时候，处理函数将会被调用。

下面我们来解释一下上面出现过的一些异步过程。首先是最先提到的用于准备库存数据的`checkAvailableInventory`方法：

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

有点复杂呢。。。首先我们通过`getInventoryEndpoint`方法来从服务发现层获取商品库存组件对应的REST端点（1）。这是对`HttpEndpoint`接口的`getClient`方法的简单封装：

```java
private Future<HttpClient> getInventoryEndpoint() {
  Future<HttpClient> future = Future.future();
  HttpEndpoint.getClient(discovery,
    new JsonObject().put("name", "inventory-rest-api"), // service name
    future.completer());
  return future;
}
```

接着我们又要组合另一个`Future`。在这个过程中，我们从购物车中获取商品列表（2），然后将每个`ProductTuple`都变换成对应的商品ID以及对应库存（3）。之前我们已经获取到库存服务REST端点对应的`HttpClient`了，下面我们就可以通过客户端来获取每个商品的库存。获取库存的过程是在`getInventory`方法中实现的：

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

过程非常简洁明了。首先我们先创建一个`Future<Integer>`来保存库存数量异步结果（A）。然后我们调用`client`的`get`方法来发送获取库存的请求（B）。在对回应的处理逻辑`responseHandler`中，如果结果状态为 **200 OK**（C），我们就可以通过`bodyHandler`来解析回应正文并将其转换为`Integer`类型（D）。这几个过程都完成后，对应的future会被赋值为对应的库存数量；如果结果状态不正常（比如400或404），那么我们就可以认为获取失败，将future置为失败状态（E）。

只有库存数量是不够的（因为我们不知道库存对应哪个商品），因此为了方便起见，我们将库存数量和对应的商品号以及购物车中选定的数量都塞进一个`JsonObject`中，最后将`Future<Integer>`变换为`Future<JsonObject>`类型的结果（F）。

再回到`checkAvailableInventory`方法中来。在(3)过程之后，我们有的到了一个Future列表，所以我们再次调用`Functional.sequenceFuture`方法将其变换成`Future<List<JsonObject>>`类型（4）。现在我们可以来检查每个库存是否都充足了！我们创建了一个列表`insufficient`专门存储库存不足的商品，这是通过`filter`算子实现的（5）。如果库存不足的商品列表不为空，那就是说有商品库存不足，所以我们需要获取每个库存不足的商品ID并把其归纳成一串信息。这里我们通过`collect`算子实现的：`collect(Collectors.joining(", "))` （6）。这个小trick还是很好使的，比如列表`[TST-0001, TST-0002, BK-16623]`会被归结成 "TST-0001, TST-0002, BK-16623" 这样的字符串。生成库存不足商品的信息以后，我们将此信息置于`JsonObject`中。同时，我们在此`JsonObject`中用一个bool型的`res`来表示商品库存是否充足，因此这里我们将`res`的值设为false（7）。

如果之前获得的库存不足的商品列表为空，那么就代表所有商品余额充足，我们就将`res`的值设为true（8），最后返回异步结果`future`。

再回到那一串组合中。我们接着通过`calculateTotalPrice`方法来计算购物车中商品的总价，以便为订单生成提供信息。这个过程很简单：

```java
return cart.getProductItems().stream()
  .map(p -> p.getAmount() * p.getPrice()) // join by product id
  .reduce(0.0d, (a, b) -> a + b);
```

正如之前在`checkout`方法中提到的那样，在创建原始订单之后，我们会对结果进行三个组合：`retrieveCounter -> sendOrderAwaitResult -> saveCheckoutEvent`。我们来看一下。

我们首先从缓存组件的计数器服务中生成当前订单的流水号：

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

当然你也可以直接用数据库自带的`AUTO INCREMENT`计数器，不过当有多台数据库服务器的时候，我们需要保证计数器在集群内的一致性。

接着我们通过`saveCheckoutEvent`方法存储购物车结算事件，其实现和`getCurrentCart`方法非常类似。它们都是先从服务发现层中获取购物车服务，然后再异步调用对应的逻辑：

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

## 向订单模块发送订单

生成订单流水号以后，现在我们的订单实体已经是完整的了，可以向下层订单服务组件发送了。我们来看一下其实现 —— `sendOrderAwaitResult`方法：

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

我们将订单实体发送至Event Bus上的一个特定地址中，这样在订单服务组件中，订单服务就能够从Event Bus上获取发送的订单并对其进行处理和分发。注意到我们调用的`send`函数同时还接受一个`Handler<AsyncResult<Message<T>>>`类型的参数，这意味着我们需要等待消息接收者发送回的回复消息。这其实是一种类似于 **请求/回复模式** 的消息模式。如果我们成功地接收到回复消息，我们就将其转化为订单结果`CheckoutResult`并且给`future`赋值；如果我们收到了失败的消息，或者接受消息超时，我们就将`future`标记为失败。

好啦！在经历了一系列的“组合”过程之后，我们终于完成了对`checkout`方法的探索。是不是感觉很Reactive呢？

由于订单服务并不知道我们发送的地址，我们需要向服务发现层中发布一个 **消息源**，这里的消息源其实就是我们将订单发送的位置。订单就可以通过服务发现层获取对应的消费者`MessageConsumer`，然后从此处接受订单。我们将会在`CartVerticle`中发布此消息源，不过在看`CartVerticle`的实现之前，我们先来瞥一眼购物车服务的REST Verticle。

## 购物车服务REST API

在购物车服务相关的REST Verticle里有三个主要的API：

- GET `/cart` - 获取当前用户的购物车状态
- POST `/events` - 向购物车事件存储中添加一个新的与当前用户相关的购物车事件
- POST `/checkout` - 发出购物车结算请求

注意这三个API都需要权限（登录用户），因此它们的路由处理函数都包装着`requireLogin`方法。这一点已经在之前的 [API Gateway章节](http://sczyh30.github.io/vertx-blueprint-microservice/cn/api-gateway.html) 中提到过：

```java
// api route handler
router.post(API_CHECKOUT).handler(context -> requireLogin(context, this::apiCheckout));
router.post(API_ADD_CART_EVENT).handler(context -> requireLogin(context, this::apiAddCartEvent));
router.get(API_GET_CART).handler(context -> requireLogin(context, this::apiGetCart));
```

它们的路由函数实现倒是非常简单，我们这里只看一个`apiAddCartEvent`方法：

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

首先我们从当前的用户凭证`principal`中获取用户ID。如果当前用户凭证中获取不到ID，那么我们就暂时用`TEST_USER`来替代（1）。然后我们根据请求正文来创建购物车事件`CartEvent`（2）。我们同时需要验证购物车事件中的用户与当前作用域内的用户是否相符。若相符，则调用服务的`addCartEvent`方法将事件添加至事件存储中，并在成功时返回 **201* 状态（3）。如果请求正文中的购物车事件不合法，我们就返回 **400 Bad Request** 状态（4）。

## Cart Verticle

`CartVerticle`是购物车服务组件的Main Verticle,用于发布各种服务。这里我们会发布三个服务：

- `shopping-checkout-eb-service`: 结算服务，这是一个 **Event Bus 服务**。
- `shopping-cart-eb-service`: 购物车服务，这是一个 **Event Bus 服务**。
- `shopping-order-message-source`: 发送订单的消息源，这是一个 **消息源服务**。

同时我们的`CartVerticle`也负责部署`RestShoppingAPIVerticle`。注意不要忘掉设置`api.name`:

```json
{
  "api.name": "cart"
}
```

这是购物车部分的UI：

![Cart Page](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/spa-cart.png)

# 订单服务

> 注：此部分在日后会有较大变动。

好啦！现在我们已经提交了结算请求，在底层订单已经发送至订单微服务组件中了。所以下一步自然就是订单服务的责任了 —— 分发订单以及处理订单。在当前版本的Micro Shop实现中，我们仅仅将订单存储至数据库中并变更对应的商品库存数额。在正常的生产环境中，我们通常会将订单push到消息队列中，并且在下层服务中从消息队列中pull订单并进行处理。

订单存储服务的实现与之前太类似了，因此这里就不讲`OrderService`及其实现的细节了。大家可以自行查看[相关代码](https://github.com/sczyh30/vertx-blueprint-microservice/blob/master/order-microservice/src/main/java/io/vertx/blueprint/microservice/order/impl/OrderServiceImpl.java)。

我们的订单处理逻辑写在`RawOrderDispatcher`这个verticle中，下面我们就来看一下。

## 消费消息源发送来的数据

首先我们需要从消息源中根据服务名称获取消息消费者，然后从消费者处获取发送来的订单。这可以通过`MessageSource`接口的`getConsumer`方法实现：

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

获取到对应的`MessageConsumer`以后，我们就可以通过`handler`方法给其绑定一个`Handler<Message<T>>`类型的处理函数，在此处理函数中我们就可以对获取的消息进行各种操作。这里我们的message body是`JsonObject`类型的，所以我们首先将其转化为订单实体，然后就可以对其进行分发和处理了。对应的逻辑在`dispatchOrder`方法中。

## “处理”订单

我们来看一下`dispatchOrder`方法中的简单的“分发处理”逻辑:

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

首先我们先创建一个`Future`代表向数据库中添加订单的异步结果。然后我们调用订单服务的`createOrder`方法将订单存储至数据库中（1）。可以看到我们给此方法传递的处理函数是`orderCreateFuture.completer()`，这样当添加操作结束时，对应的`Future`就会被赋值。下一步我们组合一个异步方法 —— `applyInventoryChanges`方法，用于变更商品库存数量（2）。如果这两个过程都成功完成的话，我们就创建一个代表结算成功的`CheckoutResult`实体（3），然后调用`reply`方法向消息发送者回复结算结果（4）。之后我们向Event Bus发送结算事件来通知日志组件记录日志（5）。如果其中有过程失败的话，我们需要对消息发送者`sender`调用`fail`方法来通知操作失败（6）。

很简单吧？下面我们来看一下`applyInventoryChanges`方法的实现，看看如何变更商品库存数量：

```java
private Future<Void> applyInventoryChanges(Order order) {
  Future<Void> future = Future.future();
  // 从服务发现层获取REST端点
  Future<HttpClient> clientFuture = Future.future();
  HttpEndpoint.getClient(discovery,
    new JsonObject().put("name", "inventory-rest-api"), // 服务名称
    clientFuture.completer());
  // 通过调用REST API来变更对应的库存
  return clientFuture.compose(client -> {
    List<Future> futures = order.getProducts()
      .stream()
      .map(item -> { // 变换成对应的异步结果
        Future<Void> resultFuture = Future.future();
        String url = String.format("/%s/decrease?n=%d", item.getProductId(), item.getAmount());
        client.put(url, response -> {
          if (response.statusCode() == 200) {
            resultFuture.complete();
          } else {
            resultFuture.fail(response.statusMessage());
          }
        })
          .exceptionHandler(resultFuture::fail)
          .end();
        return resultFuture;
      })
      .collect(Collectors.toList());
    // 每个Future必须都success，生成的组合Future才会success
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

相信你一定不会对此方法的实现感到陌生，因为它和我们之前在购物车服务中讲的`getInventory`方法非常类似。我们首先获取库存组件对应的HTTP客户端，接着对订单中每个商品，根据其数额来调用REST API减少对应的库存。调用REST API获取结果的过程是异步的，因此这里我们又得到了一个`List<Future>`。但是这里我们并不需要每个`Future`的实际结果。我们只需要每个`Future`的状态，因此这里仅需调用`CompositeFuture.all`方法获取所有`Future`的组合`Future`。

至于组件中的`OrderVerticle`，它只做了三件微小的事情：发布订单服务、部署用于订单分发处理的`RawOrderDispatcher`以及部署REST Verticle。

# Micro Shop SPA整合

在我们的Micro Shop项目中，我们提供了一个用Angular.js写的简单的SPA前端页面。那么问题来了，如何将其整合至我们的微服务中？

> 注意：当前版本中，为了方便起见，我们将SPA部分整合进了`api-gateway`模块中。在生产环境下UI部分通常要单独部署。

有了Vert.x Web的魔力，我们只需要做的是配置一下路由，让其可以处理静态资源即可！只需要一行：

```java
router.route("/*").handler(StaticHandler.create());
```

默认情况下静态资源映射的目录是`webroot`目录，当然你也可以在创建`StaticHandler`的时候来配置映射目录。

# 监控仪表板与统计数据

监控仪表板(Monitor Dashboard)同样也是一个SPA前端应用。在本章节中我们会涉及到以下内容：

- 如何配置SockJS - EventBus bridge
- 如何在浏览器中接受来自Event Bus的信息
- 如何利用 **Vert.x Dropwizard Metrics** 来获取Vert.x组件的统计数据

## SockJS - Event Bus Bridge

很多时候我们想要在浏览器中接收来自Event Bus的消息并进行处理。听起来很神奇吧～而且你应该能够想象到，Vert.x支持这么做！Vert.x提供了 [SockJS - Event Bus Bridge](http://vertx.io/docs/vertx-sockjs-service-proxy/java/) 来支持服务的和客户端（通常是浏览器端）通过Event Bus进行通信。

为了开启SockJS - Event Bus Bridge支持，我们需要配置`SockJSHandler`以及对应的路由器：

```java
// event bus bridge
SockJSHandler sockJSHandler = SockJSHandler.create(vertx); // (1)
BridgeOptions options = new BridgeOptions()
  .addOutboundPermitted(new PermittedOptions().setAddress("microservice.monitor.metrics")) // (2)
  .addOutboundPermitted(new PermittedOptions().setAddress("events.log"));

sockJSHandler.bridge(options); // (3)
router.route("/eventbus/*").handler(sockJSHandler); // (4)
```

首先我们创建一个`SockJSHandler` (1)，它用于处理Event Bus信息。默认情况下，为了安全起见，Vert.x不允许任何消息通过Event Bus传输至浏览器端，因此我们需要对其进行配置。我们可以创建一个`BridgeOptions`然后设定允许单向传输消息的地址。这里有两种地址：**Outbound** 以及 **Inbound**。Outbound地址允许服务端向浏览器端通过Event Bus发送消息，而Inbound地址允许浏览器端向服务端通过Event Bus发送消息。这里我们只需要两个Outbound Address：`microservice.monitor.metrics`用作传输统计数据，`events.log`用作传输日志消息（2）。接着我们就可以将配置好的`BridgeOptions`设置给Bridge（3），最后配置对应的路由。浏览器端的SockJS客户端会使用`/eventbus/*`路由路径来进行通信。

## 将统计数据发送至Event Bus

在微服务架构中，监控(Monitoring)也是重要的一环。有了Vert.x的各种Metrics组件，如 **Vert.x Dropwizard Metrics** 或 **Vert.x Hawkular Metrics**，我们可以从对应的组件中获取到统计数据。

这里我们使用 **Vert.x Dropwizard Metrics**。使用方法很简单，首先创建一个`MetricsService`实例:

```java
MetricsService service = MetricsService.create(vertx);
```

接着我们就可以调用`getMetricsSnapshot`方法获取各种组件的统计数据。此方法接受一个实现了`Measured`接口的类。`Measured`接口定义了获取Metrics Data的一种规范，Vert.x中主要的类，如`Vertx`和`EventBus`都实现了此接口。因此传入不同的`Measured`实现就可以获取不同的数据。这里我们传入了`Vertx`实例来获取更多的统计数据。获取的统计数据的格式为`JsonObject`：

```java
// send metrics message to the event bus
vertx.setPeriodic(metricsInterval, t -> {
  JsonObject metrics = service.getMetricsSnapshot(vertx);
  vertx.eventBus().publish("microservice.monitor.metrics", metrics);
});
```

我们设定了一个定时器，每隔一段时间就向`microservice.monitor.metrics`地址发送当前的统计数据。

如果想了解统计数据都包含什么，请参考 [Vert.x Dropwizard metrics 官方文档](http://vertx.io/docs/vertx-dropwizard-metrics/java/#_the_metrics)。

现在是时候在浏览器端接收并展示统计数据以及日志消息了～

## 在浏览器端接收Event Bus上的消息

为了在浏览器端接收Event Bus上的消息，我们首先需要这两个库： `vertx3-eventbus-client`以及`sockjs`。你可以通过npm或bower来下载这两个库。然后我们就可以在代码中创建一个`EventBus`实例，然后注册处理函数：

```javascript
var eventbus = new EventBus('/eventbus');

eventbus.onopen = () => {
  eventbus.registerHandler('microservice.monitor.metrics', (err, message) => {
      $scope.metrics = message.body;
      $scope.$apply();
  });
}
```

我们可以通过`message.body`来获取对应的消息数据。

之后我们将会运行这个仪表板来监视整个微服务应用的状态。

# 展示时间！

哈哈，现在我们已经看完整个Micro Shop微服务的源码了～看源码看的也有些累了，现在到了展示时间了！这里我们使用Docker Compose来编排容器并运行我们的微服务应用，非常方便。

> 注意：至少预留 4GB 内存来运行此微服务应用。

## Docker Machine配置(macOS/Windows)

如果你是通过Docker Machine来运行我们的微服务应用的话，你需要进行一些配置。首先向 `hosts` 文件中添加一条记录：

```
192.168.99.100 dockernet
```

其中记录对应的IP地址为Docker Machine的IP地址，可以通过`docker-machine ip`命令获取。当然这个地方你也可以用其它的hostname。

接着你需要设定Docker external IP。编辑`api-gateway/src/config/docker.json`配置文件，将`api.gateway.http.address.external`属性设置为`dockernet`。

## 内核属性及内存配置

为了让ELK组件能够正常运作，我们需要更改一些内核属性。首先，`vm.max_map_count`需要大于 **262144**。在Linux下可以用以下命令修改：

```shell
sudo sysctl -w vm.max_map_count=262144
```

Docker Machine对应的命令:

```shell
docker-machine ssh
sudo sysctl net.ipv4.ip_forward
sudo sysctl -w vm.max_map_count=262144
```

另外，正常运行本微服务实例至少需要4GB内存，因此Docker Machine虚拟机对应的内存至少应该为 4096 MB，否则可能会出错。

## 构建项目以及容器

在我们构建整个项目之前，我们需要先通过 **bower** 获取`api-gateway`和`monitor-dashboard`这两个组件中前端代码对应的依赖。它们的`bower.json`文件都在对应的`src/main/resources/webroot`目录中。我们分别进入这两个目录并执行：

```
bower install
```

然后我们就可以构建整个项目了：

```
mvn clean install -Dmaven.test.skip=true
```

构建完项目以后，我们再来构建容器（需要root权限）：

```
cd docker
sudo ./build.sh
```

构建完成后，我们就可以来运行我们的微服务应用了：

```
sudo ./run.sh
```

数据库组件与中间件组件（如MySQL, Keycloak, ELK)会首先启动，因为它们启动的时间比较长。中间件组件初始化完毕以后，各个服务容器就会依次启动。当整个微服务初始化完成的时候，我们就可以在浏览器中浏览网店页面了，默认地址是 [https://localhost:8787](https://localhost:8787)。

## 第一次运行？

如果我们是第一次运行此微服务应用（或之前删除了所有的容器），我们必须手动配置**Keycloak**服务器。首先我们需要在hosts文件中添加一条记录：

```
0.0.0.0	keycloak-server
```

然后我们需要访问 [http://keycloak-server:8080](http://keycloak-server:8080)然后进入管理员登录页面。默认情况下用户名和密码都是 **admin**。进入管理台之后，我们需要创建一个 **Realm**，名字随意（实例中给的是`Vert.x`）。然后进入此Realm，并且为我们的应用创建一个**Client**，类似于这样：

![Keycloak configuration](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/keycloak-client-config.png)

创建完以后，我们进入 **Installation** 选项卡中来复制对应的JSON配置文件。我们需要将复制的内容覆盖掉`api-gateway/src/config/docker.json`中对应的配置。比如：

```json
{
  "api.gateway.http.port": 8787,
  "api.gateway.http.address": "localhost",
  "circuit-breaker": {
    "name": "api-gateway-cb",
    "timeout": 10000,
    "max-failures": 5
  },
  // 下面的都是Keycloak相关的配置
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

我们还需要创建几个用户(**User**)以便后面通过这些用户来登录。

更详细的Keycloak的配置过程及解释请参考Paulo的教程： [Vertx 3 and Keycloak tutorial](http://vertx.io/blog/vertx-3-and-keycloak-tutorial/)，非常详细。

修改完对应的配置文件之后，我们必须重新构建`api-gateway`模块的容器，然后重新启动此容器。

## 欢乐的购物时间！

完成配置之后，我们就来访问前端页面吧！

![SPA Frontend](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/shopping-spa-index.png)

现在我们可以访问 `https://localhost:8787/login` 进行登录，它会跳转至Keycloak的用户登录页面。如果登陆成功，它会自动跳转回Micro Shop的主页。现在我们可以尽情地享受购物时间了！这真是极好的！

我们也可以来访问Monitor Dashboard，默认地址是 [http://localhost:9100](http://localhost:9100)。

![Monitor Dashboard](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/monitor-dashboard.png)

一颗赛艇！

# 完结！

不错不错！我们终于到达了微服务旅途的终点！恭喜！我们非常希望你能够喜欢此蓝图教程，并且掌握到关于Vert.x和微服务的知识 :-)

以下是关于微服务和分布式系统的一些推荐阅读材料：

- [Microservices - a definition of this new architectural term](http://martinfowler.com/articles/microservices.html)
- [Event Sourcing](http://martinfowler.com/eaaDev/EventSourcing.html)
- [Cloud Design Patterns: Prescriptive Architecture Guidance for Cloud Applications](https://msdn.microsoft.com/en-us/library/dn568099.aspx)

享受微服务的狂欢吧！
