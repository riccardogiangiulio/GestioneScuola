package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riccardo.giangiulio.gestionescuola.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByTitle(String title);
    
    List<Course> findByTitleContainingIgnoreCase(String keyword);
    
}
