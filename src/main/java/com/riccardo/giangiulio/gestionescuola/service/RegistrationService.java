package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.RegistrationRepository;

@Service
public class RegistrationService {
    
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    
    private final RegistrationRepository registrationRepository;
    private final UserService userService;
    private final SchoolClassService schoolClassService;
    
    @Autowired
    public RegistrationService(
            RegistrationRepository registrationRepository,
            UserService userService,
            SchoolClassService schoolClassService) {
        this.registrationRepository = registrationRepository;
        this.userService = userService;
        this.schoolClassService = schoolClassService;
        log.info("RegistrationService initialized");
    }
    
    public List<Registration> findAll() {
        log.debug("Retrieving all registrations");
        return registrationRepository.findAll();
    }
    
    public Registration findById(Long id) {
        log.debug("Finding registration with id: {}", id);
        return registrationRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Registration not found with ID: {}", id);
                return new RuntimeException("Registration not found with ID: " + id);
            });
    }
    
    @Transactional
    public Registration save(Registration registration) {
        log.debug("Saving registration for student {} and school class {}", 
            registration.getStudent().getId(), registration.getSchoolClass().getId());
        
        validateRegistration(registration);
        Registration savedRegistration = registrationRepository.save(registration);
        log.info("Registration saved successfully with ID: {}", savedRegistration.getId());
        return savedRegistration;
    }
    
    @Transactional
    public Registration update(Long id, Registration registration) {
        log.debug("Updating registration with id: {}", id);
        Registration existingRegistration = findById(id);
        
        existingRegistration.setStatus(registration.getStatus());
        existingRegistration.setSchoolClass(registration.getSchoolClass());
        
        validateRegistration(existingRegistration);
        Registration updatedRegistration = registrationRepository.save(existingRegistration);
        log.info("Registration updated successfully with ID: {}", id);
        return updatedRegistration;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete registration with id: {}", id);
        if (!registrationRepository.existsById(id)) {
            log.error("Failed to delete registration: Not found with ID: {}", id);
            throw new RuntimeException("Registration not found with ID: " + id);
        }
        registrationRepository.deleteById(id);
        log.info("Registration deleted successfully with ID: {}", id);
    }
    
    public List<Registration> findByStudent(User student) {
        log.debug("Finding registrations for student id: {}", student.getId());
        List<Registration> registrations = registrationRepository.findByStudent(student);
        if (registrations.isEmpty()) {
            log.warn("No registrations found for student ID: {}", student.getId());
        }
        return registrations;
    }
    
    public List<Registration> findBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding registrations for school class id: {}", schoolClass.getId());
        List<Registration> registrations = registrationRepository.findBySchoolClass(schoolClass);
        if (registrations.isEmpty()) {
            log.warn("No registrations found for school class ID: {}", schoolClass.getId());
        }
        return registrations;
    }
    
    public List<Registration> findByStatus(RegistrationStatus status) {
        log.debug("Finding registrations with status: {}", status);
        List<Registration> registrations = registrationRepository.findByStatus(status);
        if (registrations.isEmpty()) {
            log.warn("No registrations found with status: {}", status);
        }
        return registrations;
    }
    
    public Registration findByStudentAndSchoolClass(User student, SchoolClass schoolClass) {
        log.debug("Finding registration for student {} and school class {}", student.getId(), schoolClass.getId());
        return registrationRepository.findByStudentAndSchoolClass(student, schoolClass)
            .orElseThrow(() -> {
                log.error("Registration not found for student {} and school class {}", 
                    student.getId(), schoolClass.getId());
                return new RuntimeException("Registration not found for this student and school class");
            });
    }
    
    public Long countActiveRegistrationsBySchoolClass(SchoolClass schoolClass) {
        log.debug("Counting active registrations for school class id: {}", schoolClass.getId());
        return registrationRepository.countActiveRegistrationsBySchoolClass(schoolClass);
    }
    
    public List<Registration> findByCourseId(Long courseId) {
        log.debug("Finding registrations for course id: {}", courseId);
        List<Registration> registrations = registrationRepository.findByCourseId(courseId);
        if (registrations.isEmpty()) {
            log.warn("No registrations found for course ID: {}", courseId);
        }
        return registrations;
    }
    
    public List<Registration> findActiveByStudent(User student) {
        log.debug("Finding active registrations for student id: {}", student.getId());
        List<Registration> registrations = registrationRepository.findActiveByStudent(student);
        if (registrations.isEmpty()) {
            log.warn("No active registrations found for student ID: {}", student.getId());
        }
        return registrations;
    }
    
    public List<Registration> findActiveBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding active registrations for school class id: {}", schoolClass.getId());
        List<Registration> registrations = registrationRepository.findActiveBySchoolClass(schoolClass);
        if (registrations.isEmpty()) {
            log.warn("No active registrations found for school class ID: {}", schoolClass.getId());
        }
        return registrations;
    }
    
    private void validateRegistration(Registration registration) {
        if (!userService.isStudent(registration.getStudent())) {
            log.error("Cannot save registration: user {} is not a student", registration.getStudent().getId());
            throw new RuntimeException("The specified user is not a student");
        }
        
        SchoolClass schoolClass = schoolClassService.findById(registration.getSchoolClass().getId());
        if (schoolClassService.isFull(schoolClass.getId())) {
            log.error("Cannot save registration: school class {} is full", schoolClass.getId());
            throw new RuntimeException("The school class is full");
        }
        
        Optional<Registration> existingRegistration = registrationRepository
            .findByStudentAndSchoolClass(registration.getStudent(), schoolClass);
        if (existingRegistration.isPresent() && 
            existingRegistration.get().getStatus() == RegistrationStatus.ACTIVE) {
            log.error("Cannot save registration: student {} is already registered in class {}", 
                registration.getStudent().getId(), schoolClass.getId());
            throw new RuntimeException("The student is already registered in this class");
        }
    }
}
