package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class SubjectNotFoundException extends ResourceNotFoundException {
    public SubjectNotFoundException(Long id) {
        super("Subject not found with ID: " + id);
    }

    public SubjectNotFoundException(String name) {
        super("Subject not found with name: " + name);
    }
}
