/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.blueprint.microservice.product.rxjava;

import java.util.Map;
import rx.Observable;
import java.util.List;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.blueprint.microservice.product.Product;

/**
 * A service interface managing products.
 * <p>
 * This service is an event bus service (aka. service proxy)
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.product.ProductService original} non RX-ified interface using Vert.x codegen.
 */

public class ProductService {

  final io.vertx.blueprint.microservice.product.ProductService delegate;

  public ProductService(io.vertx.blueprint.microservice.product.ProductService delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Initialize the persistence.
   * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

  /**
   * Initialize the persistence.
   * @return 
   */
  public Observable<Void> initializePersistenceObservable() { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    initializePersistence(resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Add a product to the persistence.
   * @param product a product entity that we want to add
   * @param resultHandler the result handler will be called as soon as the product has been added. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService addProduct(Product product, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.addProduct(product, resultHandler);
    return this;
  }

  /**
   * Add a product to the persistence.
   * @param product a product entity that we want to add
   * @return 
   */
  public Observable<Void> addProductObservable(Product product) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    addProduct(product, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve the product with certain `productId`.
   * @param productId product id
   * @param resultHandler the result handler will be called as soon as the product has been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler) { 
    delegate.retrieveProduct(productId, resultHandler);
    return this;
  }

  /**
   * Retrieve the product with certain `productId`.
   * @param productId product id
   * @return 
   */
  public Observable<Product> retrieveProductObservable(String productId) { 
    io.vertx.rx.java.ObservableFuture<Product> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveProduct(productId, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve the product price with certain `productId`.
   * @param productId product id
   * @param resultHandler the result handler will be called as soon as the product has been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService retrieveProductPrice(String productId, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.retrieveProductPrice(productId, resultHandler);
    return this;
  }

  /**
   * Retrieve the product price with certain `productId`.
   * @param productId product id
   * @return 
   */
  public Observable<JsonObject> retrieveProductPriceObservable(String productId) { 
    io.vertx.rx.java.ObservableFuture<JsonObject> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveProductPrice(productId, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve all products.
   * @param resultHandler the result handler will be called as soon as the products have been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService retrieveAllProducts(Handler<AsyncResult<List<Product>>> resultHandler) { 
    delegate.retrieveAllProducts(resultHandler);
    return this;
  }

  /**
   * Retrieve all products.
   * @return 
   */
  public Observable<List<Product>> retrieveAllProductsObservable() { 
    io.vertx.rx.java.ObservableFuture<List<Product>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveAllProducts(resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve products by page.
   * @param page 
   * @param resultHandler the result handler will be called as soon as the products have been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService retrieveProductsByPage(int page, Handler<AsyncResult<List<Product>>> resultHandler) { 
    delegate.retrieveProductsByPage(page, resultHandler);
    return this;
  }

  /**
   * Retrieve products by page.
   * @param page 
   * @return 
   */
  public Observable<List<Product>> retrieveProductsByPageObservable(int page) { 
    io.vertx.rx.java.ObservableFuture<List<Product>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveProductsByPage(page, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Delete a product from the persistence
   * @param productId product id
   * @param resultHandler the result handler will be called as soon as the product has been removed. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService deleteProduct(String productId, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteProduct(productId, resultHandler);
    return this;
  }

  /**
   * Delete a product from the persistence
   * @param productId product id
   * @return 
   */
  public Observable<Void> deleteProductObservable(String productId) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    deleteProduct(productId, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Delete all products from the persistence
   * @param resultHandler the result handler will be called as soon as the products have been removed. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ProductService deleteAllProducts(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteAllProducts(resultHandler);
    return this;
  }

  /**
   * Delete all products from the persistence
   * @return 
   */
  public Observable<Void> deleteAllProductsObservable() { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    deleteAllProducts(resultHandler.toHandler());
    return resultHandler;
  }


  public static ProductService newInstance(io.vertx.blueprint.microservice.product.ProductService arg) {
    return arg != null ? new ProductService(arg) : null;
  }
}
