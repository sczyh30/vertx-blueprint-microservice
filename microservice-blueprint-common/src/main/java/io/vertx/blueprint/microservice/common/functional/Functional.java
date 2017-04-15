package io.vertx.blueprint.microservice.common.functional;

import io.vertx.core.Future;
import io.vertx.core.impl.CompositeFutureImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Functional helper class.
 *
 * @author Eric Zhao
 */
public final class Functional {

  private Functional() {
  }

  /**
   * Evaluate a list of futures. Transforms a `List[Future[R]]` into a `Future[List[R]]`.
   * <p>
   * When all futures succeed, the result future completes with the list of each result of elements in {@code futures}.
   * </p>
   * The returned future fails as soon as one of the futures in {@code futures} fails.
   * When the list is empty, the returned future will be already completed.
   * <p>
   * Useful for reducing many futures into a single @{link Future}.
   *
   * @param futures a list of {@link Future futures}
   * @return the transformed future
   */
  public static <R> Future<List<R>> allOfFutures(List<Future<R>> futures) {
    return CompositeFutureImpl.all(futures.toArray(new Future[futures.size()]))
      .map(v -> futures.stream()
        .map(Future::result)
        .collect(Collectors.toList())
      );
  }
}
