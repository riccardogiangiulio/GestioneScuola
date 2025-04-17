package com.riccardo.giangiulio.gestionescuola.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                return new RuntimeException("Lesson not found with ID: " + id);
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
            throw new RuntimeException("Start time cannot be after end time");
        }
        List<Lesson> lessons = lessonRepository.findByDateTimeBetween(start, end);
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
        return lessonRepository.save(lesson);
    }

    @Transactional
    public Lesson update(Long id, Lesson lesson) {
        log.debug("Updating lesson with id: {}", id);
        Optional<Lesson> existingLessonOptional = lessonRepository.findById(id);

        if (!existingLessonOptional.isPresent()) {
            log.error("Attempting to update non-existent lesson with ID: {}", id);
            throw new RuntimeException("Lesson not found with ID: " + id);
        }

        Lesson existingLesson = existingLessonOptional.get();
        existingLesson.setStartDateTime(lesson.getStartDateTime());
        existingLesson.setEndDateTime(lesson.getEndDateTime());
        existingLesson.setClassroom(lesson.getClassroom());
        existingLesson.setTeacher(lesson.getTeacher());
        existingLesson.setSchoolClass(lesson.getSchoolClass());
        existingLesson.setSubject(lesson.getSubject());
        
        validateLesson(existingLesson);
        return lessonRepository.save(existingLesson);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting lesson with id: {}", id);
        if (!lessonRepository.existsById(id)) {
            log.error("Attempting to delete non-existent lesson with ID: {}", id);
            throw new RuntimeException("Lesson not found with ID: " + id);
        }
        lessonRepository.deleteById(id);
    }

    private void validateLesson(Lesson lesson) {
        // Validazione della relazione temporale tra start e end
        if (lesson.getEndDateTime().isBefore(lesson.getStartDateTime())) {
            log.warn("Attempting to save lesson with invalid end date and time: {}", lesson.getEndDateTime());
            throw new RuntimeException("Lesson end date and time cannot be before start date and time");
        }
        
        // Validazioni di business con servizi esterni
        try {
            Classroom classroom = classroomService.findById(lesson.getClassroom().getId());
            if (!classroomService.hasSufficientCapacity(classroom.getId(), lesson.getSchoolClass().getRegistrations().size())) {
                log.warn("Classroom {} has insufficient capacity for school class {}", 
                    classroom.getId(), lesson.getSchoolClass().getId());
                throw new RuntimeException("Classroom capacity is insufficient for the school class");
            }
        } catch (RuntimeException e) {
            log.error("Invalid classroom ID {} in lesson validation", lesson.getClassroom().getId());
            throw new RuntimeException("Invalid classroom ID: " + lesson.getClassroom().getId());
        }
        
        try {
            User teacher = userService.findById(lesson.getTeacher().getId());
            if (!userService.isTeacher(teacher)) {
                log.error("Attempting to save lesson with non-teacher user ID: {}", teacher.getId());
                throw new RuntimeException("The specified user is not a teacher");
            }
        } catch (RuntimeException e) {
            log.error("Invalid teacher ID {} in lesson validation", lesson.getTeacher().getId());
            throw new RuntimeException("Invalid teacher ID: " + lesson.getTeacher().getId());
        }
        
        try {
            schoolClassService.findById(lesson.getSchoolClass().getId());
        } catch (RuntimeException e) {
            log.error("Invalid school class ID {} in lesson validation", lesson.getSchoolClass().getId());
            throw new RuntimeException("Invalid school class ID: " + lesson.getSchoolClass().getId());
        }
        
        try {
            subjectService.findById(lesson.getSubject().getId());
        } catch (RuntimeException e) {
            log.error("Invalid subject ID {} in lesson validation", lesson.getSubject().getId());
            throw new RuntimeException("Invalid subject ID: " + lesson.getSubject().getId());
        }
        
        if (!classroomService.isAvailableForTimeSlot(
            lesson.getClassroom().getId(), 
            lesson.getStartDateTime(), 
            lesson.getEndDateTime())) {
            log.warn("Classroom {} is not available in the specified time slot", lesson.getClassroom().getId());
            throw new RuntimeException("Classroom is not available in the specified time slot");
        }
    }
} 