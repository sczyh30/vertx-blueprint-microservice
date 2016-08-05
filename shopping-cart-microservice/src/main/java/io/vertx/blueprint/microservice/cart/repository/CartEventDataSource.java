package io.vertx.blueprint.microservice.cart.repository;

import io.vertx.blueprint.microservice.cart.CartEvent;
import rx.Observable;

/**
 * Data source interface for processing {@link CartEvent}. Append-only operations.
 */
public interface CartEventDataSource extends SimpleCrudDataSource<CartEvent, Long> {

  Observable<CartEvent> streamByUser(String userId);

}
