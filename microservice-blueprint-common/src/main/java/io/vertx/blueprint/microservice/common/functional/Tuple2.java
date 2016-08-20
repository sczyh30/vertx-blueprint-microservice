package io.vertx.blueprint.microservice.common.functional;


/**
 * A tuple of 2 elements.
 */
public final class Tuple2<T1, T2> {

  public final T1 _1;
  public final T2 _2;

  public Tuple2(T1 _1, T2 _2) {
    this._1 = _1;
    this._2 = _2;
  }

  /**
   * Swaps the elements of this `Tuple`.
   *
   * @return a new Tuple where the first element is the second element of this Tuple and the
   * second element is the first element of this Tuple.
   */
  public Tuple2<T2, T1> swap() {
    return new Tuple2<>(_2, _1);
  }

  @Override
  public String toString() {
    return "(" + _1 + "," + _2 + ")";
  }
}
