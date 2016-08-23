package io.vertx.blueprint.microservice.order.impl;

import io.vertx.blueprint.microservice.common.service.JdbcRepositoryWrapper;
import io.vertx.blueprint.microservice.order.Order;
import io.vertx.blueprint.microservice.order.OrderService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link OrderService}.
 */
public class OrderServiceImpl extends JdbcRepositoryWrapper implements OrderService {

  public OrderServiceImpl(Vertx vertx, JsonObject config) {
    super(vertx, config);
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
    retrieveMany(new JsonArray().add(accountId), RETRIEVE_BY_ACCOUNT_STATEMENT)
      .map(rawList -> rawList.stream()
        .map(Order::new)
        .collect(Collectors.toList())
      )
      .setHandler(resultHandler);
    return this;
  }

  @Override
  public OrderService createOrder(Order order, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray params = new JsonArray().add(order.getOrderId())
      .add(order.getPayId())
      .add(order.getBuyerId())
      .add(order.getCreateTime())
      .add(new JsonArray(order.getProducts()).encode())
      .add(order.getTotalPrice());
    executeNoResult(params, INSERT_STATEMENT, resultHandler);
    return this;
  }

  @Override
  public OrderService retrieveOrder(Long orderId, Handler<AsyncResult<Order>> resultHandler) {
    retrieveOne(orderId, RETRIEVE_BY_OID_STATEMENT)
      .map(option -> option.map(Order::new).orElse(null))
      .setHandler(resultHandler);
    return this;
  }

  // SQL statement

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
  private static final String INSERT_STATEMENT = "INSERT INTO `order` VALUES (?, ?, ?, ?, ?, ?)";
  private static final String RETRIEVE_BY_ACCOUNT_STATEMENT = "SELECT * FROM `order` WHERE buyerId = ?";
  private static final String RETRIEVE_BY_OID_STATEMENT = "SELECT * FROM `order` WHERE orderId = ?";
}
