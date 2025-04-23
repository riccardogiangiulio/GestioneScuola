package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;
 
public class ActiveRegistrationsException extends BusinessValidationException {
    public ActiveRegistrationsException(Long schoolClassId, int registrationsCount) {
        super("Cannot delete school class with ID: " + schoolClassId + " because it has " + registrationsCount + " active registrations");
    }
} 