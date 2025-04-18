package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
