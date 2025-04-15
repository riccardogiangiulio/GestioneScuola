package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Attendance;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByLesson(Lesson lesson);
    
    List<Attendance> findByStudent(User student);
    
    Optional<Attendance> findByLessonAndStudent(Lesson lesson, User student);
    
    List<Attendance> findByPresent(Boolean present);
    
    @Query("SELECT a FROM Attendance a WHERE a.lesson.schoolClass = :schoolClass")
    List<Attendance> findBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.present = true")
    Long countPresentByStudent(@Param("student") User student);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.present = false")
    Long countAbsentByStudent(@Param("student") User student);
    
}