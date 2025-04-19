package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

import com.riccardo.giangiulio.gestionescuola.model.ERole;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(Long id) {
        super("Role not found with ID: " + id);
    }

    public RoleNotFoundException(ERole name) {
        super("Role not found with name: " + name);
    }
}
