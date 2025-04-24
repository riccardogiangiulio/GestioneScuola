package com.riccardo.giangiulio.gestionescuola.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    List<Exam> findByTitle(String title);
    
    List<Exam> findByDate(LocalDateTime date);
    
    List<Exam> findBySubject(Subject subject);
    
    List<Exam> findBySchoolClass(SchoolClass schoolClass);
    
    List<Exam> findByTeacher(User teacher);
    
    List<Exam> findByClassroom(Classroom classroom);
    
    @Query("SELECT e FROM Exam e WHERE :course MEMBER OF e.courses")
    List<Exam> findByCourse(@Param("course") Course course);
    
    @Query("SELECT DISTINCT e FROM Exam e JOIN e.courses c WHERE c IN :courses")
    List<Exam> findByAnyCourseIn(@Param("courses") List<Course> courses);

    List<Exam> findByDateBetween(LocalDateTime start, LocalDateTime end);

    List<Exam> findByDateAfter(LocalDateTime date);

    List<Exam> findByDateBefore(LocalDateTime date);
}
