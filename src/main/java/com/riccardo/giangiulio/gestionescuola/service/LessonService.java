package com.riccardo.giangiulio.gestionescuola.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.LessonNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomCapacityExceededException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomNotAvailableException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTimeRangeException;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.LessonRepository;

@Service
public class LessonService {

    private static final Logger log = LoggerFactory.getLogger(LessonService.class);

    private final LessonRepository lessonRepository;
    private final ClassroomService classroomService;
    private final UserService userService;
    private final SchoolClassService schoolClassService;
    private final SubjectService subjectService;

    @Autowired
    public LessonService(
            LessonRepository lessonRepository,
            ClassroomService classroomService,
            UserService userService,
            SchoolClassService schoolClassService,
            SubjectService subjectService) {
        this.lessonRepository = lessonRepository;
        this.classroomService = classroomService;
        this.userService = userService;
        this.schoolClassService = schoolClassService;
        this.subjectService = subjectService;
        log.info("LessonService initialized");
    }

    public List<Lesson> findAll() {
        log.debug("Retrieving all lessons");
        return lessonRepository.findAll();
    }

    public Lesson findById(Long id) {
        log.debug("Finding lesson with id: {}", id);
        return lessonRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Lesson not found with ID: {}", id);
                return new LessonNotFoundException(id);
            });
    }

    public List<Lesson> findByTeacher(User teacher) {
        log.debug("Finding lessons for teacher id: {}", teacher.getId());
        List<Lesson> lessons = lessonRepository.findByTeacher(teacher);
        if (lessons.isEmpty()) {
            log.warn("No lessons found for teacher ID: {}", teacher.getId());
        }
        return lessons;
    }

    public List<Lesson> findBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding lessons for school class id: {}", schoolClass.getId());
        List<Lesson> lessons = lessonRepository.findBySchoolClass(schoolClass);
        if (lessons.isEmpty()) {
            log.warn("No lessons found for school class ID: {}", schoolClass.getId());
        }
        return lessons;
    }

    public List<Lesson> findByClassroom(Classroom classroom) {
        log.debug("Finding lessons for classroom id: {}", classroom.getId());
        List<Lesson> lessons = lessonRepository.findByClassroom(classroom);
        if (lessons.isEmpty()) {
            log.warn("No lessons found for classroom ID: {}", classroom.getId());
        }
        return lessons;
    }

    public List<Lesson> findBySubject(Subject subject) {
        log.debug("Finding lessons for subject id: {}", subject.getId());
        List<Lesson> lessons = lessonRepository.findBySubject(subject);
        if (lessons.isEmpty()) {
            log.warn("No lessons found for subject ID: {}", subject.getId());
        }
        return lessons;
    }

    public List<Lesson> findByDateRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding lessons between {} and {}", start, end);
        if (start.isAfter(end)) {
            log.error("Invalid time range: start time {} is after end time {}", start, end);
            throw new InvalidTimeRangeException(start, end);
        }
        List<Lesson> lessons = lessonRepository.findByStartDateTimeBetween(start, end);
        if (lessons.isEmpty()) {
            log.warn("No lessons found in the specified time range");
        }
        return lessons;
    }
    
    public List<Lesson> findUpcomingLessonsBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding upcoming lessons for school class id: {}", schoolClass.getId());
        List<Lesson> lessons = lessonRepository.findUpcomingLessonsBySchoolClass(schoolClass);
        if (lessons.isEmpty()) {
            log.warn("No upcoming lessons found for school class ID: {}", schoolClass.getId());
        }
        return lessons;
    }
    
    public List<Lesson> findTodayLessons() {
        log.debug("Finding today's lessons");
        List<Lesson> lessons = lessonRepository.findTodayLessons();
        if (lessons.isEmpty()) {
            log.warn("No lessons found for today");
        }
        return lessons;
    }

    @Transactional
    public Lesson save(Lesson lesson) {
        log.debug("Saving lesson for subject {} and teacher {}", 
            lesson.getSubject().getId(), lesson.getTeacher().getId());
        
        validateLesson(lesson);
        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Lesson saved successfully with ID: {}", savedLesson.getId());
        return savedLesson;
    }

    @Transactional
    public Lesson update(Long id, Lesson lesson) {
        log.debug("Updating lesson with id: {}", id);
        Optional<Lesson> existingLessonOptional = lessonRepository.findById(id);

        if (!existingLessonOptional.isPresent()) {
            log.error("Attempting to update non-existent lesson with ID: {}", id);
            throw new LessonNotFoundException(id);
        }

        Lesson existingLesson = existingLessonOptional.get();
        existingLesson.setTitle(lesson.getTitle());
        existingLesson.setDescription(lesson.getDescription());
        existingLesson.setStartDateTime(lesson.getStartDateTime());
        existingLesson.setEndDateTime(lesson.getEndDateTime());
        existingLesson.setClassroom(lesson.getClassroom());
        existingLesson.setTeacher(lesson.getTeacher());
        existingLesson.setSchoolClass(lesson.getSchoolClass());
        existingLesson.setSubject(lesson.getSubject());
        
        validateLesson(existingLesson);
        Lesson updatedLesson = lessonRepository.save(existingLesson);
        log.info("Lesson updated successfully with ID: {}", id);
        return updatedLesson;
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting lesson with id: {}", id);
        if (!lessonRepository.existsById(id)) {
            log.error("Attempting to delete non-existent lesson with ID: {}", id);
            throw new LessonNotFoundException(id);
        }
        lessonRepository.deleteById(id);
        log.info("Lesson deleted successfully with ID: {}", id);
    }

    private void validateLesson(Lesson lesson) {
        log.debug("Validating lesson for school class {} and teacher {}", 
            lesson.getSchoolClass().getId(), lesson.getTeacher().getId());
        
        // Validazione della relazione temporale tra start e end
        if (lesson.getEndDateTime().isBefore(lesson.getStartDateTime())) {
            log.warn("Attempting to save lesson with invalid end date and time: {}", lesson.getEndDateTime());
            throw new InvalidTimeRangeException(lesson.getStartDateTime(), lesson.getEndDateTime());
        }
        
        // Verifica che l'aula esista e abbia capacità sufficiente
        Classroom classroom = classroomService.findById(lesson.getClassroom().getId());
        if (!classroomService.hasSufficientCapacity(classroom.getId(), lesson.getSchoolClass().getRegistrations().size())) {
            log.warn("Classroom {} has insufficient capacity for school class {}", 
                classroom.getId(), lesson.getSchoolClass().getId());
            throw new ClassroomCapacityExceededException(classroom.getId(), classroom.getCapacity(), 
                lesson.getSchoolClass().getRegistrations().size());
        }
        
        // Verifica che l'insegnante esista e sia effettivamente un insegnante
        User teacher = userService.findById(lesson.getTeacher().getId());
        if (!userService.isTeacher(teacher)) {
            log.error("Attempting to save lesson with non-teacher user ID: {}", teacher.getId());
            throw new InvalidTeacherException(teacher.getId());
        }
        
        // Verifica che la classe scolastica esista
        schoolClassService.findById(lesson.getSchoolClass().getId());

        // Verifica che la materia esista
        subjectService.findById(lesson.getSubject().getId());
        
        List<Lesson> conflictingLessons = lessonRepository.findByStartDateTimeBetween(
            lesson.getStartDateTime().minusHours(24), // Marge di sicurezza per prendere tutte le possibili sovrapposizioni 
            lesson.getEndDateTime().plusHours(24)     // Marge di sicurezza per prendere tutte le possibili sovrapposizioni
        );
        
        // Filtra le lezioni per trovare quelle che si sovrappongono nel periodo richiesto
        // ed esclude la lezione stessa se ha già un ID (aggiornamento)
        boolean isNotAvailable = conflictingLessons.stream()
            .filter(l -> l.getClassroom().getId().equals(lesson.getClassroom().getId())) // Solo per l'aula richiesta
            .filter(l -> (l.getStartDateTime().isBefore(lesson.getEndDateTime()) && 
                         l.getEndDateTime().isAfter(lesson.getStartDateTime()))) // Controllo sovrapposizione
            .anyMatch(l -> !l.getId().equals(lesson.getId())); // Esclude la lezione stessa
        
        if (isNotAvailable) {
            log.warn("Classroom {} is not available in the specified time slot", lesson.getClassroom().getId());
            throw new ClassroomNotAvailableException(lesson.getClassroom().getId(), 
                lesson.getStartDateTime(), lesson.getEndDateTime());
        }
        
        log.debug("Lesson validation completed successfully");
    }
} 