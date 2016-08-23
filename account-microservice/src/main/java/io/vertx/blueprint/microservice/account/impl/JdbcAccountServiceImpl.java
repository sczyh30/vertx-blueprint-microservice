package io.vertx.blueprint.microservice.account.impl;

import io.vertx.blueprint.microservice.account.Account;
import io.vertx.blueprint.microservice.account.AccountService;
import io.vertx.blueprint.microservice.common.service.JdbcRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JDBC implementation of {@link AccountService}.
 *
 * @author Eric Zhao
 */
public class JdbcAccountServiceImpl extends JdbcRepositoryWrapper implements AccountService {

  public JdbcAccountServiceImpl(Vertx vertx, JsonObject config) {
    super(vertx, config);
  }

  @Override
  public AccountService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
    client.getConnection(connHandler(resultHandler, connection -> {
      connection.execute(CREATE_STATEMENT, r -> {
        resultHandler.handle(r);
        connection.close();
      });
    }));
    return this;
  }

  @Override
  public AccountService addAccount(Account account, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray params = new JsonArray().add(account.getId())
      .add(account.getUsername())
      .add(account.getPhone())
      .add(account.getEmail())
      .add(account.getBirthDate());
    this.executeNoResult(params, INSERT_STATEMENT, resultHandler);
    return this;
  }

  @Override
  public AccountService retrieveAccount(String id, Handler<AsyncResult<Account>> resultHandler) {
    this.retrieveOne(id, FETCH_STATEMENT)
      .map(option -> option.map(Account::new).orElse(null))
      .setHandler(resultHandler);
    return this;
  }

  @Override
  public AccountService retrieveByUsername(String username, Handler<AsyncResult<Account>> resultHandler) {
    this.retrieveOne(username, FETCH_BY_USERNAME_STATEMENT)
      .map(option -> option.map(Account::new).orElse(null))
      .setHandler(resultHandler);
    return this;
  }

  @Override
  public AccountService retrieveAllAccounts(Handler<AsyncResult<List<Account>>> resultHandler) {
    this.retrieveAll(FETCH_ALL_STATEMENT)
      .map(rawList -> rawList.stream()
        .map(Account::new)
        .collect(Collectors.toList())
      )
      .setHandler(resultHandler);
    return this;
  }

  @Override
  public AccountService updateAccount(Account account, Handler<AsyncResult<Account>> resultHandler) {
    JsonArray params = new JsonArray()
      .add(account.getUsername())
      .add(account.getPhone())
      .add(account.getEmail())
      .add(account.getBirthDate())
      .add(account.getId());
    this.execute(params, UPDATE_STATEMENT, account, resultHandler);
    return this;
  }

  @Override
  public AccountService deleteAccount(String id, Handler<AsyncResult<Void>> resultHandler) {
    this.removeOne(id, DELETE_STATEMENT, resultHandler);
    return this;
  }

  @Override
  public AccountService deleteAllAccounts(Handler<AsyncResult<Void>> resultHandler) {
    this.removeAll(DELETE_ALL_STATEMENT, resultHandler);
    return this;
  }

  // SQL statement

  private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `user_account` (\n" +
    "  `id` varchar(30) NOT NULL,\n" +
    "  `username` varchar(20) NOT NULL,\n" +
    "  `phone` varchar(20) NOT NULL,\n" +
    "  `email` varchar(45) NOT NULL,\n" +
    "  `birthDate` bigint(20) NOT NULL,\n" +
    "  PRIMARY KEY (`id`),\n" +
    "  UNIQUE KEY `username_UNIQUE` (`username`) )";
  private static final String INSERT_STATEMENT = "INSERT INTO user_account (id, username, phone, email, birthDate) VALUES (?, ?, ?, ?, ?)";
  private static final String EXISTS_STATEMENT = "SELECT EXISTS(1) FROM user_account WHERE username = ?";
  private static final String FETCH_STATEMENT = "SELECT * FROM user_account WHERE id = ?";
  private static final String FETCH_BY_USERNAME_STATEMENT = "SELECT * FROM user_account WHERE username = ?";
  private static final String FETCH_ALL_STATEMENT = "SELECT * FROM user_account";
  private static final String UPDATE_STATEMENT = "UPDATE `user_account`\n" +
    "SET `username` = ?,\n" +
    "`phone` = ?,\n" +
    "`email` = ?,\n" +
    "`birthDate` = ? \n" +
    "WHERE `id` = ?";
  private static final String DELETE_STATEMENT = "DELETE FROM user_account WHERE id = ?";
  private static final String DELETE_ALL_STATEMENT = "DELETE FROM user_account";

}
