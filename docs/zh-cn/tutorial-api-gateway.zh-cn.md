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

[Circuit Breaker](http://martinfowler.com/bliki/CircuitBreaker.html)（熔断器）是一个非常有用的容错机制。它可以描述为三种状态：**关闭**、**开启** 以及 **半开启** 状态。默认情况下断路器为 **关闭** 状态。我们可以在断路器中执行一些逻辑，每当执行失败时，断路器中的失败次数计数器会自动续一次。一旦失败次数达到设定的阈值，断路器会**开启**。此时任何在断路器内的调用都会快速失败。同时，断路器中的重置定时器也会开始计时，这段时间用于给下层服务恢复的时间。当计时时间到的时候，断路器会变为 **半开启** 模式。在半开启模式下，断路器允许一定次数的调用，但是非常脆弱，只要出现调用失败的情况，断路器马上置为 **开启** 状态并进入下一个等待轮回。而如果所有调用都成功的话，断路器会认为服务已经恢复正常，所以会重置失败次数并且置为 **关闭** 状态。

Vert.x提供了现成的Circuit Breaker实现 —— [Vert.x Circuit Breaker](http://vertx.io/docs/vertx-circuit-breaker/java/)。在我们的API Gateway中，我们会使用Vert.x Circuit Breaker来进行错误处理。

# 用Vert.x实现API Gateway

在本蓝图应用中，API Gateway作为一个单独的模块`api-gateway`出现。此模块中只有一个Verticle - `APIGatewayVerticle`。我们的API Gateway使用了`HTTPS - HTTP`模式进行通信，也就是说，API Gateway本身通过SSL加密，但是与其他组件的通信则是使用HTTP。

正如之前我们提到的那样，API Gateway 同时还负责：

- 权限认证
- 错误处理
- 负载均衡
- 协议适配

> 注： 在Chris Richardson的 [Building Microservices: Using an API Gateway](https://www.nginx.com/blog/building-microservices-using-an-api-gateway) 文章中提到，API Gateway还负责服务的编排(orchestration)，即将几个REST API的结果组合成一个。这种观点的争议比较大，我们认为如果加入了服务编排，API Gateway就过于庞大，职责太多，不利于解耦，因此在本文API Gateway实现中我们并没有加入服务编排的内容。

> 注：通常情况下，心跳检测应由服务治理模块负责。

## 总览

我们首先通过探索其中最重要的方法 - `start` 方法来对 `APIGatewayVerticle` 做个总览：

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

首先我们给路由器绑定了一个`CookieHandler`以开启Cookie支持，因为Session依赖Cookie。然后我们就可以给路由器绑定`SessionHandler`来开启Session支持了。创建`SessionHandler`需要提供一个`SessionStore`，它代表某种Session存储方式。在Vert.x中有两种`SessionStore`：`LocalSessionStore`以及`ClusteredSessionStore`。前者将Session存储至local map中，而后者将Session存储至集群的distributed map中，所以集群中的其它结点也可以访问它。

之后我们给路由器绑定`BodyHandler`以开启request body处理支持（4）。

之前我们说过，我们的API Gateway也负责权限管理，因此这里我们通过`createKeycloak`方法创建了一个`OAuth2Auth`实例（6），通过它来请求Keycloak。下面我们给路由器绑定一个`UserSessionHandler`处理器（7），从字面意思就可以看出，它的作用是自动将路由context内的`user`存储至Session中。OAuth 2还要求我们提供一个 **callback** 路径，所以我们还要创建一个callback route（8），在后面的章节中我们还要详细讲述其实现。然后我们创建了一系列的路由用于权限操作：`authUaaHandler`用于获取当前已登录用户，`loginEntryHandler`用于重定向至Keycloak的登录页面，`logoutHandler`用于注销当前用户（9）。当然，Vert.x Web提供了一个现成的`AuthHandler`用于权限验证，但是使用场景有限，因此为了与SPA整合并且细粒度地控制权限，这里我们就不用原生的`AuthHandler`了。

然后我们创建了用于反向代理的路由（10）然后调用`initHealthCheck`函数开启心跳检测支持（11）。同时，我们的SPA前端应用也被整合至API Gateway中了，因此我们还需要给路由设置`StaticHandler`来处理静态资源（12）。静态资源路由的优先级通常需要很低因此我们把它放到后面。

为了讲解如何在Vert.x中使用HTTPS，我们给API Gateway添加了SSL支持。我们创建了服务器配置`HttpServerOptions`，并且对其做相应的配置使其支持HTTPS（13）。最后我们将配置好的`HttpServerOptions`传递给`createHttpServer`来创建服务端（14）。如果服务端创建成功，我们就将API Gateway发布至服务发现层，将`future`标记为完成状态，然后发布日志。如果服务端创建失败，我们就将`future`标记为失败状态。

好了，接下来我们来看看API Gateway的各个功能是如何设计和实现的～

## 基于熔断器的容错机制

我们的失败处理（容错机制）是基于Circuit Breaker的。考虑到我们使用 `HTTP-HTTP` 模式，如果请求内部的API端点得到的状态为服务器错误（如 **500 Internal Error**），我们就可以认为这次请求是失败的。我们在Circuit Breaker中执行分发请求的逻辑。一旦请求出现了错误，或者请求超时，Circuit Breaker中的失败次数就会增加。一旦次数达到设定的阈值，断路器就会开启，此时断路器不会处理任何的请求 —— 直接返回 **502 Bad Gateway** 状态。下面的过程可以参考上面Circuit Breaker的状态变换过程。

我们可以用以下的伪代码来简单地描述上述过程：

```java
circuitBreaker.execute(future -> {
  client.request(method, path, response -> {
    if (response.statusCode() >= 500) {
      future.fail("Some error");
    } else {
      // 写入response
    }
  });
}).setHandler(ar -> {
      if (ar.failed()) {
        badGateway(ar.cause(), context);
      }
    });
```

在下面的反向代理部分我们讲详细讲述。

## 反向代理 - 分发请求

反向代理是API Gateway的关键部分之一。所有来自客户端的请求都首先经过API Gateway，然后经API Gateway分发至对应的REST端点。这里请求的分发取决于路由的模式，因此这里我们有以下约定：

- 所有需要分发的请求对应的路径都以`api`开头，因此我们的分发处理函数会识别路径模式为`/api/*`的请求
- `api`后面的path variable代表对应的API名称。比如，`/api/product/*`请求对应的API名称为`product`
- 每个发布至服务发现层的HTTP端点都需要有一个`api.name`代表API名称，它存储于服务记录的元数据内。此属性需要从JSON配置文件中读取。我们来看一下`BaseMicroserviceVerticle`类中`publishHttpEndpoint`的实现：


```java
protected Future<Void> publishHttpEndpoint(String name, String host, int port) {
  Record record = HttpEndpoint.createRecord(name, host, port, "/",
    new JsonObject().put("api.name", config().getString("api.name", ""))
  );
  return publish(record);
}
```

我们从外部的`config()`中读取`api.name`，然后将它添加至元数据(metadata)中，因此之后我们可以通过对应的`api.name`来获取服务记录。

配置文件需要包含以下信息，类似于：

```json
{
  "api.name": "product"
}
```

好了，现在我们已经对API Gateway中的路由分发约定有所了解了。现在我们来看看分发逻辑的实现 —— `dispatchRequests`方法：

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
        future.fail(ar.cause());
      }
    });
  }).setHandler(ar -> {
    if (ar.failed()) {
      badGateway(ar.cause(), context); // (7)
    }
  });
}
```

整个分发逻辑都是在Circuit Breaker中执行的以便处理错误（1）。`CircuitBreaker`的`execute`方法接受一个`Handler<Future<T>>`类型的处理函数。在此处理函数中，我们来执行我们的分发逻辑，并根据结果来给`future`赋值。

在分发逻辑中，我们首先从服务发现层中获取所有的REST端点（2），这个过程非常简单。只需要在通过`getRecords`方法获取服务记录时指定其类型为`HttpEndpoint.TYPE`即可：

```java
private Future<List<Record>> getAllEndpoints() {
  Future<List<Record>> future = Future.future();
  discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE),
    future.completer());
  return future;
}
```

之后我们来解析路由path获取对应的API名称`prefix`，然后生成请求REST端点的相对路径。之后我们需要通过`filter`算子过滤出与API名称相符的服务记录（3），然后通过`discovery.getReference(record).get()`方法将服务记录变换成对应的`HttpClient`实例（4）。下面我们就可以通过`findAny`算子获取流中任意的REST端点服务记录（4）。`findAny`的使用可以算是一个简单的负载均衡的实现，当然我们也可以实现自己的负载均衡算子来操作流并获取其中的服务记录。注意流中可能不存在对应的服务记录，因此这里调用`findAny`会返回一个`Optional<Record>`类型的结果。之后我们需要检查端点是否存在，如果存在，我们就通过通过`discovery.getReference(record).get()`方法将服务记录变换成对应的`HttpClient`实例，接着调用`doDispatch`方法向对应REST端点转发请求并等待回应（5）。如果不存在任何的客户端对应请求的API，这代表对应的API端点不存在，所以直接返回 **404** 状态并且将`future`置为完成状态（6）。

`execute`方法的结果即为其接受的lambda中的`future`（代表执行结果），所以我们直接给其设定一个错误处理函数，当对应的`future`失败时，我们就调用封装好的`badGateway`方法来返回 **Bad Gateway** 错误（7）并且记录日志。


现在我们来看看请求是怎么被转发的 —— `doDispatch`方法：

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

`doDispatch`方法中接受的`client`即为前面我们选出的REST端点，`context`为API Gateway内对应的路由上下文。我们通过`client.request(method, path, handler)`方法来发送HTTP请求，这里需要指定HTTP Method以及请求相对路径（1）。为了对请求进行“完美转发”，原先请求的Header也需要保持（2）。并且这里如果用户已登录的话，我们还需要添加一个特殊的HTTP头`user-principal`来传递对应的用户凭证数据，在下面的章节我们将着重讲述。注意只有`end`的方法被调用的时候，请求才会被发出，因此我们需要调用`end`方法发送请求（3）。注意如果原先的请求body不为空的话，我们也需要对其进行原样转发。

下面我们就可以在`request`方法的回应处理器(response handler)中获取请求回应。我们可以通过`response.bodyHandler`方法来获取response body。之前提到过，如果回应的状态对应服务器错误(5xx)，那么我们就认为请求失败，所以Circuit Breaker中的`future`也要标记为失败状态（4）。如果回应状态正常，那么我们就从`context`创建一个server response，设定状态以及HTTP Headers（5），然后调用`end`方法将结果回应至用户端（6）。最后不要忘记将`future`标记为完成状态。

> 注意：不要忘记释放HTTP Client的资源！我们可以利用 `ServiceDiscovery.releaseServiceObject(discovery, object)` 函数来释放对应的资源。

这样，一个带有错误处理功能的反向代理模块就完成了！当然这个反向代理实现的很简单，大家可以自行扩展让它支持任意的模式，并且优化一下它的性能。当然我们也可以直接用Nginx做负载均衡和反向代理。

下面我们来看一下如何进行权限管理。

## 权限管理

请见英文版本：[Authentication management](http://sczyh30.github.io/vertx-blueprint-microservice/api-gateway.html#authentication-management)。

> 注：此部分在日后会有较大变动。
