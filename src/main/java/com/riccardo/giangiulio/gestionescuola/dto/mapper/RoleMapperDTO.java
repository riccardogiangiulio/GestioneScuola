package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.RoleDTO;
import com.riccardo.giangiulio.gestionescuola.model.Role;

public class RoleMapperDTO {
    
    public static RoleDTO toDTO(Role role) {
        return new RoleDTO(role.getId(), role.getName());
    }
}
