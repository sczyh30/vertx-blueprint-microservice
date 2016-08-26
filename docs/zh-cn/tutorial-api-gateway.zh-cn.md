# API Gateway是什么？

我们先来考虑一下，对于我们的Micro Shop微服务应用，当我们在访问 **商品详情** 页面的时候，我们需要调用哪些服务？

![Product Detail Page - SPA](https://raw.githubusercontent.com/sczyh30/vertx-blueprint-microservice/master/docs/images/shopping-spa-product-detail.png)

在此页面中存在着各种各样的信息：

- 商品详情信息，包括名称、价格以及商品号
- 商品库存
- 用户信息
- 相关商品（推荐）

这些数据都来自不同的服务组件中，所以我们必须从不同的服务中获取各种数据。这样会导致一些问题：

- 每个服务的位置可能会动态地变更，前端请求也需要跟着变动
- 服务的协议可能与客户端的协议不兼容（比如Event Bus服务）
- 在后面我们可能需要重构服务，或者对服务进行聚合或拆分，造成许多变动，而这些变动对外是可见的

所以，直接调用对应的微服务组件显然不是一个好的方法。我们需要一种更为机智的方法 - [API Gateway](http://microservices.io/patterns/apigateway.html)。API Gateway是客户端访问微服务应用的大门。每个由客户端发出的请求都先达到API Gateway，然后经其处理后分发至对应的REST端点。听起来像是个简单的反向代理组件？不错，但它又不仅仅是一个反向代理组件。它也负责进行负载均衡，权限认证以及处理错误。并且如果服务的协议与HTTP协议不兼容的话，它还负责服务协议的对接与适配。API Gateway可以与服务发现组件结合，因此我们不必担心底层服务位置的变动。

API Gateway如此重要，因此如果引入它的话，必须保证其高可用性，这也需要仔细思考。并且，如果API Gateway设计的不合理的话，会成为系统性能的瓶颈。不过除了这些点之外，我们没有理由不用API Gateway。

# Circuit Breaker

[Circuit Breaker](http://martinfowler.com/bliki/CircuitBreaker.html)是一个非常有用的用于处理错误的模式。它可以描述为三种状态：**关闭**、**开启** 以及 **半开启** 状态。默认情况下断路器为 **关闭** 状态。我们可以在断路器中执行一些逻辑，每当执行失败时，断路器中的失败次数计数器会自动续一次。一旦失败次数达到设定的阈值，断路器会**开启**。此时任何在断路器内的调用都会快速失败。同时，断路器中的重置定时器也会开始计时，这段时间用于给下层服务恢复的时间。当计时时间到的时候，断路器会变为 **半开启** 模式。在半开启模式下，断路器允许一定次数的调用，但是非常脆弱，只要出现调用失败的情况，断路器马上置为 **开启** 状态并进入下一个等待轮回。而如果所有调用都成功的话，断路器会认为服务已经恢复正常，所以会重置失败次数并且置为 **关闭** 状态。

Vert.x提供了现成的Circuit Breaker实现 —— [Vert.x Circuit Breaker](http://vertx.io/docs/vertx-circuit-breaker/java/)。在我们的API Gateway中，我们会使用Vert.x Circuit Breaker来进行错误处理。

# 用Vert.x实现API Gateway

在本蓝图应用中，API Gateway作为一个单独的模块`api-gateway`出现。此模块中只有一个Verticle - `APIGatewayVerticle`。我们的API Gateway使用了`HTTPS - HTTP`模式进行通信，也就是说，API Gateway本身通过SSL加密，但是与其他组件的通信则是使用HTTP。

正如之前我们提到的那样，API Gateway同时还负责：

- 权限认证
- 错误处理
- 负载均衡
- 简单的心跳检测

## 总览

我们首先通过探索其中最重要的方法 - `start`方法来对`APIGatewayVerticle`做个总览：

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

额。。。我们的API Gateway肩负着如此多的功能！首先我们从`config()`中获取对应的`host`和`port` (1)。接着我们创建了一个`Router`实例 (2) 并且通过`enableLocalSession`方法开启Cookie和Session的支持 (3)。`enableLocalSession`方法位于`RestAPIVerticle`抽象类中，我们来看一下其实现：

```java
protected void enableLocalSession(Router router) {
  router.route().handler(CookieHandler.create());
  router.route().handler(SessionHandler.create(
    LocalSessionStore.create(vertx, "shopping.user.session")));
}
```
