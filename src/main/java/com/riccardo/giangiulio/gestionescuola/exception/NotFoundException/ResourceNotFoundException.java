package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}