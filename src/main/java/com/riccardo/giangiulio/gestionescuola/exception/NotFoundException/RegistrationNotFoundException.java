package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class RegistrationNotFoundException extends ResourceNotFoundException {
    public RegistrationNotFoundException(Long id) {
        super("Registration not found with ID: " + id);
    }
    
    public RegistrationNotFoundException(Long studentId, Long schoolClassId) {
        super("Registration not found for student " + studentId + " and school class " + schoolClassId);
    }
}
