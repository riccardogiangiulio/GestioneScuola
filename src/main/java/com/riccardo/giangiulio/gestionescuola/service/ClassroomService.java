package com.riccardo.giangiulio.gestionescuola.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ClassroomNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomCapacityExceededException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTimeRangeException;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.repository.ClassroomRepository;

@Service
public class ClassroomService {
    
    private static final Logger log = LoggerFactory.getLogger(ClassroomService.class);
    
    private final ClassroomRepository classroomRepository;
    private final SchoolClassService schoolClassService;
    
    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository, SchoolClassService schoolClassService) {
        this.classroomRepository = classroomRepository;
        this.schoolClassService = schoolClassService;
        log.info("ClassroomService initialized");
    }
    
    public List<Classroom> findAll() {
        log.debug("Retrieving all classrooms");
        return classroomRepository.findAll();
    }
    
    public Classroom findById(Long id) {
        log.debug("Finding classroom with id: {}", id);
        return classroomRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Classroom not found with ID: {}", id);
                return new ClassroomNotFoundException(id);
            });
    }
    
    @Transactional
    public Classroom save(Classroom classroom) {
        log.info("Saving new classroom: {}", classroom.getName());
        if (classroom.getCapacity() <= 0) {
            log.error("Failed to save classroom: Invalid capacity {}", classroom.getCapacity());
            throw new ClassroomCapacityExceededException(0L, 0, classroom.getCapacity());
        }
        Classroom savedClassroom = classroomRepository.save(classroom);
        log.info("Classroom saved successfully with ID: {}", savedClassroom.getId());
        return savedClassroom;
    }
    
    @Transactional
    public Classroom update(Long id, Classroom classroom) {
        log.info("Updating classroom with id: {}", id);
        Optional<Classroom> existingClassroomOptional = classroomRepository.findById(id);

        if (!existingClassroomOptional.isPresent()) {
            log.error("Failed to update classroom: Not found with ID: {}", id);
            throw new ClassroomNotFoundException(id);
        }

        Classroom existingClassroom = existingClassroomOptional.get();
        existingClassroom.setName(classroom.getName());
        existingClassroom.setCapacity(classroom.getCapacity());
        
        if (classroom.getCapacity() <= 0) {
            log.error("Failed to update classroom: Invalid capacity {}", classroom.getCapacity());
            throw new ClassroomCapacityExceededException(id, 0, classroom.getCapacity());
        }
        
        Classroom updatedClassroom = classroomRepository.save(existingClassroom);
        log.info("Classroom updated successfully with ID: {}", id);
        return updatedClassroom;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete classroom with id: {}", id);
        if (!classroomRepository.existsById(id)) {
            log.error("Failed to delete classroom: Not found with ID: {}", id);
            throw new ClassroomNotFoundException(id);
        }
        classroomRepository.deleteById(id);
        log.info("Classroom deleted successfully with ID: {}", id);
    }
    
    public List<Classroom> findByName(String name) {
        log.debug("Finding classrooms by name: {}", name);
        List<Classroom> classrooms = classroomRepository.findByName(name);
        if (classrooms.isEmpty()) {
            log.warn("No classrooms found with name: {}", name);
        } else {
            log.info("Found {} classrooms with name: {}", classrooms.size(), name);
        }
        return classrooms;
    }
    
    public List<Classroom> findByMinCapacity(Integer minCapacity) {
        log.debug("Finding classrooms with minimum capacity of: {}", minCapacity);
        if (minCapacity <= 0) {
            log.error("Invalid minimum capacity specified: {}", minCapacity);
            throw new ClassroomCapacityExceededException(0L, 0, minCapacity);
        }
        List<Classroom> classrooms = classroomRepository.findByCapacityGreaterThanEqual(minCapacity);
        log.info("Found {} classrooms with minimum capacity of {}", classrooms.size(), minCapacity);
        return classrooms;
    }
    
    public List<Classroom> findAvailableInTimeRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding available classrooms between {} and {}", start, end);
        if (start.isAfter(end)) {
            log.error("Invalid time range: start time {} is after end time {}", start, end);
            throw new InvalidTimeRangeException(start, end);
        }
        List<Classroom> availableClassrooms = classroomRepository.findAvailableClassroomsInTimeRange(start, end);
        if (availableClassrooms.isEmpty()) {
            log.warn("No available classrooms found in the specified time range");
        } else {
            log.info("Found {} available classrooms in the specified time range", availableClassrooms.size());
        }
        return availableClassrooms;
    }
    
    public List<Classroom> findWithSufficientCapacityForSchoolClass(Long schoolClassId) {
        log.debug("Finding classrooms with sufficient capacity for school class id: {}", schoolClassId);
        
        // Ottieni prima la SchoolClass e il numero di studenti
        SchoolClass schoolClass = schoolClassService.findById(schoolClassId);
        int studentCount = schoolClass.getRegistrations().size();
        
        List<Classroom> classrooms = classroomRepository.findClassroomsWithSufficientCapacityForSchoolClass(schoolClassId);
        if (classrooms.isEmpty()) {
            log.warn("No classrooms found with sufficient capacity for school class ID: {}", schoolClassId);
            // Trova l'aula con capacità massima per mostrare informazioni utili nell'eccezione
            List<Classroom> allClassrooms = findAll();
            int maxCapacity = allClassrooms.stream()
                               .mapToInt(Classroom::getCapacity)
                               .max()
                               .orElse(0);
            
            throw new ClassroomCapacityExceededException(schoolClassId, maxCapacity, studentCount);
        } else {
            log.info("Found {} classrooms with sufficient capacity for school class ID: {}", classrooms.size(), schoolClassId);
        }
        return classrooms;
    }
    
    public boolean isAvailableForTimeSlot(Long classroomId, LocalDateTime start, LocalDateTime end) {
        log.debug("Checking if classroom id {} is available between {} and {}", classroomId, start, end);
        if (start.isAfter(end)) {
            log.error("Invalid time range: start time {} is after end time {}", start, end);
            throw new InvalidTimeRangeException(start, end);
        }
        
        List<Classroom> availableClassrooms = classroomRepository.findAvailableClassroomsInTimeRange(start, end);
        boolean isAvailable = availableClassrooms.stream().anyMatch(classroom -> classroom.getId().equals(classroomId));
        
        if (!isAvailable) {
            log.warn("Classroom {} is not available in the specified time range", classroomId);
        } else {
            log.info("Classroom {} is available in the specified time range", classroomId);
        }
        
        return isAvailable;
    }
    
    public boolean hasSufficientCapacity(Long classroomId, Integer requiredCapacity) {
        log.debug("Checking if classroom id {} has sufficient capacity of {}", classroomId, requiredCapacity);
        if (requiredCapacity <= 0) {
            log.error("Invalid required capacity specified: {}", requiredCapacity);
            throw new ClassroomCapacityExceededException(classroomId, 0, requiredCapacity);
        }
        
        Classroom classroom = findById(classroomId);
        boolean hasCapacity = classroom.getCapacity() >= requiredCapacity;
        
        if (!hasCapacity) {
            log.warn("Classroom {} has insufficient capacity. Required: {}, Available: {}", 
                classroomId, requiredCapacity, classroom.getCapacity());
        } else {
            log.info("Classroom {} has sufficient capacity. Required: {}, Available: {}", 
                classroomId, requiredCapacity, classroom.getCapacity());
        }
        
        return hasCapacity;
    }
}
