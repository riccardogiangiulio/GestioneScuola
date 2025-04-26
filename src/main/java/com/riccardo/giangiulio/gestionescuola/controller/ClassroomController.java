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

import com.riccardo.giangiulio.gestionescuola.dto.mapper.ClassroomMapperDTO;
import com.riccardo.giangiulio.gestionescuola.dto.model.ClassroomDTO;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.service.ClassroomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/classrooms")
@Tag(name = "Classroom Controller", description = "API for the management of classrooms")
public class ClassroomController {
    
    private final ClassroomService classroomService;
    
    @Autowired
    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }
    
    @Operation(summary = "Get classroom by ID", description = "Returns a classroom based on the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classroom found", content = @Content(schema = @Schema(implementation = ClassroomDTO.class))),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> getClassroomById(
            @Parameter(description = "ID of the classroom to search for") @PathVariable Long id) {
        Classroom classroom = classroomService.findById(id);
        ClassroomDTO classroomDTO = ClassroomMapperDTO.toDTO(classroom);
        return ResponseEntity.ok(classroomDTO);
    }
    
    @Operation(summary = "Get all classrooms", description = "Returns the list of all classrooms")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classrooms found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> getAllClassrooms() {
        List<ClassroomDTO> classroomDTOs = classroomService.findAll().stream()
                .map(ClassroomMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(classroomDTOs);
    }
    
    @Operation(summary = "Create a new classroom", description = "Creates a new classroom in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Classroom created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid classroom data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Classroom> createClassroom(
            @Parameter(description = "Data of the classroom to create") @RequestBody Classroom classroom) {
        Classroom savedClassroom = classroomService.save(classroom);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClassroom);
    }
    
    @Operation(summary = "Update a classroom", description = "Updates the data of an existing classroom")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classroom updated successfully"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "400", description = "Invalid classroom data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(
            @Parameter(description = "ID of the classroom to update") @PathVariable Long id,
            @Parameter(description = "New data of the classroom") @RequestBody Classroom classroom) {
        Classroom updatedClassroom = classroomService.update(id, classroom);
        return ResponseEntity.ok(updatedClassroom);
    }
    
    @Operation(summary = "Delete a classroom", description = "Deletes an existing classroom from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classroom deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassroom(
            @Parameter(description = "ID of the classroom to delete") @PathVariable Long id) {
        classroomService.deleteById(id);
        return ResponseEntity.ok("Classroom with ID " + id + " deleted successfully.");
    }
    
    @Operation(summary = "Get classrooms by name", description = "Returns the list of classrooms with the specified name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classrooms found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/search/name")
    public ResponseEntity<ClassroomDTO> getClassroomsByName(
            @Parameter(description = "Name of the classrooms to search for") @RequestParam String name) {
        Classroom classroom = classroomService.findByName(name);
        ClassroomDTO classroomDTO = ClassroomMapperDTO.toDTO(classroom);
        return ResponseEntity.ok(classroomDTO);
    }
    
    @Operation(summary = "Get classrooms by minimum capacity", description = "Returns the list of classrooms with at least the specified capacity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of classrooms found"),
        @ApiResponse(responseCode = "400", description = "Invalid capacity value"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/search/capacity")
    public ResponseEntity<List<ClassroomDTO>> getClassroomsByMinCapacity(
            @Parameter(description = "Minimum capacity of the classrooms") @RequestParam Integer minCapacity) {
        List<ClassroomDTO> classroomDTOs = classroomService.findByMinCapacity(minCapacity).stream()
                .map(ClassroomMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(classroomDTOs);
    }
    
    @Operation(summary = "Get available classrooms in a time range", description = "Returns the list of classrooms available in the specified time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of available classrooms found"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/available")
    public ResponseEntity<List<ClassroomDTO>> getAvailableClassroomsInTimeRange(
            @Parameter(description = "Start time of the range") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time of the range") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ClassroomDTO> classroomDTOs = classroomService.findAvailableInTimeRange(start, end).stream()
                .map(ClassroomMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(classroomDTOs);
    }
    
    @Operation(summary = "Get classrooms with sufficient capacity for a school class", 
               description = "Returns the list of classrooms with enough capacity for all students in a specific class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of suitable classrooms found"),
        @ApiResponse(responseCode = "404", description = "School class not found"),
        @ApiResponse(responseCode = "400", description = "No classrooms with sufficient capacity"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/suitable-for-class/{schoolClassId}")
    public ResponseEntity<List<ClassroomDTO>> getClassroomsWithSufficientCapacityForSchoolClass(
            @Parameter(description = "ID of the school class") @PathVariable Long schoolClassId) {
        List<ClassroomDTO> classroomDTOs = classroomService.findWithSufficientCapacityForSchoolClass(schoolClassId).stream()
                .map(ClassroomMapperDTO::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(classroomDTOs);
    }
    
    @Operation(summary = "Check if a classroom is available for a time slot", 
               description = "Checks if the specified classroom is available during the given time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability status returned"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "400", description = "Invalid time range"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{classroomId}/available")
    public ResponseEntity<Boolean> isClassroomAvailableForTimeSlot(
            @Parameter(description = "ID of the classroom") @PathVariable Long classroomId,
            @Parameter(description = "Start time of the slot") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End time of the slot") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Boolean isAvailable = classroomService.isAvailableForTimeSlot(classroomId, start, end);
        return ResponseEntity.ok(isAvailable);
    }
    
    @Operation(summary = "Check if a classroom has sufficient capacity", 
               description = "Checks if the specified classroom has enough capacity for the required number of people")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capacity status returned"),
        @ApiResponse(responseCode = "404", description = "Classroom not found"),
        @ApiResponse(responseCode = "400", description = "Invalid capacity requirement"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{classroomId}/capacity-check")
    public ResponseEntity<Boolean> hasClassroomSufficientCapacity(
            @Parameter(description = "ID of the classroom") @PathVariable Long classroomId,
            @Parameter(description = "Required capacity") @RequestParam Integer requiredCapacity) {
        Boolean hasSufficientCapacity = classroomService.hasSufficientCapacity(classroomId, requiredCapacity);
        return ResponseEntity.ok(hasSufficientCapacity);
    }
}
