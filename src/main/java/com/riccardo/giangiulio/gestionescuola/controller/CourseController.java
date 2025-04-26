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

import com.riccardo.giangiulio.gestionescuola.dto.mapper.CourseMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.CourseDTO;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Controller", description = "API for the management of courses")
public class CourseController {
    
    private final CourseService courseService;
    
    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    
    @Operation(summary = "Get course by ID", description = "Returns a course based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found", content = @Content(schema = @Schema(implementation = CourseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(
            @Parameter(description = "ID of the course to search for") @PathVariable Long id) {
        Course course = courseService.findById(id);
        CourseDTO courseDTO = CourseMapperDTO.toDTO(course);
        return ResponseEntity.ok(courseDTO);
    }
    
    @Operation(summary = "Get all courses", description = "Returns the list of all courses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of courses found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courseDTOs = courseService.findAll().stream()
                .map(CourseMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }
    
    @Operation(summary = "Create a new course", description = "Creates a new course in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid course data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Course> createCourse(
            @Parameter(description = "Data of the course to create") @RequestBody Course course) {
        Course savedCourse = courseService.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }
    
    @Operation(summary = "Update a course", description = "Updates the data of an existing course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course updated successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "400", description = "Invalid course data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @Parameter(description = "ID of the course to update") @PathVariable Long id,
            @Parameter(description = "New data of the course") @RequestBody Course course) {
        Course updatedCourse = courseService.update(id, course);
        CourseDTO courseDTO = CourseMapperDTO.toDTO(updatedCourse);
        return ResponseEntity.ok(courseDTO);
    }
    
    @Operation(summary = "Delete a course", description = "Deletes an existing course from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @Parameter(description = "ID of the course to delete") @PathVariable Long id) {
        courseService.deleteById(id);
        return ResponseEntity.ok("Course with ID " + id + " deleted successfully.");
    }
    
    @Operation(summary = "Find course by title", description = "Returns a course with the specified title")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/search/title")
    public ResponseEntity<CourseDTO> getCourseByTitle(
            @Parameter(description = "Title of the course to search for") @RequestParam String title) {
        Course course = courseService.findByTitle(title);
        CourseDTO courseDTO = CourseMapperDTO.toDTO(course);
        return ResponseEntity.ok(courseDTO);
    }
    
    @Operation(summary = "Add subject to course", description = "Associates an existing subject with a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject added successfully"),
        @ApiResponse(responseCode = "404", description = "Course or subject not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{courseId}/subjects/{subjectId}")
    public ResponseEntity<String> addSubjectToCourse(
            @Parameter(description = "ID of the course") @PathVariable Long courseId,
            @Parameter(description = "ID of the subject to add") @PathVariable Long subjectId) {
        courseService.addSubject(courseId, subjectId);
        return ResponseEntity.ok("Subject with ID " + subjectId + " added to course with ID " + courseId);
    }
    
    @Operation(summary = "Remove subject from course", description = "Removes the association between a subject and a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject removed successfully"),
        @ApiResponse(responseCode = "404", description = "Course or subject not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{courseId}/subjects/{subjectId}")
    public ResponseEntity<String> removeSubjectFromCourse(
            @Parameter(description = "ID of the course") @PathVariable Long courseId,
            @Parameter(description = "ID of the subject to remove") @PathVariable Long subjectId) {
        courseService.removeSubject(courseId, subjectId);
        return ResponseEntity.ok("Subject with ID " + subjectId + " removed from course with ID " + courseId);
    }
    
    @Operation(summary = "Add exam to course", description = "Associates an existing exam with a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam added successfully"),
        @ApiResponse(responseCode = "404", description = "Course or exam not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{courseId}/exams/{examId}")
    public ResponseEntity<String> addExamToCourse(
            @Parameter(description = "ID of the course") @PathVariable Long courseId,
            @Parameter(description = "ID of the exam to add") @PathVariable Long examId) {
        courseService.addExam(courseId, examId);
        return ResponseEntity.ok("Exam with ID " + examId + " added to course with ID " + courseId);
    }
    
    @Operation(summary = "Remove exam from course", description = "Removes the association between an exam and a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam removed successfully"),
        @ApiResponse(responseCode = "404", description = "Course or exam not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{courseId}/exams/{examId}")
    public ResponseEntity<String> removeExamFromCourse(
            @Parameter(description = "ID of the course") @PathVariable Long courseId,
            @Parameter(description = "ID of the exam to remove") @PathVariable Long examId) {
        courseService.removeExam(courseId, examId);
        return ResponseEntity.ok("Exam with ID " + examId + " removed from course with ID " + courseId);
    }
}
