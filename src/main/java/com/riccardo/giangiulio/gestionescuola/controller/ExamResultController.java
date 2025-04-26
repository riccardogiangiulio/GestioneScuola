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
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.dto.mapper.ExamResultMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.ExamResultDTO;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.ExamResultService;
import com.riccardo.giangiulio.gestionescuola.service.ExamService;
import com.riccardo.giangiulio.gestionescuola.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/exam-results")
@Tag(name = "Exam Result Controller", description = "API for the management of exam results")
public class ExamResultController {
    
    private final ExamResultService examResultService;
    private final ExamService examService;
    private final UserService userService;
    
    @Autowired
    public ExamResultController(
            ExamResultService examResultService,
            ExamService examService,
            UserService userService) {
        this.examResultService = examResultService;
        this.examService = examService;
        this.userService = userService;
    }
    
    @Operation(summary = "Get exam result by ID", description = "Returns an exam result based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam result found", content = @Content(schema = @Schema(implementation = ExamResultDTO.class))),
        @ApiResponse(responseCode = "404", description = "Exam result not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ExamResultDTO> getExamResultById(
            @Parameter(description = "ID of the exam result to search for") @PathVariable Long id) {
        ExamResult examResult = examResultService.findById(id);
        ExamResultDTO examResultDTO = ExamResultMapperDTO.toDTO(examResult);
        return ResponseEntity.ok(examResultDTO);
    }
    
    @Operation(summary = "Get all exam results", description = "Returns the list of all exam results")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exam results found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ExamResultDTO>> getAllExamResults() {
        List<ExamResultDTO> examResultDTOs = examResultService.findAll().stream()
                .map(ExamResultMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examResultDTOs);
    }
    
    @Operation(summary = "Create a new exam result", description = "Creates a new exam result in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Exam result created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid exam result data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<ExamResultDTO> createExamResult(
            @Parameter(description = "Data of the exam result to create") @RequestBody ExamResult examResult) {
        ExamResult savedExamResult = examResultService.save(examResult);
        ExamResultDTO examResultDTO = ExamResultMapperDTO.toDTO(savedExamResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(examResultDTO);
    }
    
    @Operation(summary = "Update an exam result", description = "Updates the data of an existing exam result")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam result updated successfully"),
        @ApiResponse(responseCode = "404", description = "Exam result not found"),
        @ApiResponse(responseCode = "400", description = "Invalid exam result data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<ExamResultDTO> updateExamResult(
            @Parameter(description = "ID of the exam result to update") @PathVariable Long id,
            @Parameter(description = "New data of the exam result") @RequestBody ExamResult examResult) {
        ExamResult updatedExamResult = examResultService.update(id, examResult);
        ExamResultDTO examResultDTO = ExamResultMapperDTO.toDTO(updatedExamResult);
        return ResponseEntity.ok(examResultDTO);
    }
    
    @Operation(summary = "Delete an exam result", description = "Deletes an existing exam result from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam result deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Exam result not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExamResult(
            @Parameter(description = "ID of the exam result to delete") @PathVariable Long id) {
        examResultService.deleteById(id);
        return ResponseEntity.ok("Exam result with ID " + id + " deleted successfully.");
    }
    
    @Operation(summary = "Get exam results by exam", description = "Returns the list of exam results for a specific exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exam results found"),
        @ApiResponse(responseCode = "404", description = "Exam not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamResultDTO>> getExamResultsByExam(
            @Parameter(description = "ID of the exam") @PathVariable Long examId) {
        Exam exam = examService.findById(examId);
        List<ExamResultDTO> examResultDTOs = examResultService.findByExam(exam).stream()
                .map(ExamResultMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examResultDTOs);
    }
    
    @Operation(summary = "Get exam results by student", description = "Returns the list of exam results for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of exam results found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ExamResultDTO>> getExamResultsByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        List<ExamResultDTO> examResultDTOs = examResultService.findByStudent(student).stream()
                .map(ExamResultMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examResultDTOs);
    }
    
    @Operation(summary = "Get exam result by exam and student", description = "Returns an exam result for a specific exam and student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam result found"),
        @ApiResponse(responseCode = "404", description = "Exam result not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/exam/{examId}/student/{studentId}")
    public ResponseEntity<ExamResultDTO> getExamResultByExamAndStudent(
            @Parameter(description = "ID of the exam") @PathVariable Long examId,
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        Exam exam = examService.findById(examId);
        User student = userService.findById(studentId);
        ExamResult examResult = examResultService.findByExamAndStudent(exam, student);
        ExamResultDTO examResultDTO = ExamResultMapperDTO.toDTO(examResult);
        return ResponseEntity.ok(examResultDTO);
    }
    
    @Operation(summary = "Get all passed exam results", description = "Returns the list of exam results with passing scores")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of passed exam results found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/passed")
    public ResponseEntity<List<ExamResultDTO>> getAllPassedExamResults() {
        List<ExamResultDTO> examResultDTOs = examResultService.findAllPassed().stream()
                .map(ExamResultMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examResultDTOs);
    }
    
    @Operation(summary = "Get all failed exam results", description = "Returns the list of exam results with failing scores")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of failed exam results found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/failed")
    public ResponseEntity<List<ExamResultDTO>> getAllFailedExamResults() {
        List<ExamResultDTO> examResultDTOs = examResultService.findAllFailed().stream()
                .map(ExamResultMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(examResultDTOs);
    }
    
    @Operation(summary = "Get average score by exam", description = "Returns the average score for a specific exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Average score found"),
        @ApiResponse(responseCode = "404", description = "Exam not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/exam/{examId}/average")
    public ResponseEntity<Double> getAverageScoreByExam(
            @Parameter(description = "ID of the exam") @PathVariable Long examId) {
        Exam exam = examService.findById(examId);
        Double averageScore = examResultService.findAverageScoreByExam(exam);
        return ResponseEntity.ok(averageScore != null ? averageScore : 0.0);
    }
    
    @Operation(summary = "Get average score by student", description = "Returns the average score for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Average score found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<Double> getAverageScoreByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        Double averageScore = examResultService.findAverageScoreByStudent(student);
        return ResponseEntity.ok(averageScore != null ? averageScore : 0.0);
    }
    
    @Operation(summary = "Count passed students by exam", description = "Returns the count of students who passed a specific exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count of passed students found"),
        @ApiResponse(responseCode = "404", description = "Exam not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/exam/{examId}/count-passed")
    public ResponseEntity<Long> countPassedByExam(
            @Parameter(description = "ID of the exam") @PathVariable Long examId) {
        Exam exam = examService.findById(examId);
        Long count = examResultService.countPassedByExam(exam);
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Count passed exams by student", description = "Returns the count of exams passed by a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count of passed exams found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/count-passed")
    public ResponseEntity<Long> countPassedByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        Long count = examResultService.countPassedByStudent(student);
        return ResponseEntity.ok(count);
    }
}
