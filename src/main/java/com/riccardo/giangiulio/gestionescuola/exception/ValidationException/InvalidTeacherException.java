package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class InvalidTeacherException extends BusinessValidationException {
    public InvalidTeacherException(Long id) {
        super("User with ID: " + id + " is not a teacher");
    }
}
