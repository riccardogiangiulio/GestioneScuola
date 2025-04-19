package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class MinimumTeachersException extends BusinessValidationException {
    public MinimumTeachersException(Long schoolClassId) {
        super("School class with ID: " + schoolClassId + " must have at least one teacher");
    }
} 