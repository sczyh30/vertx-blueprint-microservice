package io.vertx.blueprint.microservice.account.api;

import io.vertx.blueprint.microservice.account.Account;
import io.vertx.blueprint.microservice.account.AccountService;
import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This verticle exposes a HTTP endpoint to process user data via REST API.
 *
 * @author Eric Zhao
 */
public class RestUserAccountAPIVerticle extends RestAPIVerticle {

  private static final String SERVICE_NAME = "user-account-rest-api";

  private final AccountService accountService;

  private static final String API_ADD = "/user";
  private static final String API_RETRIEVE = "/user/:id";
  private static final String API_RETRIEVE_ALL = "/user";
  private static final String API_UPDATE = "/user/:id";
  private static final String API_DELETE = "/user/:id";

  public RestUserAccountAPIVerticle(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.post(API_ADD).handler(this::apiAddUser);
    router.get(API_RETRIEVE).handler(this::apiRetrieveUser);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdateUser);
    router.delete(API_DELETE).handler(this::apiDeleteUser);

    String host = config().getString("user.account.http.address", "0.0.0.0");
    int port = config().getInteger("user.account.http.port", 8081);

    // create HTTP server and publish REST service
    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiAddUser(RoutingContext context) {
    Account account = new Account(context.getBodyAsJson());
    accountService.addAccount(account, resultVoidHandler(context, 201));
  }

  private void apiRetrieveUser(RoutingContext context) {
    String id = context.request().getParam("id");
    accountService.retrieveAccount(id, resultHandlerNonEmpty(context));
  }

  private void apiRetrieveAll(RoutingContext context) {
    accountService.retrieveAllAccounts(resultHandler(context, Json::encodePrettily));
  }

  private void apiUpdateUser(RoutingContext context) {
    notImplemented(context);
  }

  private void apiDeleteUser(RoutingContext context) {
    String id = context.request().getParam("id");
    accountService.deleteAccount(id, deleteResultHandler(context));
  }

}
