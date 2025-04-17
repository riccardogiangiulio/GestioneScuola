package com.riccardo.giangiulio.gestionescuola.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.ExamRepository;

@Service
public class ExamService {
    
    private static final Logger log = LoggerFactory.getLogger(ExamService.class);
    
    private final ExamRepository examRepository;
    
    @Autowired
    public ExamService(
            ExamRepository examRepository) {
        this.examRepository = examRepository;
        log.info("ExamService initialized");
    }
    
    public List<Exam> findAll() {
        log.debug("Retrieving all exams");
        List<Exam> exams = examRepository.findAll();
        log.info("Retrieved {} exams", exams.size());
        return exams;
    }
    
    public Exam findById(Long id) {
        log.debug("Finding exam with id: {}", id);
        return examRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Exam not found with ID: {}", id);
                return new RuntimeException("Exam not found with ID: " + id);
            });
    }
    
    @Transactional
    public Exam save(Exam exam) {
        log.info("Saving exam: {}", exam.getId());
        Exam savedExam = examRepository.save(exam);
        log.info("Exam saved successfully with id: {}", savedExam.getId());
        return savedExam;
    }
    
    @Transactional
    public Exam update(Long id, Exam exam) {
        log.info("Updating exam with id: {}", id);
        Optional<Exam> existingExamOptional = examRepository.findById(id);
        
        if (!existingExamOptional.isPresent()) {
            log.error("Attempting to update non-existent exam with ID: {}", id);
            throw new RuntimeException("Exam not found with ID: " + id);
        }
        
        Exam existingExam = existingExamOptional.get();
        existingExam.setTitle(exam.getTitle());
        existingExam.setDescription(exam.getDescription());
        existingExam.setDate(exam.getDate());
        existingExam.setDuration(exam.getDuration());
        existingExam.setMaxScore(exam.getMaxScore());
        existingExam.setPassingScore(exam.getPassingScore());
        existingExam.setClassroom(exam.getClassroom());
        existingExam.setSubject(exam.getSubject());
        existingExam.setSchoolClass(exam.getSchoolClass());
        existingExam.setTeacher(exam.getTeacher());
        
        Exam updatedExam = examRepository.save(existingExam);
        log.info("Exam updated successfully with ID: {}", id);
        return updatedExam;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete exam with id: {}", id);
        if (!examRepository.existsById(id)) {
            log.error("Attempting to delete non-existent exam with ID: {}", id);
            throw new RuntimeException("Exam not found with ID: " + id);
        }
        examRepository.deleteById(id);
        log.info("Exam deleted successfully with ID: {}", id);
    }
    
    public List<Exam> findByTitle(String title) {
        log.debug("Finding exams by title: {}", title);
        List<Exam> exams = examRepository.findByTitle(title);
        if (exams.isEmpty()) {
            log.warn("No exams found with title: {}", title);
        } else {
            log.info("Found {} exams with title: {}", exams.size(), title);
        }
        return exams;
    }
    
    public List<Exam> findByDate(LocalDateTime date) {
        log.debug("Finding exams by date: {}", date);
        List<Exam> exams = examRepository.findByDate(date);
        if (exams.isEmpty()) {
            log.warn("No exams found for date: {}", date);
        } else {
            log.info("Found {} exams for date: {}", exams.size(), date);
        }
        return exams;
    }
    
    public List<Exam> findBySubject(Subject subject) {
        log.debug("Finding exams for subject id: {}", subject.getId());
        List<Exam> exams = examRepository.findBySubject(subject);
        if (exams.isEmpty()) {
            log.warn("No exams found for subject ID: {}", subject.getId());
        }
        return exams;
    }
    
    public List<Exam> findBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding exams by school class id: {}", schoolClass.getId());
        List<Exam> exams = examRepository.findBySchoolClass(schoolClass);
        if (exams.isEmpty()) {
            log.warn("No exams found for school class ID: {}", schoolClass.getId());
        } else {
            log.info("Found {} exams for school class ID: {}", exams.size(), schoolClass.getId());
        }
        return exams;
    }
    
    public List<Exam> findByTeacher(User teacher) {
        log.debug("Finding exams for teacher id: {}", teacher.getId());
        List<Exam> exams = examRepository.findByTeacher(teacher);
        if (exams.isEmpty()) {
            log.warn("No exams found for teacher ID: {}", teacher.getId());
        }
        return exams;
    }
    
    public List<Exam> findByClassroom(Classroom classroom) {
        log.debug("Finding exams by classroom id: {}", classroom.getId());
        return examRepository.findByClassroom(classroom);
    }
    
    public List<Exam> findByCourse(Course course) {
        log.debug("Finding exams by course id: {}", course.getId());
        return examRepository.findByCourse(course);
    }
    
    public List<Exam> findByAnyCourseIn(List<Course> courses) {
        log.debug("Finding exams by any course in a list of {} courses", courses.size());
        return examRepository.findByAnyCourseIn(courses);
    }
    
    public List<Exam> findByDateRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding exams between {} and {}", start, end);
        
        if (start.isAfter(end)) {
            throw new RuntimeException("Start date cannot be after end date");
        }
        
        return examRepository.findAll().stream()
                .filter(exam -> !exam.getDate().isBefore(start) && !exam.getDate().isAfter(end))
                .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                .toList();
    }
    
    public List<Exam> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding exams between {} and {}", start, end);
        if (start.isAfter(end)) {
            log.error("Invalid time range: start time {} is after end time {}", start, end);
            throw new RuntimeException("Start time cannot be after end time");
        }
        List<Exam> exams = examRepository.findAll().stream()
                .filter(exam -> !exam.getDate().isBefore(start) && !exam.getDate().isAfter(end))
                .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                .toList();
        if (exams.isEmpty()) {
            log.warn("No exams found in the specified time range");
        }
        return exams;
    }
    
    public List<Exam> findUpcomingExams() {
        log.debug("Finding upcoming exams");
        List<Exam> exams = examRepository.findAll().stream()
                .filter(exam -> exam.getDate().isAfter(LocalDateTime.now()))
                .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                .toList();
        if (exams.isEmpty()) {
            log.warn("No upcoming exams found");
        }
        return exams;
    }
    
    public List<Exam> findPastExams() {
        log.debug("Finding past exams");
        List<Exam> exams = examRepository.findAll().stream()
                .filter(exam -> exam.getDate().isBefore(LocalDateTime.now()))
                .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                .toList();
        if (exams.isEmpty()) {
            log.warn("No past exams found");
        }
        return exams;
    }
    
   
}
