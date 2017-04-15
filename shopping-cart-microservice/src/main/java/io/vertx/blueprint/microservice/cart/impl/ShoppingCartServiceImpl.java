package io.vertx.blueprint.microservice.cart.impl;

import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.ShoppingCart;
import io.vertx.blueprint.microservice.cart.ShoppingCartService;
import io.vertx.blueprint.microservice.cart.repository.CartEventDataSource;
import io.vertx.blueprint.microservice.cart.repository.impl.CartEventDataSourceImpl;
import io.vertx.blueprint.microservice.common.functional.Functional;
import io.vertx.blueprint.microservice.product.Product;
import io.vertx.blueprint.microservice.product.ProductService;
import io.vertx.blueprint.microservice.product.ProductTuple;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ShoppingCartService}.
 *
 * @author Eric Zhao
 */
public class ShoppingCartServiceImpl implements ShoppingCartService {

  private final CartEventDataSource repository;
  private final ServiceDiscovery discovery;

  public ShoppingCartServiceImpl(Vertx vertx, ServiceDiscovery discovery, JsonObject config) {
    this.discovery = discovery;
    this.repository = new CartEventDataSourceImpl(vertx, config);
  }

  @Override
  public ShoppingCartService addCartEvent(CartEvent event, Handler<AsyncResult<Void>> resultHandler) {
    Future<Void> future = Future.future();
    repository.save(event).subscribe(future::complete, future::fail);
    future.setHandler(resultHandler);
    return this;
  }

  @Override
  public ShoppingCartService getShoppingCart(String userId, Handler<AsyncResult<ShoppingCart>> resultHandler) {
    aggregateCartEvents(userId)
      .setHandler(resultHandler);
    return this;
  }

  /**
   * Get the shopping cart for a certain user.
   *
   * @param userId user id
   * @return async result
   */
  private Future<ShoppingCart> aggregateCartEvents(String userId) {
    Future<ShoppingCart> future = Future.future();
    // aggregate cart events into raw shopping cart
    repository.streamByUser(userId)
      .takeWhile(cartEvent -> !CartEvent.isTerminal(cartEvent.getCartEventType()))
      .reduce(new ShoppingCart(), ShoppingCart::incorporate)
      .toSingle()
      .subscribe(future::complete, future::fail);

    return future.compose(cart ->
      getProductService()
        .compose(service -> prepareProduct(service, cart)) // prepare product data
        .compose(productList -> generateCurrentCartFromStream(cart, productList)) // prepare product items
    );
  }

  /**
   * Prepare meta product data stream for shopping cart.
   *
   * @param service product service instance
   * @param cart    raw shopping cart instance
   * @return async result
   */
  private Future<List<Product>> prepareProduct(ProductService service, ShoppingCart cart) {
    List<Future<Product>> futures = cart.getAmountMap().keySet()
      .stream()
      .map(productId -> {
        Future<Product> future = Future.future();
        service.retrieveProduct(productId, future.completer());
        return future;
      })
      .collect(Collectors.toList());
    return Functional.allOfFutures(futures);
  }

  /**
   * Generate current shopping cart from a data stream including necessary product data.
   * Note: this is not an asynchronous method. `Future` only marks whether the process is successful.
   *
   * @param rawCart       raw shopping cart
   * @param productList product data stream
   * @return async result
   */
  private Future<ShoppingCart> generateCurrentCartFromStream(ShoppingCart rawCart, List<Product> productList) {
    Future<ShoppingCart> future = Future.future();
    // check if any of the product is invalid
    if (productList.stream().anyMatch(Objects::isNull)) {
      future.fail("Error when retrieve products: empty");
      return future;
    }
    // construct the product items
    List<ProductTuple> currentItems = rawCart.getAmountMap().entrySet()
      .stream()
      .map(item -> new ProductTuple(getProductFromStream(productList, item.getKey()),
        item.getValue()))
      .filter(item -> item.getAmount() > 0)
      .collect(Collectors.toList());

    ShoppingCart cart = rawCart.setProductItems(currentItems);
    return Future.succeededFuture(cart);
  }

  /**
   * Get meta product data (seller and unit price) from a data stream of products.
   *
   * @param productList a data stream of products.
   * @param productId     product id
   * @return corresponding product
   */
  private Product getProductFromStream(List<Product> productList, String productId) {
    return productList.stream()
      .filter(product -> product.getProductId().equals(productId))
      .findFirst()
      .get();
  }

  /**
   * Get product service from the service discovery infrastructure.
   *
   * @return async result of the service.
   */
  private Future<ProductService> getProductService() {
    Future<ProductService> future = Future.future();
    EventBusService.getProxy(discovery, ProductService.class, future.completer());
    return future;
  }
}
