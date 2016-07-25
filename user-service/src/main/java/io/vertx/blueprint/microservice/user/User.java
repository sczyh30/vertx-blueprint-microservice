package io.vertx.blueprint.microservice.user;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

/**
 * User data object
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class User {

  private String id;
  private String username;
  private String phone;
  private String email;
  private Long birthDate;

  public User() {
    // Empty constructor
  }

  public User(User other) {
    this.id = other.id;
    this.username = other.username;
    this.phone = other.phone;
    this.email = other.email;
    this.birthDate = other.birthDate;
  }

  public User(JsonObject json) {
    UserConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    UserConverter.toJson(this, json);
    return json;
  }


  public String getId() {
    return id;
  }

  public User setId(String id) {
    this.id = id;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public User setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public User setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public User setEmail(String email) {
    this.email = email;
    return this;
  }

  public Long getBirthDate() {
    return birthDate;
  }

  public User setBirthDate(Long birthDate) {
    this.birthDate = birthDate;
    return this;
  }
}
