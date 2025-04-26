package com.riccardo.giangiulio.gestionescuola.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.dto.mapper.UserMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.UserDTO;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.SchoolClassService;
import com.riccardo.giangiulio.gestionescuola.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API for the management of users")
public class UserController {
    
    private final UserService userService;
    private final SchoolClassService schoolClassService;
    
    @Autowired
    public UserController(UserService userService, SchoolClassService schoolClassService) {
        this.userService = userService;
        this.schoolClassService = schoolClassService;
    }
    
    @Operation(summary = "Get user by ID", description = "Returns a user based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID of the user to search for") @PathVariable Long id) {
        User user = userService.findById(id);
        UserDTO userDTO = UserMapperDTO.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Get all users", description = "Returns the list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.findAll().stream()
                .map(UserMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
    
    @Operation(summary = "Get user by email", description = "Returns a user based on the specified email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(
            @Parameter(description = "Email of the user to search for") @PathVariable String email) {
        User user = userService.findByEmail(email);
        UserDTO userDTO = UserMapperDTO.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "409", description = "Email already in use"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "Data of the user to create") @RequestBody User user) {
        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Operation(summary = "Update a user", description = "Updates the data of an existing user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Email already in use"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Long id, 
            @Parameter(description = "New data of the user") @RequestBody User user) {
        User updatedUser = userService.update(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user", description = "Deletes an existing user from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User with ID " + id + " deleted successfully.");
    }

    @Operation(summary = "Get all teachers", description = "Returns the list of all teachers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of teachers found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/teachers")
    public ResponseEntity<List<UserDTO>> getAllTeachers() {
        List<UserDTO> teacherDTOs = userService.findByRole(ERole.ROLE_TEACHER).stream()
                .map(UserMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teacherDTOs);
    }
    
    @Operation(summary = "Get all students", description = "Returns the list of all students")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of students found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<List<UserDTO>> getAllStudents() {
        List<UserDTO> studentDTOs = userService.findByRole(ERole.ROLE_STUDENT).stream()
                .map(UserMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }
    
    @Operation(summary = "Get students without active registrations", description = "Returns the list of students without active registrations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of students found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students/without-active-registration")
    public ResponseEntity<List<UserDTO>> getStudentsWithoutActiveRegistration() {
        List<UserDTO> studentDTOs = userService.findStudentsWithoutActiveRegistrations().stream()
                .map(UserMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }
    
    @Operation(summary = "Get students of a class", description = "Returns the list of students of a specific class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of students found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<UserDTO>> getStudentsBySchoolClassId(
            @Parameter(description = "ID of the class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        List<UserDTO> studentDTOs = userService.findStudentsBySchoolClass(schoolClass).stream()
                .map(UserMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }
    
    @Operation(summary = "Get teachers of a class", description = "Returns the list of teachers of a specific class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of teachers found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/class/{classId}/teachers")
    public ResponseEntity<List<UserDTO>> getTeachersBySchoolClassId(
            @Parameter(description = "ID of the class") @PathVariable Long classId) {
        List<UserDTO> teacherDTOs = userService.findTeachersBySchoolClass(schoolClassService.findById(classId)).stream()
                .map(UserMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teacherDTOs);
    }
    
    @Operation(summary = "Assign a role to a user", description = "Assigns a specific role to an existing user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
        @ApiResponse(responseCode = "404", description = "User or role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDTO> assignRole(
            @Parameter(description = "ID of the user") @PathVariable Long id, 
            @Parameter(description = "Role to assign") @RequestParam ERole role) {
        User updatedUser = userService.assignRole(id, role);
        UserDTO userDTO = UserMapperDTO.toDTO(updatedUser);
        return ResponseEntity.ok(userDTO);
    }
    
    @Operation(summary = "Change user password", description = "Allows a user to change their password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Current password is invalid"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @PatchMapping("/{id}/password")
    public ResponseEntity<String> changePassword(
            @Parameter(description = "ID of the user") @PathVariable Long id, 
            @Parameter(description = "Current password") @RequestParam String currentPassword, 
            @Parameter(description = "New password") @RequestParam String newPassword) {
        userService.changePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }
}