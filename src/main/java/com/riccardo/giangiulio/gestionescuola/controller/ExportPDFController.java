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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riccardo.giangiulio.gestionescuola.service.ExportPDFService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/export")
@Tag(name = "Export PDF Controller", description = "API for the export of data in PDF format")
public class ExportPDFController {
    
    private static final Logger log = LoggerFactory.getLogger(ExportPDFController.class);
    
    private final ExportPDFService exportPDFService;
    
    @Autowired
    public ExportPDFController(ExportPDFService exportPDFService) {
        this.exportPDFService = exportPDFService;
        log.info("ExportPDFController initialized");
    }

    @Operation(summary = "Export student profile in PDF", description = "Generate a PDF document with the details of a specific student's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF generated successfully", 
                     content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient authorization"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/student/{id}/profile/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportStudentProfilePDF(
            @Parameter(description = "ID of the student") @PathVariable Long id) throws IOException {
        log.info("Request to export student profile with ID {} in PDF", id);
        
        byte[] pdfBytes = exportPDFService.generateStudentProfilePDF(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "student_profile_" + id + ".pdf");
        

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
