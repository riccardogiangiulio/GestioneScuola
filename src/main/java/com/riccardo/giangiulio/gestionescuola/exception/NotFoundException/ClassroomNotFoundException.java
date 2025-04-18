package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class ClassroomNotFoundException extends ResourceNotFoundException {
    public ClassroomNotFoundException(Long id) {
        super("Classroom not found with ID: " + id);
    }
}
