package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.model.ERole;

public class RoleDTO {
    
    private Long id;
    private ERole name;

    public RoleDTO() {

    }

    public RoleDTO(Long id, ERole name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    @Override
    public  boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDTO roleDTO = (RoleDTO) o;
        return id.equals(roleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
