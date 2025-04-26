package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class SchoolClassNotFoundException extends ResourceNotFoundException {
    public SchoolClassNotFoundException(Long id) {
        super("School class not found with ID: " + id);
    }

    public SchoolClassNotFoundException(String name) {
        super("School class not found with name: " + name);
    }
}