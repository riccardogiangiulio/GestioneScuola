package com.riccardo.giangiulio.gestionescuola.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.service.ExportCSVService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/export")
@Tag(name = "Export CSV Controller", description = "API for the export of data in CSV format")
public class ExportCSVController {
    
    private static final Logger log = LoggerFactory.getLogger(ExportCSVController.class);
    
    private final ExportCSVService exportCSVService;
    
    @Autowired
    public ExportCSVController(ExportCSVService exportCSVService) {
        this.exportCSVService = exportCSVService;
        log.info("ExportCSVController initialized");
    }
    
    @Operation(summary = "Export students in CSV", description = "Export the list of students in CSV format")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export completed successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient authorization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/students/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportStudentsToCSV(HttpServletResponse response) throws IOException {
        log.info("Request to export students to CSV");
        
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv");
        
        exportCSVService.exportStudentsToCSV(response.getWriter());
        
        log.info("Export of students to CSV completed");
    }
    
    @Operation(summary = "Export teachers in CSV", description = "Export the list of teachers in CSV format")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export completed successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient authorization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/teachers/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportTeachersToCSV(HttpServletResponse response) throws IOException {
        log.info("Request to export teachers to CSV");
        
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teachers.csv");
        
        exportCSVService.exportTeachersToCSV(response.getWriter());
        
        log.info("Export of teachers to CSV completed");
    }
    
    @Operation(summary = "Export registrations in CSV", description = "Export the list of registrations in CSV format")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export completed successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient authorization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/registrations/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportRegistrationsToCSV(HttpServletResponse response) throws IOException {
        log.info("Request to export registrations to CSV");
        
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registrations.csv");
        
        exportCSVService.exportRegistrationsToCSV(response.getWriter());
        
        log.info("Export of registrations to CSV completed");
    }
}
