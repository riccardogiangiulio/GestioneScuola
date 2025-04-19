package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    Optional<Subject> findByName(String name);
    
    @Query("SELECT s FROM Subject s JOIN s.courses c WHERE c = :course")
    List<Subject> findByCourse(@Param("course") Course course);
}
