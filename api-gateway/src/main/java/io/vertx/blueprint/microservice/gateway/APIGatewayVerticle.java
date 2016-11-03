package io.vertx.blueprint.microservice.gateway;

import io.vertx.blueprint.microservice.account.Account;
import io.vertx.blueprint.microservice.account.AccountService;
import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.common.functional.Functional;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A verticle for global API gateway.
 * This API gateway uses HTTP-HTTP pattern. It's also responsible for
 * load balance and failure handling.
 *
 * @author Eric Zhao
 */
public class APIGatewayVerticle extends RestAPIVerticle {

  private static final int DEFAULT_CHECK_PERIOD = 60000;
  private static final int DEFAULT_PORT = 8787;

  private static final Logger logger = LoggerFactory.getLogger(APIGatewayVerticle.class);

  private OAuth2Auth oauth2;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("api.gateway.http.address", "localhost");
    int port = config().getInteger("api.gateway.http.port", DEFAULT_PORT);

    Router router = Router.router(vertx);
    // cookie and session handler
    enableLocalSession(router);

    // body handler
    router.route().handler(BodyHandler.create());

    // version handler
    router.get("/api/v").handler(this::apiVersion);

    // create OAuth 2 instance for Keycloak
    oauth2 = OAuth2Auth.createKeycloak(vertx, OAuth2FlowType.AUTH_CODE, config());

    router.route().handler(UserSessionHandler.create(oauth2));

    String hostURI = buildHostURI();

    // set auth callback handler
    router.route("/callback").handler(context -> authCallback(oauth2, hostURI, context));

    router.get("/uaa").handler(this::authUaaHandler);
    router.get("/login").handler(this::loginEntryHandler);
    router.post("/logout").handler(this::logoutHandler);

    // api dispatcher
    router.route("/api/*").handler(this::dispatchRequests);

    // init heart beat check
    initHealthCheck();

    // static content
    router.route("/*").handler(StaticHandler.create());

    // enable HTTPS
    HttpServerOptions httpServerOptions = new HttpServerOptions()
      .setSsl(true)
      .setKeyStoreOptions(new JksOptions().setPath("server.jks").setPassword("123456"));

    // create http server
    vertx.createHttpServer(httpServerOptions)
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

  private void dispatchRequests(RoutingContext context) {
    int initialOffset = 5; // length of `/api/`
    // run with circuit breaker in order to deal with failure
    circuitBreaker.execute(future -> {
      getAllEndpoints().setHandler(ar -> {
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
            .filter(record -> record.getMetadata().getString("api.name").equals(prefix))
            .findAny(); // simple load balance

          if (client.isPresent()) {
            doDispatch(context, newPath, discovery.getReference(client.get()).get(), future);
          } else {
            notFound(context);
            future.complete();
          }
        } else {
          future.fail(ar.cause());
        }
      });
    }).setHandler(ar -> {
      if (ar.failed()) {
        badGateway(ar.cause(), context);
      }
    });
  }

  /**
   * Dispatch the request to the downstream REST layers.
   *
   * @param context routing context instance
   * @param path    relative path
   * @param client  relevant HTTP client
   */
  private void doDispatch(RoutingContext context, String path, HttpClient client, Future<Object> cbFuture) {
    HttpClientRequest toReq = client
      .request(context.request().method(), path, response -> {
        response.bodyHandler(body -> {
          if (response.statusCode() >= 500) { // api endpoint server error, circuit breaker should fail
            cbFuture.fail(response.statusCode() + ": " + body.toString());
          } else {
            HttpServerResponse toRsp = context.response()
              .setStatusCode(response.statusCode());
            response.headers().forEach(header -> {
              toRsp.putHeader(header.getKey(), header.getValue());
            });
            // send response
            toRsp.end(body);
            cbFuture.complete();
          }
        });
      });
    // set headers
    context.request().headers().forEach(header -> {
      toReq.putHeader(header.getKey(), header.getValue());
    });
    if (context.user() != null) {
      toReq.putHeader("user-principal", context.user().principal().encode());
    }
    // send request
    if (context.getBody() == null) {
      toReq.end();
    } else {
      toReq.end(context.getBody());
    }
  }

  private void apiVersion(RoutingContext context) {
    context.response()
      .end(new JsonObject().put("version", "v1").encodePrettily());
  }

  /**
   * Get all REST endpoints from the service discovery infrastructure.
   *
   * @return async result
   */
  private Future<List<Record>> getAllEndpoints() {
    Future<List<Record>> future = Future.future();
    discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE),
      future.completer());
    return future;
  }

  // heart beat check (very simple)

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

  /**
   * Send heart-beat check request to every REST node in every interval and await response.
   *
   * @return async result. If all nodes are active, the result will be assigned `true`, else the result will fail
   */
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

  private boolean healthStatus(int code) {
    return code == 200;
  }

  // log methods

  private void publishGatewayLog(String info) {
    JsonObject message = new JsonObject()
      .put("info", info)
      .put("time", System.currentTimeMillis());
    publishLogEvent("gateway", message);
  }

  private void publishGatewayLog(JsonObject msg) {
    JsonObject message = msg.copy()
      .put("time", System.currentTimeMillis());
    publishLogEvent("gateway", message);
  }

  // auth

  private void authCallback(OAuth2Auth oauth2, String hostURL, RoutingContext context) {
    final String code = context.request().getParam("code");
    // code is a require value
    if (code == null) {
      context.fail(400);
      return;
    }
    final String redirectTo = context.request().getParam("redirect_uri");
    final String redirectURI = hostURL + context.currentRoute().getPath() + "?redirect_uri=" + redirectTo;
    oauth2.getToken(new JsonObject().put("code", code).put("redirect_uri", redirectURI), ar -> {
      if (ar.failed()) {
        logger.warn("Auth fail");
        context.fail(ar.cause());
      } else {
        logger.info("Auth success");
        context.setUser(ar.result());
        context.response()
          .putHeader("Location", redirectTo)
          .setStatusCode(302)
          .end();
      }
    });
  }

  private void authUaaHandler(RoutingContext context) {
    if (context.user() != null) {
      JsonObject principal = context.user().principal();
      String username = principal.getString("username");
      // String username = KeycloakHelper.preferredUsername(principal);
      if (username == null) {
        context.response()
          .putHeader("content-type", "application/json")
          .end(new Account().setId("TEST666").setUsername("Eric").toString()); // TODO: no username should be an error
      } else {
        Future<AccountService> future = Future.future();
        EventBusService.getProxy(discovery,
          new JsonObject().put("name", AccountService.SERVICE_NAME),
          future.completer()
        );
        future.compose(accountService -> {
          Future<Account> accountFuture = Future.future();
          accountService.retrieveByUsername(username, accountFuture.completer());
          return accountFuture;
        })
          .setHandler(resultHandlerNonEmpty(context)); // if user does not exist, should return 404
      }
    } else {
      context.fail(401);
    }
  }

  private void loginEntryHandler(RoutingContext context) {
    context.response()
      .putHeader("Location", generateAuthRedirectURI(buildHostURI()))
      .setStatusCode(302)
      .end();
  }

  private void logoutHandler(RoutingContext context) {
    context.clearUser();
    context.session().destroy();
    context.response().setStatusCode(204).end();
  }

  private String generateAuthRedirectURI(String from) {
    return oauth2.authorizeURL(new JsonObject()
      .put("redirect_uri", from + "/callback?redirect_uri=" + from)
      .put("scope", "")
      .put("state", ""));
  }

  private String buildHostURI() {
    int port = config().getInteger("api.gateway.http.port", DEFAULT_PORT);
    final String host = config().getString("api.gateway.http.address.external", "localhost");
    return String.format("https://%s:%d", host, port);
  }

}
