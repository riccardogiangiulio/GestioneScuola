package com.riccardo.giangiulio.gestionescuola.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.dto.mapper.RoleMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.RoleDTO;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Role Controller", description = "API for the management of roles")
public class RoleController {
    
    private final RoleService roleService;
    
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @Operation(summary = "Get all roles", description = "Returns the list of all available roles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDTO> roleDTOs = roles.stream()
                .map(RoleMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }
    
    @Operation(summary = "Get role by ID", description = "Returns a role based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role found", content = @Content(schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(
            @Parameter(description = "ID of the role to search for") @PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        RoleDTO roleDTO = RoleMapperDTO.toDTO(role);
        return ResponseEntity.ok(roleDTO);
    }
    
    @Operation(summary = "Get role by name", description = "Returns a role based on the specified name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role found", content = @Content(schema = @Schema(implementation = RoleDTO.class))),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(
            @Parameter(description = "Name of the role to search for") @PathVariable ERole name) {
        Role role = roleService.getRoleByName(name);
        RoleDTO roleDTO = RoleMapperDTO.toDTO(role);
        return ResponseEntity.ok(roleDTO);
    }
    
    @Operation(summary = "Create a new role", description = "Creates a new role in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid role data"),
        @ApiResponse(responseCode = "409", description = "Role already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Role> createRole(
            @Parameter(description = "Data of the role to create") @RequestBody Role role) {
        Role savedRole = roleService.saveRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }
    
    @Operation(summary = "Delete a role", description = "Deletes a role based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID of the role to delete") @PathVariable Long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.ok().build();
    }
    
}
