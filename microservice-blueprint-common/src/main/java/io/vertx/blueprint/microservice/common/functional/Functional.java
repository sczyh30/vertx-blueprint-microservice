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

  /**
   * Transforms a `List[Future[R]]` into a `Future[List[R]]`.
   * Useful for reducing many futures into a single @{link Future}.
   *
   * @param futures a list of {@link Future futures}
   * @return the transformed future
   */
  public static <R> Future<List<R>> sequenceFuture(List<Future<R>> futures) {
    return CompositeFutureImpl.all(futures.toArray(new Future[futures.size()]))
      .map(v ->
        futures.stream()
          .map(Future::result)
          .collect(Collectors.toList())
      );
  }

}
