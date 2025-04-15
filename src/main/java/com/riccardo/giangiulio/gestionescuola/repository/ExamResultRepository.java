package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;
import com.riccardo.giangiulio.gestionescuola.model.User;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    
    List<ExamResult> findByExam(Exam exam);
    
    List<ExamResult> findByStudent(User student);
    
    Optional<ExamResult> findByExamAndStudent(Exam exam, User student);
    
    @Query("SELECT er FROM ExamResult er WHERE er.score >= er.exam.passingScore")
    List<ExamResult> findAllPassed();
    
    @Query("SELECT er FROM ExamResult er WHERE er.score < er.exam.passingScore")
    List<ExamResult> findAllFailed();
    
    @Query("SELECT AVG(er.score) FROM ExamResult er WHERE er.exam = :exam")
    Double findAverageScoreByExam(@Param("exam") Exam exam);
    
    @Query("SELECT AVG(er.score) FROM ExamResult er WHERE er.student = :student")
    Double findAverageScoreByStudent(@Param("student") User student);
    
    @Query("SELECT COUNT(er) FROM ExamResult er WHERE er.exam = :exam AND er.score >= er.exam.passingScore")
    Long countPassedByExam(@Param("exam") Exam exam);
    
    @Query("SELECT COUNT(er) FROM ExamResult er WHERE er.student = :student AND er.score >= er.exam.passingScore")
    Long countPassedByStudent(@Param("student") User student);
}
