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

import com.riccardo.giangiulio.gestionescuola.dto.mapper.AttendanceMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.AttendanceDTO;
import com.riccardo.giangiulio.gestionescuola.model.Attendance;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.service.AttendanceService;
import com.riccardo.giangiulio.gestionescuola.service.LessonService;
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
@RequestMapping("/api/attendances")
@Tag(name = "Attendance Controller", description = "API for the management of attendances")
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final LessonService lessonService;
    private final SchoolClassService schoolClassService;
    
    @Autowired
    public AttendanceController(
            AttendanceService attendanceService,
            UserService userService,
            LessonService lessonService,
            SchoolClassService schoolClassService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
        this.lessonService = lessonService;
        this.schoolClassService = schoolClassService;
    }
    
    @Operation(summary = "Get attendance by ID", description = "Returns an attendance based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance found", content = @Content(schema = @Schema(implementation = AttendanceDTO.class))),
        @ApiResponse(responseCode = "404", description = "Attendance not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(
            @Parameter(description = "ID of the attendance to search for") @PathVariable Long id) {
        Attendance attendance = attendanceService.findById(id);
        AttendanceDTO attendanceDTO = AttendanceMapperDTO.toDTO(attendance);
        return ResponseEntity.ok(attendanceDTO);
    }
    
    @Operation(summary = "Get all attendances", description = "Returns the list of all attendances")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of attendances found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendances() {
        List<AttendanceDTO> attendanceDTOs = attendanceService.findAll().stream()
                .map(AttendanceMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(attendanceDTOs);
    }
    
    @Operation(summary = "Create a new attendance", description = "Creates a new attendance record in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Attendance created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid attendance data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<AttendanceDTO> createAttendance(@RequestBody Attendance attendance) {
        Attendance savedAttendance = attendanceService.save(attendance);
        AttendanceDTO attendanceDTO = AttendanceMapperDTO.toDTO(savedAttendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceDTO);
    }
    
    @Operation(summary = "Update an attendance", description = "Updates the data of an existing attendance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance updated successfully"),
        @ApiResponse(responseCode = "404", description = "Attendance not found"),
        @ApiResponse(responseCode = "400", description = "Invalid attendance data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(
            @Parameter(description = "ID of the attendance to update") @PathVariable Long id,
            @Parameter(description = "New data of the attendance") @RequestBody Attendance attendance) {
        Attendance updatedAttendance = attendanceService.update(id, attendance);
        AttendanceDTO attendanceDTO = AttendanceMapperDTO.toDTO(updatedAttendance);
        return ResponseEntity.ok(attendanceDTO);
    }
    
    @Operation(summary = "Delete an attendance", description = "Deletes an existing attendance from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Attendance not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttendance(
            @Parameter(description = "ID of the attendance to delete") @PathVariable Long id) {
        attendanceService.deleteById(id);
        return ResponseEntity.ok("Attendance with ID " + id + " deleted successfully.");
    }
    
    @Operation(summary = "Get attendances by lesson", description = "Returns the list of attendances for a specific lesson")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of attendances found"),
        @ApiResponse(responseCode = "404", description = "Lesson not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByLesson(
            @Parameter(description = "ID of the lesson") @PathVariable Long lessonId) {
        Lesson lesson = lessonService.findById(lessonId);
        List<AttendanceDTO> attendanceDTOs = attendanceService.findByLesson(lesson).stream()
                .map(AttendanceMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(attendanceDTOs);
    }
    
    @Operation(summary = "Get attendances by student", description = "Returns the list of attendances for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of attendances found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        List<AttendanceDTO> attendanceDTOs = attendanceService.findByStudent(student).stream()
                .map(AttendanceMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(attendanceDTOs);
    }
    
    @Operation(summary = "Get present attendances", description = "Returns the list of attendances marked as present")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of attendances found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/present")
    public ResponseEntity<List<AttendanceDTO>> getPresentAttendances() {
        List<AttendanceDTO> attendanceDTOs = attendanceService.findByPresent(true).stream()
                .map(AttendanceMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(attendanceDTOs);
    }
    
    @Operation(summary = "Get absent attendances", description = "Returns the list of attendances marked as absent")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of attendances found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/absent")
    public ResponseEntity<List<AttendanceDTO>> getAbsentAttendances() {
        List<AttendanceDTO> attendanceDTOs = attendanceService.findByPresent(false).stream()
                .map(AttendanceMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(attendanceDTOs);
    }
    
    @Operation(summary = "Get attendances by class", description = "Returns the list of attendances for a specific class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of attendances found"),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByClass(
            @Parameter(description = "ID of the class") @PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.findById(classId);
        List<AttendanceDTO> attendanceDTOs = attendanceService.findBySchoolClass(schoolClass).stream()
                .map(AttendanceMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(attendanceDTOs);
    }
    
    @Operation(summary = "Count present attendances by student", description = "Returns the count of present attendances for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count of attendances found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/count-present")
    public ResponseEntity<Long> countPresentByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        Long count = attendanceService.countPresentByStudent(student);
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Count absent attendances by student", description = "Returns the count of absent attendances for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count of attendances found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/student/{studentId}/count-absent")
    public ResponseEntity<Long> countAbsentByStudent(
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        User student = userService.findById(studentId);
        Long count = attendanceService.countAbsentByStudent(student);
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Get attendance by lesson and student", description = "Returns an attendance for a specific lesson and student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance found"),
        @ApiResponse(responseCode = "404", description = "Attendance not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/lesson/{lessonId}/student/{studentId}")
    public ResponseEntity<AttendanceDTO> getAttendanceByLessonAndStudent(
            @Parameter(description = "ID of the lesson") @PathVariable Long lessonId,
            @Parameter(description = "ID of the student") @PathVariable Long studentId) {
        Lesson lesson = lessonService.findById(lessonId);
        User student = userService.findById(studentId);
        Attendance attendance = attendanceService.findByLessonAndStudent(lesson, student);
        AttendanceDTO attendanceDTO = AttendanceMapperDTO.toDTO(attendance);
        return ResponseEntity.ok(attendanceDTO);
    }
}
