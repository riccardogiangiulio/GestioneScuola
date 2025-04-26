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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.dto.mapper.RegistrationMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.mapper.SchoolClassMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.RegistrationDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.SchoolClassDTO;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.CourseService;
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
@RequestMapping("/api/classes")
@Tag(name = "School Class Controller", description = "APIs for school class management")
public class SchoolClassController {
    
    private final SchoolClassService schoolClassService;
    private final UserService userService;
    private final CourseService courseService;
    
    @Autowired
    public SchoolClassController(
            SchoolClassService schoolClassService,
            UserService userService,
            CourseService courseService) {
        this.schoolClassService = schoolClassService;
        this.userService = userService;
        this.courseService = courseService;
    }
    
    @Operation(summary = "Get class by ID", description = "Returns a school class based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Class found", content = @Content(schema = @Schema(implementation = SchoolClassDTO.class))),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<SchoolClassDTO> getSchoolClassById(
            @Parameter(description = "ID of the class to search for") @PathVariable Long id) {
        SchoolClass schoolClass = schoolClassService.findById(id);
        SchoolClassDTO schoolClassDTO = SchoolClassMapperDTO.toDTO(schoolClass);
        return ResponseEntity.ok(schoolClassDTO);
    }
    
    @Operation(summary = "Get all classes", description = "Returns the list of all school classes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classes found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SchoolClassDTO>> getAllSchoolClasses() {
        List<SchoolClassDTO> schoolClassDTOs = schoolClassService.findAll().stream()
                .map(SchoolClassMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(schoolClassDTOs);
    }
    
    @Operation(summary = "Create a new class", description = "Creates a new school class in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Class created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid class data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SchoolClassDTO> createSchoolClass(
            @Parameter(description = "Data of the class to create") @RequestBody SchoolClass schoolClass) {
        SchoolClass savedSchoolClass = schoolClassService.save(schoolClass);
        SchoolClassDTO schoolClassDTO = SchoolClassMapperDTO.toDTO(savedSchoolClass);
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolClassDTO);
    }
    
    @Operation(summary = "Update a class", description = "Updates the data of an existing school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Class updated successfully"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "400", description = "Invalid class data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SchoolClassDTO> updateSchoolClass(
            @Parameter(description = "ID of the class to update") @PathVariable Long id,
            @Parameter(description = "New class data") @RequestBody SchoolClass schoolClass) {
        SchoolClass updatedSchoolClass = schoolClassService.update(id, schoolClass);
        SchoolClassDTO schoolClassDTO = SchoolClassMapperDTO.toDTO(updatedSchoolClass);
        return ResponseEntity.ok(schoolClassDTO);
    }
    
    @Operation(summary = "Delete a class", description = "Deletes an existing school class from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Class deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchoolClass(
            @Parameter(description = "ID of the class to delete") @PathVariable Long id) {
        schoolClassService.deleteById(id);
        return ResponseEntity.ok("Class with ID " + id + " successfully deleted.");
    }
    
    @Operation(summary = "Get classes by course", description = "Returns the list of classes for a specific course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classes found"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<SchoolClassDTO>> getSchoolClassesByCourse(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        Course course = courseService.findById(courseId);
        List<SchoolClassDTO> schoolClassDTOs = schoolClassService.findByCourse(course).stream()
                .map(SchoolClassMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(schoolClassDTOs);
    }
    
    @Operation(summary = "Get classes by teacher", description = "Returns the list of classes for a specific teacher")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classes found"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<SchoolClassDTO>> getSchoolClassesByTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        User teacher = userService.findById(teacherId);
        List<SchoolClassDTO> schoolClassDTOs = schoolClassService.findByTeacher(teacher).stream()
                .map(SchoolClassMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(schoolClassDTOs);
    }
    
    @Operation(summary = "Get classes by name", description = "Returns the list of classes with a specific name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classes found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/name")
    public ResponseEntity<SchoolClassDTO> getSchoolClassesByName(
            @Parameter(description = "Class name") @RequestParam String name) {
        SchoolClass schoolClass = schoolClassService.findByName(name);
        SchoolClassDTO schoolClassDTO = SchoolClassMapperDTO.toDTO(schoolClass);
        return ResponseEntity.ok(schoolClassDTO);
    }
    
    @Operation(summary = "Get available classes", description = "Returns the list of classes with available seats")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classes found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/available")
    public ResponseEntity<List<SchoolClassDTO>> getAvailableSchoolClasses() {
        List<SchoolClassDTO> schoolClassDTOs = schoolClassService.findAvailable().stream()
                .map(SchoolClassMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(schoolClassDTOs);
    }
    
    @Operation(summary = "Get full classes", description = "Returns the list of classes with no available seats")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classes found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/full")
    public ResponseEntity<List<SchoolClassDTO>> getFullSchoolClasses() {
        List<SchoolClassDTO> schoolClassDTOs = schoolClassService.findFull().stream()
                .map(SchoolClassMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(schoolClassDTOs);
    }
    
    @Operation(summary = "Add teacher to a class", description = "Adds a teacher to an existing school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher added successfully"),
        @ApiResponse(responseCode = "404", description = "Class or teacher not found"),
        @ApiResponse(responseCode = "400", description = "User is not a teacher"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{classId}/teachers/{teacherId}")
    public ResponseEntity<SchoolClassDTO> addTeacherToClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        SchoolClass updatedClass = schoolClassService.addTeacher(classId, teacherId);
        SchoolClassDTO schoolClassDTO = SchoolClassMapperDTO.toDTO(updatedClass);
        return ResponseEntity.ok(schoolClassDTO);
    }
    
    @Operation(summary = "Remove teacher from a class", description = "Removes a teacher from an existing school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher removed successfully"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "400", description = "Minimum number of teachers required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classId}/teachers/{teacherId}")
    public ResponseEntity<SchoolClassDTO> removeTeacherFromClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId) {
        SchoolClass updatedClass = schoolClassService.removeTeacher(classId, teacherId);
        SchoolClassDTO schoolClassDTO = SchoolClassMapperDTO.toDTO(updatedClass);
        return ResponseEntity.ok(schoolClassDTO);
    }
    
    @Operation(summary = "Check available seats", description = "Returns the number of available seats in a class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Number of available seats found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/available-seats")
    public ResponseEntity<Integer> getAvailableSeats(
            @Parameter(description = "Class ID") @PathVariable Long id) {
        int availableSeats = schoolClassService.getAvailableSeats(id);
        return ResponseEntity.ok(availableSeats);
    }
    
    @Operation(summary = "Check if class is full", description = "Checks if a class has available seats")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability status found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/is-full")
    public ResponseEntity<Boolean> isClassFull(
            @Parameter(description = "Class ID") @PathVariable Long id) {
        boolean isFull = schoolClassService.isFull(id);
        return ResponseEntity.ok(isFull);
    }
    
    @Operation(summary = "Get active registrations", description = "Returns the list of active registrations for a class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of registrations found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}/active-registrations")
    public ResponseEntity<List<RegistrationDTO>> getActiveRegistrations(
            @Parameter(description = "Class ID") @PathVariable Long id) {
        List<Registration> activeRegistrations = schoolClassService.getActiveRegistrations(id);
        List<RegistrationDTO> activeRegistrationsDTO = activeRegistrations.stream()
                .map(RegistrationMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeRegistrationsDTO);
    }
}
