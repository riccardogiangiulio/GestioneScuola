package com.riccardo.giangiulio.gestionescuola.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.riccardo.giangiulio.gestionescuola.model.Attendance;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.User;

@Service
public class ExportPDFService {
    
    private static final Logger log = LoggerFactory.getLogger(ExportPDFService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final UserService userService;
    private final RegistrationService registrationService;
    private final ExamResultService examResultService;
    private final AttendanceService attendanceService;
    
    @Autowired
    public ExportPDFService(
            UserService userService,
            RegistrationService registrationService,
            ExamResultService examResultService,
            AttendanceService attendanceService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.examResultService = examResultService;
        this.attendanceService = attendanceService;
        log.info("ExportPDFService initialized");
    }
    
    public byte[] generateStudentProfilePDF(Long studentId) throws IOException {
        log.info("Generating PDF profile for student with ID: {}", studentId);
        
        // Retrieve student data
        User student = userService.findById(studentId);
        List<Registration> registrations = registrationService.findByStudent(student);
        List<ExamResult> examResults = examResultService.findByStudent(student);
        List<Attendance> attendances = attendanceService.findByStudent(student);
        
        // Statistics
        Double averageScore = examResultService.findAverageScoreByStudent(student);
        Long passedExams = examResultService.countPassedByStudent(student);
        Long presentDays = attendanceService.countPresentByStudent(student);
        Long absentDays = attendanceService.countAbsentByStudent(student);
        
        // Create PDF document
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        try {
            // Title
            Paragraph title = new Paragraph("Student Profile")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(0, 75, 155));
            document.add(title);
            
            // Personal information
            document.add(new Paragraph("\nPersonal Information")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 75, 155)));
            
            Table infoTable = new Table(2).useAllAvailableWidth();
            
            addTableRow(infoTable, "ID:", student.getId().toString());
            addTableRow(infoTable, "First Name:", student.getFirstName());
            addTableRow(infoTable, "Last Name:", student.getLastName());
            addTableRow(infoTable, "Email:", student.getEmail());
            addTableRow(infoTable, "Username:", student.getUsername());
            addTableRow(infoTable, "Birth Date:", student.getBirthDate() != null ? 
                    student.getBirthDate().format(DATE_FORMATTER) : "N/A");
            
            document.add(infoTable);
            
            // General statistics
            document.add(new Paragraph("\nStatistics")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 75, 155)));
            
            Table statsTable = new Table(2).useAllAvailableWidth();
            
            addTableRow(statsTable, "Average Score:", averageScore != null ? 
                    String.format("%.2f", averageScore) : "N/A");
            addTableRow(statsTable, "Passed Exams:", passedExams != null ? 
                    passedExams.toString() : "0");
            addTableRow(statsTable, "Present Days:", presentDays != null ? 
                    presentDays.toString() : "0");
            addTableRow(statsTable, "Absent Days:", absentDays != null ? 
                    absentDays.toString() : "0");
            
            document.add(statsTable);
            
            // Registrations
            document.add(new Paragraph("\nRegistrations")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 75, 155)));
            
            if (registrations.isEmpty()) {
                document.add(new Paragraph("No registrations found.").setItalic());
            } else {
                Table regTable = new Table(5).useAllAvailableWidth();
                
                // Table header for registrations
                addTableHeader(regTable, "ID", "Course", "Class", "Registration Date", "Status");
                
                // Table rows for registrations
                for (Registration reg : registrations) {
                    regTable.addCell(new Cell().add(new Paragraph(reg.getId().toString())));
                    regTable.addCell(new Cell().add(new Paragraph(reg.getCourse().getTitle())));
                    regTable.addCell(new Cell().add(new Paragraph(reg.getSchoolClass() != null ? 
                            reg.getSchoolClass().getName() : "N/A")));
                    regTable.addCell(new Cell().add(new Paragraph(reg.getRegistrationDate() != null ? 
                            reg.getRegistrationDate().format(DATE_TIME_FORMATTER) : "N/A")));
                    regTable.addCell(new Cell().add(new Paragraph(reg.getStatus().toString())));
                }
                
                document.add(regTable);
            }
            
            // Exam results
            document.add(new Paragraph("\nExam Results")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 75, 155)));
            
            if (examResults.isEmpty()) {
                document.add(new Paragraph("No exam results found.").setItalic());
            } else {
                Table examTable = new Table(4).useAllAvailableWidth();
                
                // Table header for results
                addTableHeader(examTable, "ID", "Exam", "Score", "Date");
                
                // Table rows for results
                for (ExamResult result : examResults) {
                    examTable.addCell(new Cell().add(new Paragraph(result.getId().toString())));
                    examTable.addCell(new Cell().add(new Paragraph(result.getExam().getTitle())));
                    
                    Cell scoreCell = new Cell().add(new Paragraph(String.format("%.1f", result.getScore())));
                    boolean isPassed = result.getScore() >= result.getExam().getPassingScore();
                    if (isPassed) {
                        scoreCell.setFontColor(ColorConstants.GREEN);
                    } else {
                        scoreCell.setFontColor(ColorConstants.RED);
                    }
                    examTable.addCell(scoreCell);
                    
                    examTable.addCell(new Cell().add(new Paragraph(result.getDate() != null ? 
                            result.getDate().format(DATE_TIME_FORMATTER) : "N/A")));
                }
                
                document.add(examTable);
            }
            
            // Attendances (last 10)
            document.add(new Paragraph("\nAttendance at Lessons (last 10)")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 75, 155)));
            
            if (attendances.isEmpty()) {
                document.add(new Paragraph("No attendance/absence registered.").setItalic());
            } else {
                Table attTable = new Table(5).useAllAvailableWidth();
                
                // Table header for attendances
                addTableHeader(attTable, "ID", "Lesson", "Date", "Entry", "Exit");
                
                // Table rows for attendances (limited to 10)
                int limit = Math.min(10, attendances.size());
                for (int i = 0; i < limit; i++) {
                    Attendance att = attendances.get(i);
                    attTable.addCell(new Cell().add(new Paragraph(att.getId().toString())));
                    
                    Cell lessonCell = new Cell().add(new Paragraph(att.getLesson().getTitle()));
                    if (!att.getPresent()) {
                        lessonCell.setFontColor(ColorConstants.RED);
                        lessonCell.add(new Paragraph(" (Absent)").setFontColor(ColorConstants.RED).setItalic());
                    }
                    attTable.addCell(lessonCell);
                    
                    attTable.addCell(new Cell().add(new Paragraph(att.getLesson().getStartDateTime() != null ? 
                            att.getLesson().getStartDateTime().format(DATE_FORMATTER) : "N/A")));
                    attTable.addCell(new Cell().add(new Paragraph(att.getEntryTime() != null ? 
                            att.getEntryTime().format(DATE_TIME_FORMATTER) : "N/A")));
                    attTable.addCell(new Cell().add(new Paragraph(att.getExitTime() != null ? 
                            att.getExitTime().format(DATE_TIME_FORMATTER) : "N/A")));
                }
                
                document.add(attTable);
            }
            
            // Footer
            document.add(new Paragraph("\nDocument generated on " + 
                    java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER))
                    .setFontSize(8)
                    .setItalic()
                    .setTextAlignment(TextAlignment.RIGHT));
            
            // Important: close and flush to ensure the PDF is correctly written
            document.close();
            writer.flush();
            
            log.info("Student profile PDF generation completed");
            return baos.toByteArray();
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            log.error("Error generating PDF: ", e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Unexpected error generating PDF: ", e);
            throw new IOException("Error generating PDF document", e);
        }
    }
    
    private void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setBold())
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setBackgroundColor(new DeviceRgb(240, 240, 240));
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(new DeviceRgb(220, 220, 220))
                    .setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
        }
    }
}
