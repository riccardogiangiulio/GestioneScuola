package com.riccardo.giangiulio.gestionescuola.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.RoleService;
import com.riccardo.giangiulio.gestionescuola.service.UserService;
import com.riccardo.giangiulio.gestionescuola.spring_jwt.payload.request.LoginRequest;
import com.riccardo.giangiulio.gestionescuola.spring_jwt.payload.request.SignupRequest;
import com.riccardo.giangiulio.gestionescuola.spring_jwt.payload.response.JwtResponse;
import com.riccardo.giangiulio.gestionescuola.spring_jwt.payload.response.MessageResponse;
import com.riccardo.giangiulio.gestionescuola.spring_jwt.util.security.jwt.JwtUtils;
import com.riccardo.giangiulio.gestionescuola.spring_jwt.util.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserService userService;
  
  @Autowired
  RoleService roleService;

  @Autowired
  JwtUtils jwtUtils;

  /**
  * Gestisce il login degli utenti.
  */
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    // Ottiene i dettagli dell'utente autenticato
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    
    // Estrae il ruolo dell'utente (un solo ruolo per utente)
    ERole role = null;
    if (!userDetails.getAuthorities().isEmpty()) {
        String roleStr = userDetails.getAuthorities().iterator().next().getAuthority();
        role = ERole.valueOf(roleStr);
    }

    return ResponseEntity.ok(new JwtResponse(jwt,
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        userDetails.getFirstName(),
        userDetails.getLastName(),
        userDetails.getBirthDate(),
        role));
  } 

  /**
   * Gestisce la registrazione di nuovi utenti.
   */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userService.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userService.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    ERole userRole = signUpRequest.getRole();
    if (userRole == null) {
      userRole = ERole.ROLE_ADMIN;
    }
    
    Role role = roleService.getRoleByName(userRole);
    if (role == null) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Role not found in database."));
    }

    User user = new User(
        signUpRequest.getFirstName(),
        signUpRequest.getLastName(),
        signUpRequest.getEmail(),
        signUpRequest.getUsername(),
        signUpRequest.getPassword(),
        signUpRequest.getBirthDate(),
        role
    );

    userService.save(user);
    
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
