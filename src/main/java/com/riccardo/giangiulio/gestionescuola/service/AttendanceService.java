package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.AttendanceNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidStudentException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTimeRangeException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.TimeOutOfBoundsExceptions;
import com.riccardo.giangiulio.gestionescuola.model.Attendance;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.AttendanceRepository;

@Service
public class AttendanceService {
    
    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);
    
    private final AttendanceRepository attendanceRepository;
    private final UserService userService;
    private final LessonService lessonService;
    
    @Autowired
    public AttendanceService(
            AttendanceRepository attendanceRepository,
            UserService userService,
            LessonService lessonService) {
        this.attendanceRepository = attendanceRepository;
        this.userService = userService;
        this.lessonService = lessonService;
        log.info("AttendanceService initialized");
    }
    
    public List<Attendance> findAll() {
        log.debug("Retrieving all attendances");
        return attendanceRepository.findAll();
    }
    
    public Attendance findById(Long id) {
        log.debug("Finding attendance with id: {}", id);
        return attendanceRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Attendance not found with ID: {}", id);
                return new AttendanceNotFoundException(id);
            });
    }
    
    public Attendance save(Attendance attendanceRequest) {
        log.debug("Saving attendance for lesson {} and student {}", 
            attendanceRequest.getLesson().getId(), attendanceRequest.getStudent().getId());
        
        // Pre-carica lo studente completo
        User student = userService.findById(attendanceRequest.getStudent().getId());
        
        // Pre-carica la lezione completa
        Lesson lesson = lessonService.findById(attendanceRequest.getLesson().getId());
        
        // Crea una nuova Attendance con le entità complete
        Attendance attendance = new Attendance();
        attendance.setPresent(attendanceRequest.getPresent());
        attendance.setEntryTime(attendanceRequest.getEntryTime());
        attendance.setExitTime(attendanceRequest.getExitTime());
        attendance.setLesson(lesson);
        attendance.setStudent(student);
        
        validateAttendance(attendance);
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        log.info("Attendance saved successfully with ID: {}", savedAttendance.getId());
        return savedAttendance;
    }
    
    @Transactional
    public Attendance update(Long id, Attendance attendance) {
        log.debug("Updating attendance with id: {}", id);
        Attendance existingAttendance = findById(id);
        
        if (attendance.getPresent() != null) {
            existingAttendance.setPresent(attendance.getPresent());
        }
        if (attendance.getEntryTime() != null) {
            existingAttendance.setEntryTime(attendance.getEntryTime());
        }
        if (attendance.getExitTime() != null) {
            existingAttendance.setExitTime(attendance.getExitTime());
        }
        if (attendance.getLesson() != null && attendance.getLesson().getId() != null) {
            existingAttendance.setLesson(lessonService.findById(attendance.getLesson().getId()));
        }
        if (attendance.getStudent() != null && attendance.getStudent().getId() != null) {
            existingAttendance.setStudent(userService.findById(attendance.getStudent().getId()));
        }
        
        validateAttendance(existingAttendance);
        Attendance updatedAttendance = attendanceRepository.save(existingAttendance);
        log.info("Attendance updated successfully with ID: {}", id);
        return updatedAttendance;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete attendance with id: {}", id);
        if (!attendanceRepository.existsById(id)) {
            log.error("Failed to delete attendance: Not found with ID: {}", id);
            throw new AttendanceNotFoundException(id);
        }
        attendanceRepository.deleteById(id);
        log.info("Attendance deleted successfully with ID: {}", id);
    }
    
    public List<Attendance> findByLesson(Lesson lesson) {
        log.debug("Finding attendances for lesson id: {}", lesson.getId());
        List<Attendance> attendances = attendanceRepository.findByLesson(lesson);
        if (attendances.isEmpty()) {
            log.warn("No attendances found for lesson ID: {}", lesson.getId());
        }
        return attendances;
    }
    
    public List<Attendance> findByStudent(User student) {
        log.debug("Finding attendances for student id: {}", student.getId());
        List<Attendance> attendances = attendanceRepository.findByStudent(student);
        if (attendances.isEmpty()) {
            log.warn("No attendances found for student ID: {}", student.getId());
        }
        return attendances;
    }
    
    public Attendance findByLessonAndStudent(Lesson lesson, User student) {
        log.debug("Finding attendance for lesson {} and student {}", lesson.getId(), student.getId());
        return attendanceRepository.findByLessonAndStudent(lesson, student)
            .orElseThrow(() -> {
                log.error("Attendance not found for lesson {} and student {}", lesson.getId(), student.getId());
                return new AttendanceNotFoundException(lesson.getId(), student.getId());
            });
    }
    
    public List<Attendance> findByPresent(Boolean present) {
        log.debug("Finding attendances with presence status: {}", present);
        List<Attendance> attendances = attendanceRepository.findByPresent(present);
        if (attendances.isEmpty()) {
            log.warn("No attendances found with presence status: {}", present);
        }
        return attendances;
    }
    
    public List<Attendance> findBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding attendances for school class id: {}", schoolClass.getId());
        List<Attendance> attendances = attendanceRepository.findBySchoolClass(schoolClass);
        if (attendances.isEmpty()) {
            log.warn("No attendances found for school class ID: {}", schoolClass.getId());
        }
        return attendances;
    }
    
    public Long countPresentByStudent(User student) {
        log.debug("Counting present attendances for student id: {}", student.getId());
        return attendanceRepository.countPresentByStudent(student);
    }
    
    public Long countAbsentByStudent(User student) {
        log.debug("Counting absent attendances for student id: {}", student.getId());
        return attendanceRepository.countAbsentByStudent(student);
    }
    
    private void validateAttendance(Attendance attendance) {
        // Lo studente è già stato caricato dal controller, usa direttamente quello
        User student = attendance.getStudent();
        
        // Ora controlla se è uno studente
        if (!userService.isStudent(student)) {
            log.error("Cannot save attendance: user {} is not a student", student.getId());
            throw new InvalidStudentException(student.getId());
        }
        
        // Verifica che l'orario di uscita sia dopo l'orario di entrata
        if (attendance.getExitTime().isBefore(attendance.getEntryTime())) {
            log.error("Invalid exit time: {} is before entry time {}", 
                attendance.getExitTime(), attendance.getEntryTime());
            throw new InvalidTimeRangeException(attendance.getEntryTime(), attendance.getExitTime());
        }
        
        // Verifica che l'orario di entrata e uscita siano compatibili con l'orario della lezione
        Lesson lesson = attendance.getLesson(); // Usa la lezione già caricata
        if (attendance.getEntryTime().isBefore(lesson.getStartDateTime()) || 
            attendance.getExitTime().isAfter(lesson.getEndDateTime())) {
            log.error("Attendance times are outside lesson time range");
            throw new TimeOutOfBoundsExceptions(lesson.getStartDateTime(), attendance.getEntryTime(), attendance.getExitTime());
        }
    }
}