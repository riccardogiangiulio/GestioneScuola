package com.riccardo.giangiulio.gestionescuola.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.service.ExportExcelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/export")
@Tag(name = "Export Excel Controller", description = "API for the export of data in Excel format")
public class ExportExcelController {
    
    private static final Logger log = LoggerFactory.getLogger(ExportExcelController.class);

    private final ExportExcelService exportExcelService;

    @Autowired
    public ExportExcelController(ExportExcelService exportExcelService) {
        this.exportExcelService = exportExcelService;
        log.info("ExportController initialized");
    }

    @Operation(summary = "Export courses and exams in Excel", description = "Generate an Excel sheet containing the data related to courses and their exams")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Excel generated successfully", 
                     content = @io.swagger.v3.oas.annotations.media.Content(
                         mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient authorization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/courses/exams/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportExamsOfCoursesToExcel() throws IOException {
        log.info("Request to export exams of courses to Excel");
        
        byte[] excelBytes = exportExcelService.generateCoursesAndExamsExcel();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("filename", "courses_and_exams.xlsx");
        
        log.info("Courses and exams Excel export completed");
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
