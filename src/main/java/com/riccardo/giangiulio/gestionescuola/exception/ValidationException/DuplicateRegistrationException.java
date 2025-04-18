package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class DuplicateRegistrationException extends BusinessValidationException {
    public DuplicateRegistrationException(Long studentId, Long schoolClassId) {
        super("Student with ID: " + studentId + " is already registered in school class with ID: " + schoolClassId);
    }
}
