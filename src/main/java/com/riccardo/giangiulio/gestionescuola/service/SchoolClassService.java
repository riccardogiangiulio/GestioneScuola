package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.SchoolClassRepository;

@Service
public class SchoolClassService {
    
    private static final Logger log = LoggerFactory.getLogger(SchoolClassService.class);
    
    private final SchoolClassRepository schoolClassRepository;
    private final UserService userService;
    
    @Autowired
    public SchoolClassService(
            SchoolClassRepository schoolClassRepository,
            UserService userService) {
        this.schoolClassRepository = schoolClassRepository;
        this.userService = userService;
        log.info("SchoolClassService initialized");
    }
    
    public List<SchoolClass> findAll() {
        log.debug("Retrieving all school classes");
        List<SchoolClass> classes = schoolClassRepository.findAll();
        log.info("Retrieved {} school classes", classes.size());
        return classes;
    }
    
    public SchoolClass findById(Long id) {
        log.debug("Finding school class with id: {}", id);
        return schoolClassRepository.findById(id)
            .orElseThrow(() -> {
                log.error("School class not found with ID: {}", id);
                return new RuntimeException("School class not found with ID: " + id);
            });
    }
    
    @Transactional
    public SchoolClass save(SchoolClass schoolClass) {
        log.info("Saving school class: {}", schoolClass.getName());
        validateSchoolClass(schoolClass);
        SchoolClass savedClass = schoolClassRepository.save(schoolClass);
        log.info("School class saved successfully with ID: {}", savedClass.getId());
        return savedClass;
    }
    
    @Transactional
    public SchoolClass update(Long id, SchoolClass schoolClass) {
        log.info("Updating school class with id: {}", id);
        SchoolClass existingSchoolClass = findById(id);
        
        existingSchoolClass.setName(schoolClass.getName());
        if (schoolClass.getMaxStudents() != null) {
            validateMaxStudents(schoolClass.getMaxStudents(), existingSchoolClass.getRegistrations().size());
            existingSchoolClass.setMaxStudents(schoolClass.getMaxStudents());
        }
        
        existingSchoolClass.setCourse(schoolClass.getCourse());
        existingSchoolClass.setTeachers(schoolClass.getTeachers());
        
        SchoolClass updatedClass = schoolClassRepository.save(existingSchoolClass);
        log.info("School class updated successfully with ID: {}", id);
        return updatedClass;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete school class with id: {}", id);
        SchoolClass schoolClass = findById(id);
        
        if (!schoolClass.getRegistrations().isEmpty()) {
            log.error("Failed to delete school class: Has {} active registrations", schoolClass.getRegistrations().size());
            throw new RuntimeException("Cannot delete a class with active registrations");
        }
        
        schoolClassRepository.deleteById(id);
        log.info("School class deleted successfully with ID: {}", id);
    }
    
    public List<SchoolClass> findByCourse(Course course) {
        log.debug("Finding school classes by course id: {}", course.getId());
        List<SchoolClass> classes = schoolClassRepository.findByCourse(course);
        if (classes.isEmpty()) {
            log.warn("No school classes found for course: {}", course.getId());
        } else {
            log.info("Found {} school classes for course: {}", classes.size(), course.getId());
        }
        return classes;
    }
    
    public List<SchoolClass> findByTeacher(User teacher) {
        log.debug("Finding school classes by teacher id: {}", teacher.getId());
        List<SchoolClass> classes = schoolClassRepository.findByTeacher(teacher);
        if (classes.isEmpty()) {
            log.warn("No school classes found for teacher: {}", teacher.getId());
        } else {
            log.info("Found {} school classes for teacher: {}", classes.size(), teacher.getId());
        }
        return classes;
    }
    
    public List<SchoolClass> findByName(String name) {
        log.debug("Finding school classes by name: {}", name);
        List<SchoolClass> classes = schoolClassRepository.findByName(name);
        if (classes.isEmpty()) {
            log.warn("No school classes found with name: {}", name);
        } else {
            log.info("Found {} school classes with name: {}", classes.size(), name);
        }
        return classes;
    }
    
    public List<SchoolClass> findAvailable() {
        log.debug("Finding available school classes");
        List<SchoolClass> classes = schoolClassRepository.findAvailableClasses();
        log.info("Found {} available school classes", classes.size());
        return classes;
    }
    
    public List<SchoolClass> findFull() {
        log.debug("Finding full school classes");
        List<SchoolClass> classes = schoolClassRepository.findFullClasses();
        log.info("Found {} full school classes", classes.size());
        return classes;
    }
    
    @Transactional
    public SchoolClass addTeacher(Long schoolClassId, Long teacherId) {
        log.info("Adding teacher {} to school class {}", teacherId, schoolClassId);
        SchoolClass schoolClass = findById(schoolClassId);
        User teacher = userService.findById(teacherId);
        
        if (!userService.isTeacher(teacher)) {
            log.error("Failed to add teacher: User {} is not a teacher", teacherId);
            throw new RuntimeException("The specified user is not a teacher");
        }
        
        if (schoolClass.getTeachers().stream().anyMatch(t -> t.getId().equals(teacherId))) {
            log.warn("Teacher {} is already assigned to school class {}", teacherId, schoolClassId);
            return schoolClass;
        }
        
        schoolClass.getTeachers().add(teacher);
        SchoolClass updatedClass = schoolClassRepository.save(schoolClass);
        log.info("Teacher {} added successfully to school class {}", teacherId, schoolClassId);
        return updatedClass;
    }
    
    @Transactional
    public SchoolClass removeTeacher(Long schoolClassId, Long teacherId) {
        log.warn("Removing teacher {} from school class {}", teacherId, schoolClassId);
        SchoolClass schoolClass = findById(schoolClassId);
        
        if (schoolClass.getTeachers().size() <= 1) {
            log.error("Failed to remove teacher: School class {} must have at least one teacher", schoolClassId);
            throw new RuntimeException("A class must have at least one teacher");
        }
        
        if (!schoolClass.getTeachers().removeIf(teacher -> teacher.getId().equals(teacherId))) {
            log.warn("Teacher {} is not assigned to school class {}", teacherId, schoolClassId);
            return schoolClass;
        }
        
        SchoolClass updatedClass = schoolClassRepository.save(schoolClass);
        log.info("Teacher {} removed successfully from school class {}", teacherId, schoolClassId);
        return updatedClass;
    }
    
    public int getAvailableSeats(Long schoolClassId) {
        log.debug("Calculating available seats in school class: {}", schoolClassId);
        SchoolClass schoolClass = findById(schoolClassId);
        int availableSeats = Math.max(0, schoolClass.getMaxStudents() - schoolClass.getRegistrations().size());
        log.info("School class {} has {} available seats", schoolClassId, availableSeats);
        return availableSeats;
    }
    
    public boolean isFull(Long schoolClassId) {
        log.debug("Checking if school class {} is full", schoolClassId);
        boolean isFull = getAvailableSeats(schoolClassId) == 0;
        if (isFull) {
            log.info("School class {} is full", schoolClassId);
        } else {
            log.info("School class {} has available seats", schoolClassId);
        }
        return isFull;
    }
    
    public List<Registration> getActiveRegistrations(Long schoolClassId) {
        log.debug("Retrieving active registrations for school class: {}", schoolClassId);
        SchoolClass schoolClass = findById(schoolClassId);
        List<Registration> activeRegistrations = schoolClass.getRegistrations().stream()
                .filter(registration -> registration.getStatus() == com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus.ACTIVE)
                .toList();
        log.info("Found {} active registrations for school class: {}", activeRegistrations.size(), schoolClassId);
        return activeRegistrations;
    }
    
    private void validateSchoolClass(SchoolClass schoolClass) {
        if (schoolClass.getMaxStudents() != null && schoolClass.getMaxStudents() <= 0) {
            log.error("Invalid maximum number of students: {}", schoolClass.getMaxStudents());
            throw new RuntimeException("Maximum number of students must be greater than zero");
        }
        
        if (schoolClass.getTeachers() == null || schoolClass.getTeachers().isEmpty()) {
            log.error("School class must have at least one teacher");
            throw new RuntimeException("A class must have at least one teacher");
        }
        
        schoolClass.getTeachers().forEach(teacher -> {
            if (!userService.isTeacher(teacher)) {
                log.error("User {} is not a teacher", teacher.getId());
                throw new RuntimeException("The specified user is not a teacher");
            }
        });
    }
    
    private void validateMaxStudents(Integer maxStudents, int currentRegistrations) {
        if (maxStudents < currentRegistrations) {
            log.error("Cannot reduce maximum students to {} when there are {} current registrations", 
                maxStudents, currentRegistrations);
            throw new RuntimeException("Cannot reduce maximum students below current registrations");
        }
    }
}
