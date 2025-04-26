package com.riccardo.giangiulio.gestionescuola.spring_jwt.util.security.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.riccardo.giangiulio.gestionescuola.model.User;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String firstName;

  private String lastName;

  private String email;

  private String username;

  @JsonIgnore
  private String password;

  private LocalDate birthDate;

  private List<GrantedAuthority> authorities;

  public UserDetailsImpl() {

  }

  public UserDetailsImpl(Long id, String firstName, String lastName, String email, String username, String password, LocalDate birthDate, List<GrantedAuthority> authorities) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.username = username;
    this.password = password;
    this.birthDate = birthDate;
    this.authorities = authorities;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  /**
  * Crea un'istanza di UserDetailsImpl a partire da un oggetto User.
  */
  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority(user.getRole().getName().name())
    );

    return new UserDetailsImpl(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getUsername(),
        user.getPassword(),
        user.getBirthDate(),
        authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    UserDetailsImpl user = (UserDetailsImpl) o;

    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}