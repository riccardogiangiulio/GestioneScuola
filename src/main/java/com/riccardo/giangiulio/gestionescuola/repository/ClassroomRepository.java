package com.riccardo.giangiulio.gestionescuola.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riccardo.giangiulio.gestionescuola.model.Classroom;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    
    Optional<Classroom> findByName(String name);
    
    List<Classroom> findByCapacityGreaterThanEqual(Integer minCapacity);
    
    @Query("SELECT c FROM Classroom c WHERE c.id NOT IN " +
           "(SELECT DISTINCT l.classroom.id FROM Lesson l WHERE " +
           "(l.startDateTime <= :end AND l.endDateTime >= :start))")
    List<Classroom> findAvailableClassroomsInTimeRange(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    @Query("SELECT cl FROM Classroom cl WHERE cl.capacity >= " +
           "(SELECT COUNT(r) FROM Registration r WHERE r.schoolClass.id = :schoolClassId AND r.status = 'ACTIVE')")
    List<Classroom> findClassroomsWithSufficientCapacityForSchoolClass(@Param("schoolClassId") Long schoolClassId);
}
