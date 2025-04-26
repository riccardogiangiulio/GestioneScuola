package com.riccardo.giangiulio.gestionescuola.service;

import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.User;

@Service
public class ExportCSVService {
    private static final Logger log = LoggerFactory.getLogger(ExportCSVService.class);
    
    private final UserService userService;
    private final RegistrationService registrationService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Autowired
    public ExportCSVService(UserService userService, RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
        log.info("ExportCSVService inizialized");
    }
    
    public void exportStudentsToCSV(Writer writer) throws IOException {
        log.info("Starting export of students to CSV");
        List<User> students = userService.findByRole(ERole.ROLE_STUDENT);
        
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Name", "Surname", "Email", "Username", "Birth Date")
                .build())) {
            
            for (User student : students) {
                csvPrinter.printRecord(
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getUsername(),
                    student.getBirthDate() != null ? student.getBirthDate().format(DATE_FORMATTER) : ""
                );
            }
            
            log.info("Exported {} students to CSV", students.size());
        } catch (IOException e) {
            log.error("Error during export of students to CSV", e);
            throw e;
        }
    }
    
    public void exportTeachersToCSV(Writer writer) throws IOException {
        log.info("Starting export of teachers to CSV");
        List<User> teachers = userService.findByRole(ERole.ROLE_TEACHER);
        
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Name", "Surname", "Email", "Username", "Birth Date")
                .build())) {
            
            for (User teacher : teachers) {
                csvPrinter.printRecord(
                    teacher.getId(),
                    teacher.getFirstName(),
                    teacher.getLastName(),
                    teacher.getEmail(),
                    teacher.getUsername(),
                    teacher.getBirthDate() != null ? teacher.getBirthDate().format(DATE_FORMATTER) : ""
                );
            }
            
            log.info("Exported {} teachers to CSV", teachers.size());
        } catch (IOException e) {
            log.error("Error during export of teachers to CSV", e);
            throw e;
        }
    }
    
    public void exportRegistrationsToCSV(Writer writer) throws IOException {
        log.info("Starting export of registrations to CSV");
        List<Registration> registrations = registrationService.findAll();
        
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Student", "Student Email", "Course", "Class", "Registration Date", "Status")
                .build())) {
            
            for (Registration registration : registrations) {
                csvPrinter.printRecord(
                    registration.getId(),
                    registration.getStudent().getFirstName() + " " + registration.getStudent().getLastName(),
                    registration.getStudent().getEmail(),
                    registration.getCourse().getTitle(),
                    registration.getSchoolClass() != null ? registration.getSchoolClass().getName() : "",
                    registration.getRegistrationDate() != null ? 
                        registration.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                    registration.getStatus().toString()
                );
            }
            
            log.info("Exported {} registrations to CSV", registrations.size());
        } catch (IOException e) {
            log.error("Error during export of registrations to CSV", e);
            throw e;
        }
    }
}
