package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

import com.riccardo.giangiulio.gestionescuola.model.ERole;
 
public class DuplicateRoleException extends BusinessValidationException {
    public DuplicateRoleException(ERole name) {
        super("Role with name: " + name + " already exists");
    }
} 