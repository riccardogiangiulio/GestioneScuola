package com.riccardo.giangiulio.gestionescuola.spring_jwt.payload.response;

import java.time.LocalDate;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.model.ERole;

public class JwtResponse {

  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private LocalDate birthDate;
  private ERole role;

  public JwtResponse() {

  }

  public JwtResponse(String token, Long id, String username, String email, 
                    String firstName, String lastName, LocalDate birthDate, ERole role) {
    this.token = token;
    this.id = id;
    this.username = username;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.role = role;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }
  
  public ERole getRole() {
    return role;
  }

  public void setRole(ERole role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JwtResponse that = (JwtResponse) o;
    return Objects.equals(token, that.token) &&
           Objects.equals(type, that.type) &&
           Objects.equals(id, that.id) &&
           Objects.equals(username, that.username) &&
           Objects.equals(email, that.email) &&
           Objects.equals(firstName, that.firstName) &&
           Objects.equals(lastName, that.lastName) &&
           Objects.equals(role, that.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, type, id, username, email, firstName, lastName, role);
  }
}
