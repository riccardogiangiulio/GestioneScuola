package com.riccardo.giangiulio.gestionescuola.spring_jwt.payload.request;

import java.time.LocalDate;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.model.ERole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;


public class SignupRequest {
   
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(min = 2, max = 50)
  private String firstName;

  @NotBlank
  @Size(min = 2, max = 50)
  private String lastName;

  @Past(message = "The birth date cannot be in the future")
  private LocalDate birthDate;

  private ERole role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  public SignupRequest() {
    
  }

  public SignupRequest(String username, String email, String firstName, String lastName, 
                      LocalDate birthDate, ERole role, String password) {
    this.username = username;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.role = role;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public ERole getRole() {
    return role;
  }

  public String getPassword() {
    return password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public void setRole(ERole role) {
    this.role = role;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SignupRequest that = (SignupRequest) o;
    return Objects.equals(username, that.username) && 
           Objects.equals(email, that.email) &&
           Objects.equals(firstName, that.firstName) &&
           Objects.equals(lastName, that.lastName) &&
           Objects.equals(birthDate, that.birthDate) &&
           role == that.role && 
           Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, email, firstName, lastName, birthDate, role, password);
  }

}