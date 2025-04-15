package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    
    List<SchoolClass> findByCourse(Course course);
    
    @Query("SELECT sc FROM SchoolClass sc WHERE :teacher MEMBER OF sc.teachers")
    List<SchoolClass> findByTeacher(@Param("teacher") User teacher);
    
    List<SchoolClass> findByName(String name);
    
    @Query("SELECT sc FROM SchoolClass sc WHERE SIZE(sc.registrations) < sc.maxStudents")
    List<SchoolClass> findAvailableClasses();
    
    @Query("SELECT sc FROM SchoolClass sc WHERE SIZE(sc.registrations) >= sc.maxStudents")
    List<SchoolClass> findFullClasses();
}
