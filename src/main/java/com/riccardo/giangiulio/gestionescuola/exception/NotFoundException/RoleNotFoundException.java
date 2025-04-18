package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(Long id) {
        super("Role not found with ID: " + id);
    }
}
