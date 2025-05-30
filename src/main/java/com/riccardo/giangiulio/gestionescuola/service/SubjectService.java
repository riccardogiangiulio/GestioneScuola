package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.SubjectNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.SubjectRepository;

@Service
public class SubjectService {
    
    private static final Logger log = LoggerFactory.getLogger(SubjectService.class);
    
    private final SubjectRepository subjectRepository;
    private final UserService userService;
    
    @Autowired
    public SubjectService(SubjectRepository subjectRepository, UserService userService) {
        this.subjectRepository = subjectRepository;
        this.userService = userService;
        log.info("SubjectService initialized");
    }
    
    public List<Subject> findAll() {
        log.debug("Retrieving all subjects");
        List<Subject> subjects = subjectRepository.findAll();
        log.info("Retrieved {} subjects", subjects.size());
        return subjects;
    }
    
    public Subject findById(Long id) {
        log.debug("Finding subject with id: {}", id);
        return subjectRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Subject not found with ID: {}", id);
                return new SubjectNotFoundException(id);
            });
    }
    
    @Transactional
    public Subject save(Subject subjectRequest) {
        log.info("Saving subject: {}", subjectRequest.getName());
        
        User teacher = userService.findById(subjectRequest.getTeacher().getId());
        
        if (!userService.isTeacher(teacher)) {
            log.error("Failed to save subject: User {} is not a teacher", teacher.getId());
            throw new InvalidTeacherException(teacher.getId());
        }
        
        Subject subject = new Subject();
        subject.setName(subjectRequest.getName());
        subject.setDescription(subjectRequest.getDescription());
        subject.setTeacher(teacher);
        
        Subject savedSubject = subjectRepository.save(subject);
        log.info("Subject saved successfully with ID: {}", savedSubject.getId());
        return savedSubject;
    }
    
    @Transactional
    public Subject update(Long id, Subject subjectRequest) {
        log.info("Updating subject with id: {}", id);
        
        Subject existingSubject = findById(id);
        
        if (subjectRequest.getName() != null) {
            existingSubject.setName(subjectRequest.getName());
        }
        
        if (subjectRequest.getDescription() != null) {
            existingSubject.setDescription(subjectRequest.getDescription());
        }
        
        if (subjectRequest.getTeacher() != null && subjectRequest.getTeacher().getId() != null) {
            User teacher = userService.findById(subjectRequest.getTeacher().getId());
            
            if (!userService.isTeacher(teacher)) {
                log.error("Failed to update subject: User {} is not a teacher", teacher.getId());
                throw new InvalidTeacherException(teacher.getId());
            }
            
            existingSubject.setTeacher(teacher);
        }
        
        Subject updatedSubject = subjectRepository.save(existingSubject);
        log.info("Subject updated successfully with ID: {}", id);
        return updatedSubject;
    }
    
    public void deleteById(Long id) {
        log.warn("Attempting to delete subject with id: {}", id);
        if (subjectRepository.findById(id).isEmpty()) {
            log.error("Failed to delete subject: Not found with ID: {}", id);
            throw new SubjectNotFoundException(id);
        }
        subjectRepository.deleteById(id);
        log.info("Subject deleted successfully with ID: {}", id);
    }
    
    public Subject findByName(String name) {
        log.debug("Finding subjects by name: {}", name);
        return subjectRepository.findByName(name).orElseThrow(() -> {
            log.error("Subject not found with name: {}", name);
            return new SubjectNotFoundException(name);
        });
    }

    public List<Subject> findByCourse(Course course) {
        log.debug("Finding subjects for course: {}", course.getId());
        List<Subject> subjects = subjectRepository.findByCourse(course);
        if (subjects.isEmpty()) {
            log.warn("No subjects found for course: {}", course.getId());
        } else {
            log.info("Found {} subjects for course: {}", subjects.size(), course.getId());
        }
        return subjects;
    }
    
    @Transactional
    public Subject assignTeacher(Long subjectId, Long teacherId) {
        log.info("Assigning teacher {} to subject {}", teacherId, subjectId);
        Subject subject = findById(subjectId);
        User teacher = userService.findById(teacherId);
        
        if (!userService.isTeacher(teacher)) {
            log.error("Failed to assign teacher: User {} is not a teacher", teacherId);
            throw new InvalidTeacherException(teacherId);
        }
        
        subject.setTeacher(teacher);
        Subject updatedSubject = subjectRepository.save(subject);
        log.info("Teacher {} assigned successfully to subject {}", teacherId, subjectId);
        return updatedSubject;
    }
}
