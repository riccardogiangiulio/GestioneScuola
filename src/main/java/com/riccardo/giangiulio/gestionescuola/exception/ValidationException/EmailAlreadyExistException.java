package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class EmailAlreadyExistException extends BusinessValidationException {
    public EmailAlreadyExistException(String email) {
        super("Email already exists: " + email);
    }
}
