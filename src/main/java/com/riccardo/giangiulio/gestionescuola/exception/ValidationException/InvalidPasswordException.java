package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class InvalidPasswordException extends BusinessValidationException {
    public InvalidPasswordException(String password) {
        super("Invalid current password: " + password);
    }
}
