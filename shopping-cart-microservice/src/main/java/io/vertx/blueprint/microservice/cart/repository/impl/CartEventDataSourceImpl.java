package io.vertx.blueprint.microservice.cart.repository.impl;

import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.repository.CartEventDataSource;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import rx.Observable;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CartEventDataSource}.
 */
public class CartEventDataSourceImpl implements CartEventDataSource {

  private final JDBCClient client;

  public CartEventDataSourceImpl(io.vertx.core.Vertx vertx, JsonObject json) {
    this.client = JDBCClient.createNonShared(Vertx.newInstance(vertx), json);
  }

  @Override
  public Observable<CartEvent> streamByUser(String userId) {
    return client.getConnectionObservable()
      .flatMap(conn -> conn.queryWithParamsObservable(STREAM_STATEMENT, new JsonArray().add(userId)))
      .map(ResultSet::getRows)
      .flatMap(Observable::from)
      .map(CartEvent::new);
  }

  @Override
  public Observable<Void> save(CartEvent cartEvent) {
    JsonArray params = new JsonArray().add(cartEvent.getCartEventType().ordinal())
      .add(cartEvent.getUserId())
      .add(cartEvent.getProductId())
      .add(cartEvent.getAmount())
      .add(cartEvent.getCreatedAt());
    return client.getConnectionObservable()
      .flatMap(conn -> conn.updateWithParamsObservable(SAVE_STATEMENT, params))
      .map(r -> null);
  }

  @Override
  public Observable<CartEvent> retrieveOne(Long id) {
    return client.getConnectionObservable()
      .flatMap(conn -> conn.queryWithParamsObservable(RETRIEVE_STATEMENT, new JsonArray().add(id)))
      .map(ResultSet::getRows)
      .filter(list -> !list.isEmpty())
      .map(res -> res.get(0))
      .map(CartEvent::new);
  }

  @Override
  public Observable<Void> delete(Long id) {
    // This service is an append-only service, so delete is not allowed
    return Observable.error(new RuntimeException("Delete is not allowed"));
  }

  // SQL Statement

  private static final String INIT_STATEMENT = "CREATE TABLE `cart_event` (\n" +
    "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
    "  `type` tinyint(2) NOT NULL,\n" +
    "  `user_id` varchar(45) NOT NULL,\n" +
    "  `product_id` varchar(45) NOT NULL,\n" +
    "  `amount` int(11) NOT NULL,\n" +
    "  `created_at` bigint(20) NOT NULL,\n" +
    "  PRIMARY KEY (`id`),\n" +
    "  KEY `INDEX_USER` (`user_id`) )";

  private static final String SAVE_STATEMENT = "INSERT INTO `cart_event` (`type`, `user_id`, `product_id`, `amount`, `created_at`) " +
    "VALUES (?, ?, ?, ?, ?)";

  private static final String RETRIEVE_STATEMENT = "SELECT * FROM `cart_event` WHERE id = ?";

  private static final String STREAM_STATEMENT = "";
}
