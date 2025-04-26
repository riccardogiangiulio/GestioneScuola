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

import com.riccardo.giangiulio.gestionescuola.dto.mapper.RegistrationMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.RegistrationDTO;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.RegistrationService;
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
@RequestMapping("/api/registrations")
@Tag(name = "Registration Controller", description = "API for the management of student registrations")
public class RegistrationController {
    
    private final RegistrationService registrationService;
    private final UserService userService;
    private final SchoolClassService schoolClassService;
    
    @Autowired
    public RegistrationController(
            RegistrationService registrationService,
            UserService userService,
            SchoolClassService schoolClassService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.schoolClassService = schoolClassService;
    }
    
    @Operation(summary = "Get registration by ID", description = "Returns a registration based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration found", content = @Content(schema = @Schema(implementation = RegistrationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Registration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistrationById(
            @Parameter(description = "ID of the registration to search for") @PathVariable Long id) {
        Registration registration = registrationService.findById(id);
        RegistrationDTO registrationDTO = RegistrationMapperDTO.toDTO(registration);
        return ResponseEntity.ok(registrationDTO);
    }
    
    @Operation(summary = "Get all registrations", description = "Returns the list of all registrations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RegistrationDTO>> getAllRegistrations() {
        List<RegistrationDTO> registrationDTOs = registrationService.findAll().stream()
                .map(RegistrationMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registrationDTOs);
    }
    
    @Operation(summary = "Create a new registration", description = "Creates a new registration in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RegistrationDTO> createRegistration(
            @Parameter(description = "Data of the registration to create") @RequestBody Registration registration) {
        Registration savedRegistration = registrationService.save(registration);
        RegistrationDTO registrationDTO = RegistrationMapperDTO.toDTO(savedRegistration);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationDTO);
    }
    
    @Operation(summary = "Update a registration", description = "Updates the data of an existing registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration updated successfully"),
        @ApiResponse(responseCode = "404", description = "Registration not found"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationDTO> updateRegistration(
            @Parameter(description = "ID of the registration to update") @PathVariable Long id,
            @Parameter(description = "New data of the registration") @RequestBody Registration registration) {
        Registration updatedRegistration = registrationService.update(id, registration);
        RegistrationDTO registrationDTO = RegistrationMapperDTO.toDTO(updatedRegistration);
        return ResponseEntity.ok(registrationDTO);
    }
    
    @Operation(summary = "Delete a registration", description = "Deletes an existing registration from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Registration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRegistration(
            @Parameter(description = "ID of the registration to delete") @PathVariable Long id) {
        registrationService.deleteById(id);
        return ResponseEntity.ok("Registration with ID " + id + " deleted successfully.");
    }
    
    @Operation(summary = "Get registrations by student", description = "Returns the list of registrations for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        List<RegistrationDTO> registrationDTOs = registrationService.findByStudent(student).stream()
                .map(RegistrationMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registrationDTOs);
    }
    
    @Operation(summary = "Get registrations by school class", description = "Returns the list of registrations for a specific school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "404", description = "School class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsBySchoolClass(
            @Parameter(description = "ID of the school class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        List<RegistrationDTO> registrationDTOs = registrationService.findBySchoolClass(schoolClass).stream()
                .map(RegistrationMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registrationDTOs);
    }
    
    @Operation(summary = "Get registrations by status", description = "Returns the list of registrations with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByStatus(
            @Parameter(description = "Registration status") @RequestParam RegistrationStatus status) {
        List<RegistrationDTO> registrationDTOs = registrationService.findByStatus(status).stream()
                .map(RegistrationMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registrationDTOs);
    }
    
    @Operation(summary = "Get registration by student and school class", description = "Returns a specific registration for a student in a school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration found"),
        @ApiResponse(responseCode = "404", description = "Registration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/class/{classId}")
    public ResponseEntity<RegistrationDTO> getRegistrationByStudentAndSchoolClass(
            @Parameter(description = "ID of the student") @PathVariable Long studentId,
            @Parameter(description = "ID of the school class") @PathVariable Long classId) {
        User student = userService.findById(studentId);
        SchoolClass schoolClass = schoolClassService.findById(classId);
        Registration registration = registrationService.findByStudentAndSchoolClass(student, schoolClass);
        RegistrationDTO registrationDTO = RegistrationMapperDTO.toDTO(registration);
        return ResponseEntity.ok(registrationDTO);
    }
    
    @Operation(summary = "Count active registrations by school class", description = "Returns the count of active registrations for a school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count of registrations found"),
        @ApiResponse(responseCode = "404", description = "School class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/class/{classId}/count-active")
    public ResponseEntity<Long> countActiveRegistrationsBySchoolClass(
            @Parameter(description = "ID of the school class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        Long count = registrationService.countActiveRegistrationsBySchoolClass(schoolClass);
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Get registrations by course", description = "Returns the list of registrations for a specific course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByCourse(
            @Parameter(description = "ID of the course") @PathVariable Long courseId) {
        List<RegistrationDTO> registrationDTOs = registrationService.findByCourseId(courseId).stream()
                .map(RegistrationMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registrationDTOs);
    }
    
    @Operation(summary = "Get active registrations by student", description = "Returns the list of active registrations for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<RegistrationDTO> getActiveRegistrationByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        Registration registration = registrationService.findActiveByStudent(student);
        RegistrationDTO registrationDTO = RegistrationMapperDTO.toDTO(registration);
        return ResponseEntity.ok(registrationDTO);
    }
    
    @Operation(summary = "Get active registrations by school class", description = "Returns the list of active registrations for a specific school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "404", description = "School class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/class/{classId}/active")
    public ResponseEntity<List<RegistrationDTO>> getActiveRegistrationsBySchoolClass(
            @Parameter(description = "ID of the school class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        List<RegistrationDTO> registrationDTOs = registrationService.findActiveBySchoolClass(schoolClass).stream()
                .map(RegistrationMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(registrationDTOs);
    }
    
    @Operation(summary = "Change registration status", description = "Updates the status of an existing registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Registration not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<RegistrationDTO> changeStatus(
            @Parameter(description = "ID of the registration") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam RegistrationStatus status) {
        Registration updatedRegistration = registrationService.changeStatus(id, status);
        RegistrationDTO registrationDTO = RegistrationMapperDTO.toDTO(updatedRegistration);
        return ResponseEntity.ok(registrationDTO);
    }
}
