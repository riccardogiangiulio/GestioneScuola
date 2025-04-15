package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    List<Registration> findByStudent(User student);
    
    List<Registration> findBySchoolClass(SchoolClass schoolClass);
    
    List<Registration> findByStatus(RegistrationStatus status);
    
    Optional<Registration> findByStudentAndSchoolClass(User student, SchoolClass schoolClass);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.schoolClass = :schoolClass AND r.status = 'ACTIVE'")
    Long countActiveRegistrationsBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
    
    @Query("SELECT r FROM Registration r WHERE r.schoolClass.course.id = :courseId")
    List<Registration> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT r FROM Registration r WHERE r.student = :student AND r.status = 'ACTIVE'")
    List<Registration> findActiveByStudent(@Param("student") User student);
    
    @Query("SELECT r FROM Registration r WHERE r.schoolClass = :schoolClass AND r.status = 'ACTIVE'")
    List<Registration> findActiveBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
}