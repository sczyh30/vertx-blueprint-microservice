package io.vertx.blueprint.microservice.user.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;

/**
 * Vert.x Blueprint - Shopping Microservice
 * User data object
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class User {

  private Long id;
  private String username;
  private String phone;
  private String email;
  private String birthDate;

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


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }
}
