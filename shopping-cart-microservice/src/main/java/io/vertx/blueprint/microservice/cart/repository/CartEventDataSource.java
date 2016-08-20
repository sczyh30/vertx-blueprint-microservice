package io.vertx.blueprint.microservice.cart.repository;

import io.vertx.blueprint.microservice.cart.CartEvent;
import rx.Observable;

/**
 * Data source interface for processing {@link CartEvent}. Append-only operations.
 *
 * @author Eric Zhao
 */
public interface CartEventDataSource extends SimpleCrudDataSource<CartEvent, Long> {

  /**
   * Fetch cart event stream from the event source.
   *
   * @param userId user id
   * @return async stream of the cart events
   */
  Observable<CartEvent> streamByUser(String userId);

}
