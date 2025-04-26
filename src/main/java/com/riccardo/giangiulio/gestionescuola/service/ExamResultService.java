package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ExamResultNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidExamDataException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidStudentException;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.ExamResultRepository;

@Service
public class ExamResultService {
    
    private static final Logger log = LoggerFactory.getLogger(ExamResultService.class);
    
    private final ExamResultRepository examResultRepository;
    private final UserService userService;
    private final ExamService examService;
    
    @Autowired
    public ExamResultService(
            ExamResultRepository examResultRepository,
            UserService userService,
            ExamService examService) {
        this.examResultRepository = examResultRepository;
        this.userService = userService;
        this.examService = examService;
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
                return new ExamResultNotFoundException(id);
            });
    }
    
    public ExamResult save(ExamResult examResultRequest) {
        log.debug("Saving exam result for exam {} and student {}", 
            examResultRequest.getExam().getId(), examResultRequest.getStudent().getId());
        
        // Carica le entità complete dal database
        User student = userService.findById(examResultRequest.getStudent().getId());
        Exam exam = examService.findById(examResultRequest.getExam().getId());
        
        // Crea un nuovo ExamResult con le entità complete
        ExamResult examResult = new ExamResult();
        examResult.setScore(examResultRequest.getScore());
        examResult.setNotes(examResultRequest.getNotes());
        examResult.setDate(examResultRequest.getDate());
        examResult.setExam(exam);
        examResult.setStudent(student);
        
        // Validazione e salvataggio
        validateExamResult(examResult);
        
        ExamResult savedResult = examResultRepository.save(examResult);
        log.info("Exam result saved successfully for exam {} and student {}", 
            savedResult.getExam().getId(), savedResult.getStudent().getId());
        return savedResult;
    }

    @Transactional
    public ExamResult update(Long id, ExamResult examResult) {
        log.debug("Updating exam result with id: {}", id);
        ExamResult existingResult = findById(id);

        if (examResult.getScore() != null) {
            existingResult.setScore(examResult.getScore());
        }
        if (examResult.getNotes() != null) {
            existingResult.setNotes(examResult.getNotes());
        }
        if (examResult.getDate() != null) {
            existingResult.setDate(examResult.getDate());
        }
        if (examResult.getExam() != null && examResult.getExam().getId() != null) {
            existingResult.setExam(examService.findById(examResult.getExam().getId()));
        }
        if (examResult.getStudent() != null && examResult.getStudent().getId() != null) {
            existingResult.setStudent(userService.findById(examResult.getStudent().getId()));
        }
        
        validateExamResult(existingResult);
            
        ExamResult updatedResult = examResultRepository.save(existingResult);
        log.info("Exam result updated successfully with ID: {}", id);
        return updatedResult;
    }
    
    public void deleteById(Long id) {
        log.debug("Deleting exam result with id: {}", id);
        if (!examResultRepository.existsById(id)) {
            log.error("Attempting to delete non-existent exam result with ID: {}", id);
            throw new ExamResultNotFoundException(id);
        }
        examResultRepository.deleteById(id);
        log.info("Exam result deleted successfully with ID: {}", id);
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
        if (!userService.isStudent(student)) {
            log.warn("Attempted to find exam results for non-student user: {}", student.getId());
            throw new InvalidStudentException(student.getId());
        }
        
        List<ExamResult> results = examResultRepository.findByStudent(student);
        if (results.isEmpty()) {
            log.warn("No exam results found for student ID: {}", student.getId());
        }
        return results;
    }
    
    public ExamResult findByExamAndStudent(Exam exam, User student) {
        log.debug("Finding exam result for exam id: {} and student id: {}", exam.getId(), student.getId());
        
        if (!userService.isStudent(student)) {
            log.warn("Attempted to find exam result for non-student user: {}", student.getId());
            throw new InvalidStudentException(student.getId());
        }
        
        return examResultRepository.findByExamAndStudent(exam, student)
            .orElseThrow(() -> {
                log.error("Exam result not found for exam {} and student {}", exam.getId(), student.getId());
                return new ExamResultNotFoundException(exam.getId(), student.getId());
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
        
        if (!userService.isStudent(student)) {
            log.warn("Attempted to find average score for non-student user: {}", student.getId());
            throw new InvalidStudentException(student.getId());
        }
        
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
        
        if (!userService.isStudent(student)) {
            log.warn("Attempted to count passed exams for non-student user: {}", student.getId());
            throw new InvalidStudentException(student.getId());
        }
        
        return examResultRepository.countPassedByStudent(student);
    }
    
    private void validateExamResult(ExamResult examResult) {
        // Verifica che l'utente sia uno studente
        if (!userService.isStudent(examResult.getStudent())) {
            log.error("Cannot save/update exam result: user {} is not a student", examResult.getStudent().getId());
            throw new InvalidStudentException(examResult.getStudent().getId());
        }
        
        // Verifica che il punteggio sia valido
        if (examResult.getScore() < 0) {
            log.error("Invalid score value: {}", examResult.getScore());
            throw new InvalidExamDataException("Il punteggio non può essere negativo");
        }
        
        // Verifica che il punteggio non superi il punteggio massimo dell'esame
        if (examResult.getScore() > examResult.getExam().getMaxScore()) {
            log.error("Score {} exceeds max score {} for exam {}", 
                examResult.getScore(), examResult.getExam().getMaxScore(), examResult.getExam().getId());
            throw new InvalidExamDataException(
                String.format("Il punteggio (%s) non può superare il punteggio massimo dell'esame (%s)", 
                examResult.getScore(), examResult.getExam().getMaxScore()));
        }
        
        log.debug("Exam result validation completed successfully");
    }
} 