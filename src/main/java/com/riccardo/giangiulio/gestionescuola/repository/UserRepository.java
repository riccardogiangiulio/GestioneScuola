package com.riccardo.giangiulio.gestionescuola.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    List<User> findByRole(@Param("role") ERole role);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_STUDENT' AND u.id IN " +
           "(SELECT DISTINCT reg.student.id FROM Registration reg WHERE reg.schoolClass = :schoolClass AND reg.status = 'ACTIVE')")
    List<User> findStudentsBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
    
    @Query("SELECT DISTINCT t FROM SchoolClass sc JOIN sc.teachers t WHERE sc = :schoolClass")
    List<User> findTeachersBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_STUDENT' AND u.id NOT IN " +
           "(SELECT DISTINCT reg.student.id FROM Registration reg WHERE reg.status = 'ACTIVE')")
    List<User> findStudentsWithoutActiveRegistrations();
}
