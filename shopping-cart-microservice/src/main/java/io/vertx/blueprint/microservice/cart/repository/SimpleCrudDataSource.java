package io.vertx.blueprint.microservice.cart.repository;

import rx.Observable;

/**
 * Simple Rx-fied data source service interface for CRUD.
 */
public interface SimpleCrudDataSource<T, ID> {

  Observable<Void> save(T entity);

  Observable<T> retrieveOne(ID id);

  Observable<Void> delete(ID id);

}
