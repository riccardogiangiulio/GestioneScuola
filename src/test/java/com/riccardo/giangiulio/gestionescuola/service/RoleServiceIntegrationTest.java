package com.riccardo.giangiulio.gestionescuola.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.RoleNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.DuplicateRoleException;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.repository.RoleRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoleServiceIntegrationTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;
    
    private Role studentRole;
    private Role teacherRole;
    private Role adminRole;

    @BeforeEach
    public void setUp() {
        // Pulisci tutti i ruoli per garantire un ambiente pulito per ogni test
        roleRepository.deleteAll();
        
        // Crea ruoli per i test
        studentRole = new Role(ERole.ROLE_STUDENT);
        studentRole = roleService.saveRole(studentRole);
        
        teacherRole = new Role(ERole.ROLE_TEACHER);
        teacherRole = roleService.saveRole(teacherRole);
        
        adminRole = new Role(ERole.ROLE_ADMIN);
        adminRole = roleService.saveRole(adminRole);
    }

    @Test
    public void testGetAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals(3, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.getName() == ERole.ROLE_STUDENT));
        assertTrue(roles.stream().anyMatch(r -> r.getName() == ERole.ROLE_TEACHER));
        assertTrue(roles.stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN));
    }

    @Test
    public void testGetRoleById() {
        Role found = roleService.getRoleById(studentRole.getId());
        
        assertNotNull(found);
        assertEquals(studentRole.getId(), found.getId());
        assertEquals(ERole.ROLE_STUDENT, found.getName());
    }
    
    @Test
    public void testGetRoleByIdNotFound() {
        Long nonExistentId = 999999L;
        
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> {
            roleService.getRoleById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testGetRoleByName() {
        Role found = roleService.getRoleByName(ERole.ROLE_STUDENT);
        
        assertNotNull(found);
        assertEquals(studentRole.getId(), found.getId());
        assertEquals(ERole.ROLE_STUDENT, found.getName());
    }
    
    @Test
    public void testGetRoleByNameNotFound() {
        // Prima eliminiamo il ruolo studente
        roleService.deleteRoleById(studentRole.getId());
        
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> {
            roleService.getRoleByName(ERole.ROLE_STUDENT);
        });
        
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testSaveRole() {
        // Prima eliminiamo il ruolo student per poterlo ricreare
        roleService.deleteRoleById(studentRole.getId());
        
        Role newRole = new Role(ERole.ROLE_STUDENT);
        Role savedRole = roleService.saveRole(newRole);
        
        assertNotNull(savedRole);
        assertNotNull(savedRole.getId());
        assertEquals(ERole.ROLE_STUDENT, savedRole.getName());
        
        // Verifica che il ruolo sia stato salvato correttamente
        Role retrievedRole = roleService.getRoleById(savedRole.getId());
        assertEquals(savedRole.getId(), retrievedRole.getId());
        assertEquals(ERole.ROLE_STUDENT, retrievedRole.getName());
    }
    
    @Test
    public void testSaveRoleDuplicate() {
        Role duplicateRole = new Role(ERole.ROLE_STUDENT);
        
        DuplicateRoleException exception = assertThrows(DuplicateRoleException.class, () -> {
            roleService.saveRole(duplicateRole);
        });
        
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testDeleteRoleById() {
        Long idToDelete = teacherRole.getId();
        
        roleService.deleteRoleById(idToDelete);
        
        // Verifica che il ruolo sia stato eliminato
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> {
            roleService.getRoleById(idToDelete);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteRoleByIdNotFound() {
        Long nonExistentId = 999999L;
        
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> {
            roleService.deleteRoleById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testInitializeRoles() {
        // Prima eliminiamo tutti i ruoli
        roleRepository.deleteAll();
        
        // Inizializza i ruoli
        roleService.initializeRoles();
        
        // Verifica che tutti i ruoli siano stati creati
        List<Role> roles = roleService.getAllRoles();
        assertEquals(3, roles.size());
        
        // Verifica che ogni tipo di ruolo esista
        boolean hasStudent = false;
        boolean hasTeacher = false;
        boolean hasAdmin = false;
        
        for (Role role : roles) {
            if (role.getName() == ERole.ROLE_STUDENT) hasStudent = true;
            if (role.getName() == ERole.ROLE_TEACHER) hasTeacher = true;
            if (role.getName() == ERole.ROLE_ADMIN) hasAdmin = true;
        }
        
        assertTrue(hasStudent);
        assertTrue(hasTeacher);
        assertTrue(hasAdmin);
    }
}
