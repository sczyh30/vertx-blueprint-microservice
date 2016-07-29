package io.vertx.blueprint.microservice.user;

import io.vertx.blueprint.microservice.user.impl.JdbcUserServiceImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * A service interface managing users.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * @author Eric Zhao
 */
@VertxGen
@ProxyGen
public interface UserService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "user-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.user";

  /**
   * A static method that creates a user service.
   *
   * @param config a json object for configuration
   * @return initialized user service
   */
  static UserService createService(Vertx vertx, JsonObject config) {
    return new JdbcUserServiceImpl(vertx, config);
  }

  /**
   * Initialize the persistence.
   *
   * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates
   *                      whether the operation was successful or not.
   */
  @Fluent
  UserService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

  /**
   * Add a user to the persistence.
   *
   * @param user          a user entity that we want to add
   * @param resultHandler the result handler will be called as soon as the user has been added. The async result indicates
   *                      whether the operation was successful or not.
   */
  @Fluent
  UserService addUser(User user, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Retrieve the user with certain `id`.
   *
   * @param id            user id
   * @param resultHandler the result handler will be called as soon as the user has been retrieved. The async result indicates
   *                      whether the operation was successful or not.
   */
  @Fluent
  UserService retrieveUser(String id, Handler<AsyncResult<User>> resultHandler);

  /**
   * Retrieve all users.
   *
   * @param resultHandler the result handler will be called as soon as the users have been retrieved. The async result indicates
   *                      whether the operation was successful or not.
   */
  @Fluent
  UserService retrieveAllUsers(Handler<AsyncResult<List<User>>> resultHandler);

  /**
   * Delete a user from the persistence
   *
   * @param id            user id
   * @param resultHandler the result handler will be called as soon as the user has been removed. The async result indicates
   *                      whether the operation was successful or not.
   */
  @Fluent
  UserService deleteUser(String id, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Delete all users from the persistence
   *
   * @param resultHandler the result handler will be called as soon as the users have been removed. The async result indicates
   *                      whether the operation was successful or not.
   */
  @Fluent
  UserService deleteAllUsers(Handler<AsyncResult<Void>> resultHandler);

}
