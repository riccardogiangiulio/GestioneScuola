package com.riccardo.giangiulio.gestionescuola.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    List<Lesson> findByTitle(String title);
    
    List<Lesson> findBySchoolClass(SchoolClass schoolClass);
    
    List<Lesson> findByTeacher(User teacher);
    
    List<Lesson> findByClassroom(Classroom classroom);
    
    List<Lesson> findBySubject(Subject subject);
    
    List<Lesson> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT l FROM Lesson l WHERE l.dateTime BETWEEN :start AND :end AND l.classroom = :classroom")
    List<Lesson> findByDateTimeRangeAndClassroom(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end, 
            @Param("classroom") Classroom classroom);
    
    @Query("SELECT l FROM Lesson l WHERE l.dateTime BETWEEN :start AND :end AND l.teacher = :teacher")
    List<Lesson> findByDateTimeRangeAndTeacher(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end, 
            @Param("teacher") User teacher);
    
    @Query("SELECT l FROM Lesson l WHERE l.dateTime >= CURRENT_TIMESTAMP AND l.schoolClass = :schoolClass ORDER BY l.dateTime ASC")
    List<Lesson> findUpcomingLessonsBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
    
    @Query("SELECT l FROM Lesson l WHERE DATE(l.dateTime) = CURRENT_DATE")
    List<Lesson> findTodayLessons();
}
