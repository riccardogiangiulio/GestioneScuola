package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.ExamResultRepository;

@Service
public class ExamResultService {
    
    private static final Logger log = LoggerFactory.getLogger(ExamResultService.class);
    
    private final ExamResultRepository examResultRepository;
    private final UserService userService;
    
    @Autowired
    public ExamResultService(
            ExamResultRepository examResultRepository,
            UserService userService) {
        this.examResultRepository = examResultRepository;
        this.userService = userService;
        log.info("ExamResultService initialized");
    }
    
    public List<ExamResult> findAll() {
        log.debug("Retrieving all exam results");
        return examResultRepository.findAll();
    }
    
    public ExamResult findById(Long id) {
        log.debug("Finding exam result with id: {}", id);
        return examResultRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Exam result not found with ID: {}", id);
                return new RuntimeException("Exam result not found with ID: " + id);
            });
    }
    
    @Transactional
    public ExamResult save(ExamResult examResult) {
        log.debug("Saving exam result for exam {} and student {}", 
            examResult.getExam().getId(), examResult.getStudent().getId());
        
        // Verifica che l'utente sia uno studente
        if (!userService.isStudent(examResult.getStudent())) {
            log.error("Cannot save exam result: user {} is not a student", examResult.getStudent().getId());
            throw new RuntimeException("L'utente specificato non è uno studente");
        }
        
        ExamResult savedResult = examResultRepository.save(examResult);
        log.info("Exam result saved successfully for exam {} and student {}", 
            savedResult.getExam().getId(), savedResult.getStudent().getId());
        return savedResult;
    }

    @Transactional
    public ExamResult update(Long id, ExamResult examResult) {
        log.debug("Updating exam result with id: {}", id);
        Optional<ExamResult> existingResultOptional = examResultRepository.findById(id);

        if (!existingResultOptional.isPresent()) {
            log.error("Attempting to update non-existent exam result with ID: {}", id);
            throw new RuntimeException("Exam result not found with ID: " + id);
        }

        // Verifica che l'utente sia uno studente
        if (!userService.isStudent(examResult.getStudent())) {
            log.error("Cannot update exam result: user {} is not a student", examResult.getStudent().getId());
            throw new RuntimeException("L'utente specificato non è uno studente");
        }

        ExamResult existingResult = existingResultOptional.get();
        existingResult.setScore(examResult.getScore());
        existingResult.setNotes(examResult.getNotes());
        existingResult.setDate(examResult.getDate());
        existingResult.setExam(examResult.getExam());
        existingResult.setStudent(examResult.getStudent());
            
        return examResultRepository.save(existingResult);
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting exam result with id: {}", id);
        if (!examResultRepository.existsById(id)) {
            log.error("Attempting to delete non-existent exam result with ID: {}", id);
            throw new RuntimeException("Exam result not found with ID: " + id);
        }
        examResultRepository.deleteById(id);
    }
    
    public List<ExamResult> findByExam(Exam exam) {
        log.debug("Finding exam results for exam id: {}", exam.getId());
        List<ExamResult> results = examResultRepository.findByExam(exam);
        if (results.isEmpty()) {
            log.warn("No exam results found for exam ID: {}", exam.getId());
        }
        return results;
    }
    
    public List<ExamResult> findByStudent(User student) {
        log.debug("Finding exam results for student id: {}", student.getId());
        List<ExamResult> results = examResultRepository.findByStudent(student);
        if (results.isEmpty()) {
            log.warn("No exam results found for student ID: {}", student.getId());
        }
        return results;
    }
    
    public ExamResult findByExamAndStudent(Exam exam, User student) {
        log.debug("Finding exam result for exam id: {} and student id: {}", exam.getId(), student.getId());
        return examResultRepository.findByExamAndStudent(exam, student)
            .orElseThrow(() -> {
                log.error("Exam result not found for exam {} and student {}", exam.getId(), student.getId());
                return new RuntimeException("Exam result not found for this exam and student");
            });
    }
    
    public List<ExamResult> findAllPassed() {
        log.debug("Retrieving all passed exam results");
        List<ExamResult> results = examResultRepository.findAllPassed();
        if (results.isEmpty()) {
            log.warn("No passed exam results found");
        }
        return results;
    }
    
    public List<ExamResult> findAllFailed() {
        log.debug("Retrieving all failed exam results");
        List<ExamResult> results = examResultRepository.findAllFailed();
        if (results.isEmpty()) {
            log.warn("No failed exam results found");
        }
        return results;
    }
    
    public Double findAverageScoreByExam(Exam exam) {
        log.debug("Calculating average score for exam id: {}", exam.getId());
        Double average = examResultRepository.findAverageScoreByExam(exam);
        if (average == null) {
            log.warn("No scores found for exam ID: {}", exam.getId());
        }
        return average;
    }
    
    public Double findAverageScoreByStudent(User student) {
        log.debug("Calculating average score for student id: {}", student.getId());
        Double average = examResultRepository.findAverageScoreByStudent(student);
        if (average == null) {
            log.warn("No scores found for student ID: {}", student.getId());
        }
        return average;
    }
    
    public Long countPassedByExam(Exam exam) {
        log.debug("Counting passed students for exam id: {}", exam.getId());
        return examResultRepository.countPassedByExam(exam);
    }
    
    public Long countPassedByStudent(User student) {
        log.debug("Counting passed exams for student id: {}", student.getId());
        return examResultRepository.countPassedByStudent(student);
    }
} 