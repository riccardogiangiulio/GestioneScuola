package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long id) {
        super("User not found with ID: " + id);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}