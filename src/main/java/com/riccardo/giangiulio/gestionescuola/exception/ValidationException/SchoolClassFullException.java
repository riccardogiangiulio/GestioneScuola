package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class SchoolClassFullException extends BusinessValidationException {
    public SchoolClassFullException(Long id) {
        super("School class with ID: " + id + " has reached the maximum number of students");
    }
}
