package io.vertx.blueprint.microservice.order.impl;

import io.vertx.blueprint.microservice.order.Order;
import io.vertx.blueprint.microservice.order.OrderService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link OrderService}.
 */
public class OrderServiceImpl implements OrderService {

  private final JDBCClient client;

  public OrderServiceImpl(Vertx vertx, JsonObject config) {
    this.client = JDBCClient.createNonShared(vertx, config);
  }

  @Override
  public OrderService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> {
      connection.execute(CREATE_STATEMENT, r -> {
        resultHandler.handle(r);
        connection.close();
      });
    }));
    return this;
  }

  @Override
  public OrderService retrieveOrdersForAccount(String accountId, Handler<AsyncResult<List<Order>>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> {
      connection.queryWithParams("SELECT * FROM `order` WHERE buyerId = ?",
        new JsonArray().add(accountId),
        ar -> {
          if (ar.succeeded()) {
            List<Order> resList = ar.result().getRows().stream()
              .map(Order::new)
              .collect(Collectors.toList());
            resultHandler.handle(Future.succeededFuture(resList));
          } else {
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
          connection.close();
        });
    }));
    return this;
  }

  @Override
  public OrderService createOrder(Order order, Handler<AsyncResult<Void>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> {
      JsonArray params = new JsonArray().add(order.getOrderId())
        .add(order.getPayId())
        .add(order.getBuyerId())
        .add(order.getCreateTime())
        .add(new JsonArray(order.getProducts()).encode())
        .add(order.getTotalPrice());
      connection.updateWithParams("INSERT INTO `order` VALUES (?, ?, ?, ?, ?, ?)",
        params, ar -> {
          if (ar.succeeded()) {
            resultHandler.handle(Future.succeededFuture());
          } else {
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
          connection.close();
        });
    }));
    return this;
  }

  @Override
  public OrderService retrieveOrder(Long orderId, Handler<AsyncResult<Order>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> {
      connection.queryWithParams("SELECT * FROM `order` WHERE orderId = ?",
        new JsonArray().add(orderId),
        ar -> {
          if (ar.succeeded()) {
            List<JsonObject> resList = ar.result().getRows();
            if (resList == null || resList.isEmpty()) {
              resultHandler.handle(Future.succeededFuture());
            } else {
              resultHandler.handle(Future.succeededFuture(new Order(resList.get(0))));
            }
          } else {
            resultHandler.handle(Future.failedFuture(ar.cause()));
          }
          connection.close();
        });
    }));
    return this;
  }

  /**
   * A helper methods that generates async handler for SQLConnection
   *
   * @return generated handler
   */
  private <T> Handler<AsyncResult<SQLConnection>> connHandler(Handler<AsyncResult<T>> h1, Handler<SQLConnection> h2) {
    return conn -> {
      if (conn.succeeded()) {
        final SQLConnection connection = conn.result();
        h2.handle(connection);
      } else {
        h1.handle(Future.failedFuture(conn.cause()));
      }
    };
  }

  private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `order` (\n" +
    "  `orderId` bigint(20) NOT NULL,\n" +
    "  `payId` varchar(24) NOT NULL,\n" +
    "  `buyerId` varchar(20) NOT NULL,\n" +
    "  `createTime` bigint(20) NOT NULL,\n" +
    "  `products` varchar(512) NOT NULL,\n" +
    "  `totalPrice` double NOT NULL,\n" +
    "  PRIMARY KEY (`orderId`),\n" +
    "  KEY `INDEX_BUYER` (`buyerId`),\n" +
    "  KEY `INDEX_PAY` (`payId`) )";
}
