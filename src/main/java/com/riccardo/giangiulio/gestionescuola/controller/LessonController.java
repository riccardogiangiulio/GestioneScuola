package com.riccardo.giangiulio.gestionescuola.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.riccardo.giangiulio.gestionescuola.dto.mapper.LessonMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.LessonDTO;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.ClassroomService;
import com.riccardo.giangiulio.gestionescuola.service.LessonService;
import com.riccardo.giangiulio.gestionescuola.service.SchoolClassService;
import com.riccardo.giangiulio.gestionescuola.service.SubjectService;
import com.riccardo.giangiulio.gestionescuola.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/lessons")
@Tag(name = "Lesson Controller", description = "API for the management of lessons")
public class LessonController {
    
    private final LessonService lessonService;
    private final UserService userService;
    private final ClassroomService classroomService;
    private final SchoolClassService schoolClassService;
    private final SubjectService subjectService;
    
    @Autowired
    public LessonController(
            LessonService lessonService,
            UserService userService,
            ClassroomService classroomService,
            SchoolClassService schoolClassService,
            SubjectService subjectService) {
        this.lessonService = lessonService;
        this.userService = userService;
        this.classroomService = classroomService;
        this.schoolClassService = schoolClassService;
        this.subjectService = subjectService;
    }
    
    @Operation(summary = "Get lesson by ID", description = "Returns a lesson based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lesson found", content = @Content(schema = @Schema(implementation = LessonDTO.class))),
        @ApiResponse(responseCode = "404", description = "Lesson not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> getLessonById(
            @Parameter(description = "ID of the lesson to search for") @PathVariable Long id) {
        Lesson lesson = lessonService.findById(id);
        LessonDTO lessonDTO = LessonMapperDTO.toDTO(lesson);
        return ResponseEntity.ok(lessonDTO);
    }
    
    @Operation(summary = "Get all lessons", description = "Returns the list of all lessons")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<LessonDTO>> getAllLessons() {
        List<LessonDTO> lessonDTOs = lessonService.findAll().stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
    
    @Operation(summary = "Create a new lesson", description = "Creates a new lesson in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lesson created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid lesson data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<LessonDTO> createLesson(
            @Parameter(description = "Data of the lesson to create") @RequestBody Lesson lesson) {
        Lesson savedLesson = lessonService.save(lesson);
        LessonDTO lessonDTO = LessonMapperDTO.toDTO(savedLesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonDTO);
    }
    
    @Operation(summary = "Update a lesson", description = "Updates the data of an existing lesson")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lesson updated successfully"),
        @ApiResponse(responseCode = "404", description = "Lesson not found"),
        @ApiResponse(responseCode = "400", description = "Invalid lesson data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<LessonDTO> updateLesson(
            @Parameter(description = "ID of the lesson to update") @PathVariable Long id,
            @Parameter(description = "New data of the lesson") @RequestBody Lesson lesson) {
        Lesson updatedLesson = lessonService.update(id, lesson);
        LessonDTO lessonDTO = LessonMapperDTO.toDTO(updatedLesson);
        return ResponseEntity.ok(lessonDTO);
    }
    
    @Operation(summary = "Delete a lesson", description = "Deletes an existing lesson from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lesson deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Lesson not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLesson(
            @Parameter(description = "ID of the lesson to delete") @PathVariable Long id) {
        lessonService.deleteById(id);
        return ResponseEntity.ok("Lesson with ID " + id + " deleted successfully.");
    }
    
    @Operation(summary = "Find lessons by teacher", description = "Returns the list of lessons taught by a specific teacher")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByTeacher(
            @Parameter(description = "ID of the teacher") @PathVariable Long teacherId) {
        User teacher = userService.findById(teacherId);
        List<LessonDTO> lessonDTOs = lessonService.findByTeacher(teacher).stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
    
    @Operation(summary = "Find lessons by school class", description = "Returns the list of lessons for a specific school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<LessonDTO>> getLessonsBySchoolClass(
            @Parameter(description = "ID of the school class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        List<LessonDTO> lessonDTOs = lessonService.findBySchoolClass(schoolClass).stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
    
    @Operation(summary = "Find lessons by classroom", description = "Returns the list of lessons held in a specific classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByClassroom(
            @Parameter(description = "ID of the classroom") @PathVariable Long classroomId) {
        Classroom classroom = classroomService.findById(classroomId);
        List<LessonDTO> lessonDTOs = lessonService.findByClassroom(classroom).stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
    
    @Operation(summary = "Find lessons by subject", description = "Returns the list of lessons for a specific subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<LessonDTO>> getLessonsBySubject(
            @Parameter(description = "ID of the subject") @PathVariable Long subjectId) {
        Subject subject = subjectService.findById(subjectId);
        List<LessonDTO> lessonDTOs = lessonService.findBySubject(subject).stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
    
    @Operation(summary = "Find lessons in a date range", description = "Returns the list of lessons in a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "400", description = "Invalid date range"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/date-range")
    public ResponseEntity<List<LessonDTO>> getLessonsByDateRange(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<LessonDTO> lessonDTOs = lessonService.findByDateRange(start, end).stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
    
    @Operation(summary = "Find upcoming lessons by school class", description = "Returns the list of upcoming lessons for a specific school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/class/{classId}/upcoming")
    public ResponseEntity<List<LessonDTO>> getUpcomingLessonsBySchoolClass(
            @Parameter(description = "ID of the school class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        List<LessonDTO> lessonDTOs = lessonService.findUpcomingLessonsBySchoolClass(schoolClass).stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
    
    @Operation(summary = "Find today's lessons", description = "Returns the list of lessons scheduled for today")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of lessons found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/today")
    public ResponseEntity<List<LessonDTO>> getTodayLessons() {
        List<LessonDTO> lessonDTOs = lessonService.findTodayLessons().stream()
                .map(LessonMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }
}
