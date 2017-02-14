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
import rx.Single;
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

@io.vertx.lang.rxjava.RxGen(io.vertx.blueprint.microservice.product.ProductService.class)
public class ProductService {

  public static final io.vertx.lang.rxjava.TypeArg<ProductService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new ProductService((io.vertx.blueprint.microservice.product.ProductService) obj),
    ProductService::getDelegate
  );

  private final io.vertx.blueprint.microservice.product.ProductService delegate;
  
  public ProductService(io.vertx.blueprint.microservice.product.ProductService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.blueprint.microservice.product.ProductService getDelegate() {
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
  public Single<Void> rxInitializePersistence() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      initializePersistence(fut);
    }));
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
  public Single<Void> rxAddProduct(Product product) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addProduct(product, fut);
    }));
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
  public Single<Product> rxRetrieveProduct(String productId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveProduct(productId, fut);
    }));
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
  public Single<JsonObject> rxRetrieveProductPrice(String productId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveProductPrice(productId, fut);
    }));
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
  public Single<List<Product>> rxRetrieveAllProducts() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveAllProducts(fut);
    }));
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
  public Single<List<Product>> rxRetrieveProductsByPage(int page) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveProductsByPage(page, fut);
    }));
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
  public Single<Void> rxDeleteProduct(String productId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      deleteProduct(productId, fut);
    }));
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
  public Single<Void> rxDeleteAllProducts() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      deleteAllProducts(fut);
    }));
  }


  public static ProductService newInstance(io.vertx.blueprint.microservice.product.ProductService arg) {
    return arg != null ? new ProductService(arg) : null;
  }
}
