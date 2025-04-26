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
    
    @Query("SELECT u FROM User u WHERE u.role.name = :role")
    List<User> findByRole(@Param("role") ERole role);
    
    @Query("SELECT u FROM User u WHERE u.role.name = 'ROLE_STUDENT' AND u.id IN " +
           "(SELECT DISTINCT reg.student.id FROM Registration reg WHERE reg.schoolClass = :schoolClass AND reg.status = 'ACTIVE')")
    List<User> findStudentsBySchoolClass(@Param("schoolClass") SchoolClass schoolClass);
    
    @Query(value = "SELECT u.* FROM users u JOIN class_teacher ct ON u.id = ct.teacher_id WHERE ct.class_id = :schoolClassId", nativeQuery = true)
    List<User> findTeachersBySchoolClass(@Param("schoolClassId") Long schoolClassId);
    
    @Query("SELECT u FROM User u WHERE u.role.name = 'ROLE_STUDENT' AND u.id NOT IN " +
           "(SELECT DISTINCT reg.student.id FROM Registration reg WHERE reg.status = 'ACTIVE')")
    List<User> findStudentsWithoutActiveRegistrations();

    Optional<User> findByUsername(String username);
    
    Boolean existsByUsername(String username);
}
