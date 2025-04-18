package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class InvalidStudentException extends BusinessValidationException {
    public InvalidStudentException(Long id) {
        super("User with ID: " + id + " is not a student");
    }
}
