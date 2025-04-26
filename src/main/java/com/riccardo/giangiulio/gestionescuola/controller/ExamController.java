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

import com.riccardo.giangiulio.gestionescuola.dto.mapper.ExamMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.ExamDTO;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.ClassroomService;
import com.riccardo.giangiulio.gestionescuola.service.CourseService;
import com.riccardo.giangiulio.gestionescuola.service.ExamService;
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
@RequestMapping("/api/exams")
@Tag(name = "Exam Controller", description = "API for the management of exams")
public class ExamController {
    
    private final ExamService examService;
    private final ClassroomService classroomService;
    private final SubjectService subjectService;
    private final SchoolClassService schoolClassService;
    private final UserService userService;
    private final CourseService courseService;
    
    @Autowired
    public ExamController(
            ExamService examService,
            ClassroomService classroomService,
            SubjectService subjectService,
            SchoolClassService schoolClassService,
            UserService userService,
            CourseService courseService) {
        this.examService = examService;
        this.classroomService = classroomService;
        this.subjectService = subjectService;
        this.schoolClassService = schoolClassService;
        this.userService = userService;
        this.courseService = courseService;
    }
    
    @Operation(summary = "Get exam by ID", description = "Returns an exam based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam found", content = @Content(schema = @Schema(implementation = ExamDTO.class))),
        @ApiResponse(responseCode = "404", description = "Exam not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<ExamDTO> getExamById(
            @Parameter(description = "ID of the exam to search for") @PathVariable Long id) {
        Exam exam = examService.findById(id);
        ExamDTO examDTO = ExamMapperDTO.toDTO(exam);
        return ResponseEntity.ok(examDTO);
    }
    
    @Operation(summary = "Get all exams", description = "Returns the list of all exams")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public ResponseEntity<List<ExamDTO>> getAllExams() {
        List<ExamDTO> examDTOs = examService.findAll().stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Create a new exam", description = "Creates a new exam in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Exam created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid exam data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<ExamDTO> createExam(
            @Parameter(description = "Data of the exam to create") @RequestBody Exam exam) {
        Exam savedExam = examService.save(exam);
        ExamDTO examDTO = ExamMapperDTO.toDTO(savedExam);
        return ResponseEntity.status(HttpStatus.CREATED).body(examDTO);
    }
    
    @Operation(summary = "Update an exam", description = "Updates the data of an existing exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam updated successfully"),
        @ApiResponse(responseCode = "404", description = "Exam not found"),
        @ApiResponse(responseCode = "400", description = "Invalid exam data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<ExamDTO> updateExam(
            @Parameter(description = "ID of the exam to update") @PathVariable Long id,
            @Parameter(description = "New data of the exam") @RequestBody Exam exam) {
        Exam updatedExam = examService.update(id, exam);
        ExamDTO examDTO = ExamMapperDTO.toDTO(updatedExam);
        return ResponseEntity.ok(examDTO);
    }
    
    @Operation(summary = "Delete an exam", description = "Deletes an existing exam from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Exam not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(
            @Parameter(description = "ID of the exam to delete") @PathVariable Long id) {
        examService.deleteById(id);
        return ResponseEntity.ok("Exam with ID " + id + " deleted successfully.");
    }
    
    @Operation(summary = "Find exams by title", description = "Returns the list of exams with the specified title")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/title")
    public ResponseEntity<ExamDTO> getExamByTitle(
            @Parameter(description = "Title of the exam to search for") @RequestParam String title) {
        Exam exam = examService.findByTitle(title);
        ExamDTO examDTO = ExamMapperDTO.toDTO(exam);
        return ResponseEntity.ok(examDTO);
    }
    
    @Operation(summary = "Find exams by date", description = "Returns the list of exams for a specific date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/date")
    public ResponseEntity<List<ExamDTO>> getExamsByDate(
            @Parameter(description = "Date of the exam to search for") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<ExamDTO> examDTOs = examService.findByDate(date).stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Find exams by subject", description = "Returns the list of exams for a specific subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ExamDTO>> getExamsBySubject(
            @Parameter(description = "ID of the subject") @PathVariable Long subjectId) {
        Subject subject = subjectService.findById(subjectId);
        List<ExamDTO> examDTOs = examService.findBySubject(subject).stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Find exams by school class", description = "Returns the list of exams for a specific school class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "404", description = "School class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ExamDTO>> getExamsBySchoolClass(
            @Parameter(description = "ID of the school class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        List<ExamDTO> examDTOs = examService.findBySchoolClass(schoolClass).stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Find exams by teacher", description = "Returns the list of exams for a specific teacher")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ExamDTO>> getExamsByTeacher(
            @Parameter(description = "ID of the teacher") @PathVariable Long teacherId) {
        User teacher = userService.findById(teacherId);
        List<ExamDTO> examDTOs = examService.findByTeacher(teacher).stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }

    @Operation(summary = "Find exams by classroom", description = "Returns the list of exams for a specific classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<ExamDTO>> getExamsByClassroom(
            @Parameter(description = "ID of the classroom") @PathVariable Long classroomId) {
        Classroom classroom = classroomService.findById(classroomId);
        List<ExamDTO> examDTOs = examService.findByClassroom(classroom).stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Find exams by course", description = "Returns the list of exams for a specific course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ExamDTO>> getExamsByCourse(
            @Parameter(description = "ID of the course") @PathVariable Long courseId) {
        Course course = courseService.findById(courseId);
        List<ExamDTO> examDTOs = examService.findByCourse(course).stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Find exams in a date range", description = "Returns the list of exams in a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "400", description = "Invalid date range"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/date-range")
    public ResponseEntity<List<ExamDTO>> getExamsByDateRange(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ExamDTO> examDTOs = examService.findByDateRange(start, end).stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Find upcoming exams", description = "Returns the list of exams scheduled in the future")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/upcoming")
    public ResponseEntity<List<ExamDTO>> getUpcomingExams() {
        List<ExamDTO> examDTOs = examService.findUpcomingExams().stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
    
    @Operation(summary = "Find past exams", description = "Returns the list of exams already completed")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exams found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/past")
    public ResponseEntity<List<ExamDTO>> getPastExams() {
        List<ExamDTO> examDTOs = examService.findPastExams().stream()
                .map(ExamMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examDTOs);
    }
}
