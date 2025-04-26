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
        
    List<Lesson> findBySchoolClass(SchoolClass schoolClass);
    
    List<Lesson> findByTeacher(User teacher);
    
    List<Lesson> findByClassroom(Classroom classroom);
    
    List<Lesson> findBySubject(Subject subject);
    
    List<Lesson> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);    
    
    @Query("SELECT l FROM Lesson l WHERE l.startDateTime >= CURRENT_TIMESTAMP AND l.schoolClass = :schoolClass ORDER BY l.startDateTime ASC")
    List<Lesson> findUpcomingLessonsBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
    
    @Query(value = "SELECT * FROM lessons l WHERE DATE(l.start_date_time) = CURRENT_DATE", nativeQuery = true)
    List<Lesson> findTodayLessons();
}
