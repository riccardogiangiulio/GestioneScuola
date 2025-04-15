package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);

    Boolean existsByName(String name);
}
