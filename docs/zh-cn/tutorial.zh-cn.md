# 前言

欢迎回到Vert.x 蓝图系列！当今，**微服务架构** 变得越来越流行，开发者们都想尝试一下微服务应用的开发和架构设计。令人激动的是，Vert.x给我们提供了一系列用于微服务开发的组件，包括 **Service Discovery** (服务发现)、**Circuit Breaker** (断路器)以及其它的一些组件。有了Vert.x微服务组件的帮助，我们就可以快速利用Vert.x搭建我们的微服务应用。在这篇蓝图教程中，我们一起来探索一个利用Vert.x的各个组件开发的 Micro-Shop 微服务应用～

通过本教程，你将会学习到以下内容：

- 微服务架构
- 如何利用Vert.x来开发微服务应用
- 异步开发模式
- 响应式、函数式编程
- 事件溯源 (Event Sourcing)
- 通过分布式 Event Bus 进行异步RPC调用
- 各种各样的服务类型（例如REST、数据源、Event Bus服务等）
- 如何使用服务发现模块 (Vert.x Service Discovery)
- 如何使用断路器模块 (Vert.x Circuit Breaker)
- 如何利用Vert.x实现API Gateway
- 如何进行权限认证 (OAuth 2 + Keycloak)
- 如何配置及使用 SockJS - Event Bus Bridge

以及其它的一些东西。。。

本教程是 **Vert.x 蓝图系列** 的第三篇教程，对应的Vert.x版本为 **3.3.2** 。本教程中的完整代码已托管至[GitHub](https://github.com/sczyh30/vertx-blueprint-microservice)。

# 踏入微服务之门

哈～你一定对“微服务”这个词很熟悉——至少听起来很熟悉～越来越多的开发者开始拥抱微服务架构，那么微服务究竟是什么呢？一句话总结一下：

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

  git clone https://github.com/sczyh30/vertx-blueprint-microservice.git

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
