package io.vertx.blueprint.microservice.cart.repository.impl;

import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.CartEventType;
import io.vertx.blueprint.microservice.cart.repository.CartEventDataSource;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import rx.Observable;
import rx.Single;

import java.util.Optional;


/**
 * Implementation of {@link CartEventDataSource}.
 *
 * @author Eric Zhao
 */
public class CartEventDataSourceImpl implements CartEventDataSource {

  private final JDBCClient client;

  public CartEventDataSourceImpl(io.vertx.core.Vertx vertx, JsonObject json) {
    this.client = JDBCClient.createNonShared(Vertx.newInstance(vertx), json);
    // TODO: Should not init the table here.
    client.rxGetConnection()
      .flatMap(connection ->
        connection.rxExecute(INIT_STATEMENT)
          .doAfterTerminate(connection::close)
      )
      .subscribe();
  }

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

  @Override
  public Single<Void> delete(Long id) {
    // This service is an append-only service, so delete is not allowed.
    return Single.error(new RuntimeException("Delete is not allowed"));
  }

  /**
   * Wrap raw cart event object from the event source.
   *
   * @param raw raw event object
   * @return wrapped cart event
   */
  private CartEvent wrapCartEvent(JsonObject raw) {
    return new CartEvent(raw)
      .setUserId(raw.getString("user_id"))
      .setProductId(raw.getString("product_id"))
      .setCreatedAt(raw.getLong("created_at"))
      .setCartEventType(CartEventType.valueOf(raw.getString("type")));
  }

  // SQL Statement

  private static final String INIT_STATEMENT = "CREATE TABLE IF NOT EXISTS `cart_event` (\n" +
    "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
    "  `type` VARCHAR(20) NOT NULL,\n" +
    "  `user_id` varchar(45) NOT NULL,\n" +
    "  `product_id` varchar(45) NOT NULL,\n" +
    "  `amount` int(11) NOT NULL,\n" +
    "  `created_at` bigint(20) NOT NULL,\n" +
    "  PRIMARY KEY (`id`),\n" +
    "  KEY `INDEX_USER` (`user_id`) )";

  private static final String SAVE_STATEMENT = "INSERT INTO `cart_event` (`type`, `user_id`, `product_id`, `amount`, `created_at`) " +
    "VALUES (?, ?, ?, ?, ?)";

  private static final String RETRIEVE_STATEMENT = "SELECT * FROM `cart_event` WHERE id = ?";

  private static final String STREAM_STATEMENT = "SELECT * FROM cart_event c\n" +
    "WHERE c.user_id = ? AND c.created_at > coalesce(\n" +
    "    (SELECT created_at FROM cart_event\n" +
    "\t WHERE user_id = ? AND (`type` = \"CHECKOUT\" OR `type` = \"CLEAR_CART\")\n" +
    "     ORDER BY cart_event.created_at DESC\n" +
    "     LIMIT 1\n" +
    "     ), 0)\n" +
    "ORDER BY c.created_at ASC;";
}
