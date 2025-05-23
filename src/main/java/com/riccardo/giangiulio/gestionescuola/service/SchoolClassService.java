package com.riccardo.giangiulio.gestionescuola.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.SchoolClassNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ActiveRegistrationsException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.MinimumTeachersException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.SchoolClassFullException;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.SchoolClassRepository;

@Service
public class SchoolClassService {
    
    private static final Logger log = LoggerFactory.getLogger(SchoolClassService.class);
    
    private final SchoolClassRepository schoolClassRepository;
    private final UserService userService;
    private final CourseService courseService;
    
    @Autowired
    public SchoolClassService(
            SchoolClassRepository schoolClassRepository,
            UserService userService,
            CourseService courseService) {
        this.schoolClassRepository = schoolClassRepository;
        this.userService = userService;
        this.courseService = courseService;
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
                return new SchoolClassNotFoundException(id);
            });
    }
    
    @Transactional
    public SchoolClass save(SchoolClass schoolClassRequest) {
        log.info("Saving school class: {}", schoolClassRequest.getName());

        Course course = courseService.findById(schoolClassRequest.getCourse().getId());
        
        if (schoolClassRequest.getTeachers() == null || schoolClassRequest.getTeachers().isEmpty()) {
            log.error("School class must have at least one teacher");
            throw new MinimumTeachersException(null);
        }
        
        // Ottieni il set di insegnanti verificati
        Set<User> teachers = new HashSet<>();
        for (User teacherRequest : schoolClassRequest.getTeachers()) {
            User teacher = userService.findById(teacherRequest.getId());
            if (!userService.isTeacher(teacher)) {
                log.error("User {} is not a teacher", teacher.getId());
                throw new InvalidTeacherException(teacher.getId());
            }
            teachers.add(teacher);
        }

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(schoolClassRequest.getName());
        schoolClass.setMaxStudents(schoolClassRequest.getMaxStudents());
        schoolClass.setCourse(course);
        schoolClass.setTeachers(teachers);

        validateSchoolClass(schoolClass);
        SchoolClass savedClass = schoolClassRepository.save(schoolClass);
        log.info("School class saved successfully with ID: {}", savedClass.getId());
        return savedClass;
    }
    
    @Transactional
    public SchoolClass update(Long id, SchoolClass schoolClassRequest) {
        log.info("Updating school class with id: {}", id);
        
        SchoolClass existingSchoolClass = findById(id);
        
        if (schoolClassRequest.getName() != null) {
            existingSchoolClass.setName(schoolClassRequest.getName());
        }
        
        if (schoolClassRequest.getMaxStudents() != null) {
            validateMaxStudents(schoolClassRequest.getMaxStudents(), existingSchoolClass.getRegistrations().size());
            existingSchoolClass.setMaxStudents(schoolClassRequest.getMaxStudents());
        }
        
        // Aggiorna il corso se fornito
        if (schoolClassRequest.getCourse() != null && schoolClassRequest.getCourse().getId() != null) {
            Course course = courseService.findById(schoolClassRequest.getCourse().getId());
            existingSchoolClass.setCourse(course);
        }
        
        // Aggiorna gli insegnanti se forniti
        if (schoolClassRequest.getTeachers() != null && !schoolClassRequest.getTeachers().isEmpty()) {
            Set<User> teachers = new HashSet<>();
            for (User teacherRequest : schoolClassRequest.getTeachers()) {
                User teacher = userService.findById(teacherRequest.getId());
                if (!userService.isTeacher(teacher)) {
                    log.error("User {} is not a teacher", teacher.getId());
                    throw new InvalidTeacherException(teacher.getId());
                }
                teachers.add(teacher);
            }
            existingSchoolClass.setTeachers(teachers);
        }
        
        validateSchoolClass(existingSchoolClass);
        
        SchoolClass updatedClass = schoolClassRepository.save(existingSchoolClass);
        log.info("School class updated successfully with ID: {}", id);
        return updatedClass;
    }
    
    public void deleteById(Long id) {
        log.warn("Attempting to delete school class with id: {}", id);
        SchoolClass schoolClass = findById(id);
        
        int registrationsCount = schoolClass.getRegistrations().size();
        if (registrationsCount > 0) {
            log.error("Failed to delete school class: Has {} active registrations", registrationsCount);
            throw new ActiveRegistrationsException(id, registrationsCount);
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
    
    public SchoolClass findByName(String name) {
        log.debug("Finding school classes by name: {}", name);
        SchoolClass schoolClass = schoolClassRepository.findByName(name)
            .orElseThrow(() -> {
                log.warn("No school classes found with name: {}", name);
                return new SchoolClassNotFoundException(name);
            });
        log.info("Found school class with name: {}", name);
        return schoolClass;
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
            throw new InvalidTeacherException(teacherId);
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
            throw new MinimumTeachersException(schoolClassId);
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
            throw new SchoolClassFullException(schoolClassId);
        } else {
            log.info("School class {} has available seats", schoolClassId);
        }
        return isFull;
    }
    
    public List<Registration> getActiveRegistrations(Long schoolClassId) {
        log.debug("Retrieving active registrations for school class: {}", schoolClassId);
        SchoolClass schoolClass = findById(schoolClassId);
        List<Registration> activeRegistrations = schoolClass.getRegistrations().stream()
                .filter(registration -> registration.getStatus() == RegistrationStatus.ACTIVE)
                .toList();
        log.info("Found {} active registrations for school class: {}", activeRegistrations.size(), schoolClassId);
        return activeRegistrations;
    }
    
    private void validateSchoolClass(SchoolClass schoolClass) {

        // Validare che ci sia almeno un insegnante
        if (schoolClass.getTeachers() == null || schoolClass.getTeachers().isEmpty()) {
            log.error("School class must have at least one teacher");
            throw new MinimumTeachersException(schoolClass.getId());
        }
        
        // Validare che tutti gli utenti siano insegnanti
        schoolClass.getTeachers().forEach(teacher -> {
            if (!userService.isTeacher(teacher)) {
                log.error("User {} is not a teacher", teacher.getId());
                throw new InvalidTeacherException(teacher.getId());
            }
        });
    }
    
    private void validateMaxStudents(Integer maxStudents, int currentRegistrations) {
        if (maxStudents < currentRegistrations) {
            log.error("Cannot reduce maximum students to {} when there are {} current registrations", 
                maxStudents, currentRegistrations);
            throw new IllegalArgumentException("Cannot reduce maximum students below current registrations");
        }
    }
}
