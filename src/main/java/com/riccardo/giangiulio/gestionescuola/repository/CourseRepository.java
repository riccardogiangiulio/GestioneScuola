package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riccardo.giangiulio.gestionescuola.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findByTitle(String title);
    
}
