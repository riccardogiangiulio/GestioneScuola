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

import com.riccardo.giangiulio.gestionescuola.dto.mapper.SubjectMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.SubjectDTO;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.service.CourseService;
import com.riccardo.giangiulio.gestionescuola.service.SubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/subjects")
@Tag(name = "Subject Controller", description = "APIs for subject management")
public class SubjectController {
    
    private final SubjectService subjectService;
    private final CourseService courseService;
    
    @Autowired
    public SubjectController(SubjectService subjectService, CourseService courseService) {
        this.subjectService = subjectService;
        this.courseService = courseService;
    }
    
    @Operation(summary = "Get subject by ID", description = "Returns a subject based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject found", content = @Content(schema = @Schema(implementation = SubjectDTO.class))),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(
            @Parameter(description = "ID of the subject to search for") @PathVariable Long id) {
        Subject subject = subjectService.findById(id);
        SubjectDTO subjectDTO = SubjectMapperDTO.toDTO(subject);
        return ResponseEntity.ok(subjectDTO);
    }
    
    @Operation(summary = "Get all subjects", description = "Returns the list of all subjects")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of subjects found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        List<SubjectDTO> subjectDTOs = subjectService.findAll().stream()
                .map(SubjectMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }
    
    @Operation(summary = "Create a new subject", description = "Creates a new subject in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Subject created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid subject data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(
            @Parameter(description = "Data of the subject to create") @RequestBody Subject subject) {
        Subject savedSubject = subjectService.save(subject);
        SubjectDTO subjectDTO = SubjectMapperDTO.toDTO(savedSubject);
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectDTO);
    }
    
    @Operation(summary = "Update a subject", description = "Updates the data of an existing subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject updated successfully"),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "400", description = "Invalid subject data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @Parameter(description = "ID of the subject to update") @PathVariable Long id,
            @Parameter(description = "New subject data") @RequestBody Subject subject) {
        Subject updatedSubject = subjectService.update(id, subject);
        SubjectDTO subjectDTO = SubjectMapperDTO.toDTO(updatedSubject);
        return ResponseEntity.ok(subjectDTO);
    }
    
    @Operation(summary = "Delete a subject", description = "Deletes an existing subject from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(
            @Parameter(description = "ID of the subject to delete") @PathVariable Long id) {
        subjectService.deleteById(id);
        return ResponseEntity.ok("Subject with ID " + id + " successfully deleted.");
    }
    
    @Operation(summary = "Get subject by name", description = "Returns a subject based on the specified name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject found"),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/name")
    public ResponseEntity<SubjectDTO> getSubjectByName(
            @Parameter(description = "Name of the subject to search for") @RequestParam String name) {
        Subject subject = subjectService.findByName(name);
        SubjectDTO subjectDTO = SubjectMapperDTO.toDTO(subject);
        return ResponseEntity.ok(subjectDTO);
    }
    
    @Operation(summary = "Get subjects by course", description = "Returns the list of subjects for a specific course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of subjects found"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByCourse(
            @Parameter(description = "ID of the course") @PathVariable Long courseId) {
        Course course = courseService.findById(courseId);
        List<SubjectDTO> subjectDTOs = subjectService.findByCourse(course).stream()
                .map(SubjectMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }
    
    @Operation(summary = "Assign teacher to subject", description = "Assigns a teacher to an existing subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Subject or teacher not found"),
        @ApiResponse(responseCode = "400", description = "User is not a teacher"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{subjectId}/teachers/{teacherId}")
    public ResponseEntity<SubjectDTO> assignTeacherToSubject(
            @Parameter(description = "ID of the subject") @PathVariable Long subjectId,
            @Parameter(description = "ID of the teacher") @PathVariable Long teacherId) {
        Subject updatedSubject = subjectService.assignTeacher(subjectId, teacherId);
        SubjectDTO subjectDTO = SubjectMapperDTO.toDTO(updatedSubject);
        return ResponseEntity.ok(subjectDTO);
    }
}