package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.repository.RoleRepository;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.RoleNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.DuplicateRoleException;

@Service
public class RoleService {
    
    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    
    private final RoleRepository roleRepository;
    
    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        log.info("RoleService initialized");
    }
    
    public List<Role> getAllRoles() {
        log.debug("Retrieving all roles");
        List<Role> roles = roleRepository.findAll();
        log.info("Retrieved {} roles", roles.size());
        return roles;
    }
    
    public Role getRoleById(Long id) {
        log.debug("Finding role with id: {}", id);
        return roleRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Role not found with id: {}", id);
                return new RoleNotFoundException(id);
            });
    }
    
    public Role getRoleByName(ERole name) {
        log.debug("Finding role by name: {}", name);
        return roleRepository.findByName(name)
            .orElseThrow(() -> {
                log.error("Role not found with name: {}", name);
                return new RoleNotFoundException(name);
            });
    }
    
    public Role saveRole(Role role) {
        log.info("Saving role: {}", role.getName());
        // Verifica se il ruolo esiste già
        Optional<Role> existingRole = roleRepository.findByName(role.getName());
        if (existingRole.isPresent()) {
            log.error("Failed to save role: Role with name '{}' already exists", role.getName());
            throw new DuplicateRoleException(role.getName());
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("Role saved successfully with ID: {}", savedRole.getId());
        return savedRole;
    }
    
    public void deleteRoleById(Long id) {
        log.warn("Attempting to delete role with id: {}", id);
        if (!roleRepository.existsById(id)) {
            log.error("Failed to delete role: Not found with id: {}", id);
            throw new RoleNotFoundException(id);
        }
        
        roleRepository.deleteById(id);
        log.info("Role deleted successfully with id: {}", id);
    }
    
    //Metodo per inizializzare i ruoli di base nel sistema
    public void initializeRoles() {
        log.info("Initializing basic roles");
        int createdRoles = 0;
        for (ERole roleType : ERole.values()) {
            if (!roleRepository.findByName(roleType).isPresent()) {
                log.info("Creating basic role: {}", roleType);
                Role role = new Role(roleType);
                roleRepository.save(role);
                createdRoles++;
            }
        }
        if (createdRoles > 0) {
            log.info("Successfully created {} basic roles", createdRoles);
        } else {
            log.info("All basic roles already exist");
        }
    }
}
