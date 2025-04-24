package com.riccardo.giangiulio.gestionescuola.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ExamNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomCapacityExceededException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomNotAvailableException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidExamDataException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTimeRangeException;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.ExamRepository;

@Service
public class ExamService {
    
    private static final Logger log = LoggerFactory.getLogger(ExamService.class);
    
    private final ExamRepository examRepository;
    private final ClassroomService classroomService;
    private final SchoolClassService schoolClassService;
    private final UserService userService;
    
    @Autowired
    public ExamService(
            ExamRepository examRepository,
            ClassroomService classroomService,
            SchoolClassService schoolClassService,
            UserService userService) {
        this.examRepository = examRepository;
        this.classroomService = classroomService;
        this.schoolClassService = schoolClassService;
        this.userService = userService;
        log.info("ExamService initialized");
    }
    
    public List<Exam> findAll() {
        log.debug("Retrieving all exams");
        List<Exam> exams = examRepository.findAll();
        log.info("Retrieved {} exams", exams.size());
        return exams;
    }
    
    public Exam findById(Long id) {
        log.debug("Finding exam with id: {}", id);
        return examRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Exam not found with ID: {}", id);
                return new ExamNotFoundException(id);
            });
    }
    
    @Transactional
    public Exam save(Exam exam) {
        log.info("Saving exam: {}", exam.getTitle());
        
        validateExam(exam);
        
        Exam savedExam = examRepository.save(exam);
        log.info("Exam saved successfully with id: {}", savedExam.getId());
        return savedExam;
    }
    
    @Transactional
    public Exam update(Long id, Exam exam) {
        log.info("Updating exam with id: {}", id);
        
        Exam existingExam = findById(id);
        
        existingExam.setTitle(exam.getTitle());
        existingExam.setDescription(exam.getDescription());
        existingExam.setDate(exam.getDate());
        existingExam.setDuration(exam.getDuration());
        existingExam.setMaxScore(exam.getMaxScore());
        existingExam.setPassingScore(exam.getPassingScore());
        existingExam.setClassroom(exam.getClassroom());
        existingExam.setSubject(exam.getSubject());
        existingExam.setSchoolClass(exam.getSchoolClass());
        existingExam.setTeacher(exam.getTeacher());
        
        validateExam(existingExam);
        
        Exam updatedExam = examRepository.save(existingExam);
        log.info("Exam updated successfully with ID: {}", id);
        return updatedExam;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete exam with id: {}", id);
        if (!examRepository.existsById(id)) {
            log.error("Attempting to delete non-existent exam with ID: {}", id);
            throw new ExamNotFoundException(id);
        }
        examRepository.deleteById(id);
        log.info("Exam deleted successfully with ID: {}", id);
    }
    
    public List<Exam> findByTitle(String title) {
        log.debug("Finding exams by title: {}", title);
        List<Exam> exams = examRepository.findByTitle(title);
        if (exams.isEmpty()) {
            log.warn("No exams found with title: {}", title);
        } else {
            log.info("Found {} exams with title: {}", exams.size(), title);
        }
        return exams;
    }
    
    public List<Exam> findByDate(LocalDateTime date) {
        log.debug("Finding exams by date: {}", date);
        List<Exam> exams = examRepository.findByDate(date);
        if (exams.isEmpty()) {
            log.warn("No exams found for date: {}", date);
        } else {
            log.info("Found {} exams for date: {}", exams.size(), date);
        }
        return exams;
    }
    
    public List<Exam> findBySubject(Subject subject) {
        log.debug("Finding exams for subject id: {}", subject.getId());
        List<Exam> exams = examRepository.findBySubject(subject);
        if (exams.isEmpty()) {
            log.warn("No exams found for subject ID: {}", subject.getId());
        }
        return exams;
    }
    
    public List<Exam> findBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding exams by school class id: {}", schoolClass.getId());
        List<Exam> exams = examRepository.findBySchoolClass(schoolClass);
        if (exams.isEmpty()) {
            log.warn("No exams found for school class ID: {}", schoolClass.getId());
        } else {
            log.info("Found {} exams for school class ID: {}", exams.size(), schoolClass.getId());
        }
        return exams;
    }
    
    public List<Exam> findByTeacher(User teacher) {
        log.debug("Finding exams for teacher id: {}", teacher.getId());
        List<Exam> exams = examRepository.findByTeacher(teacher);
        if (exams.isEmpty()) {
            log.warn("No exams found for teacher ID: {}", teacher.getId());
        }
        return exams;
    }
    
    public List<Exam> findByClassroom(Classroom classroom) {
        log.debug("Finding exams by classroom id: {}", classroom.getId());
        return examRepository.findByClassroom(classroom);
    }
    
    public List<Exam> findByCourse(Course course) {
        log.debug("Finding exams by course id: {}", course.getId());
        return examRepository.findByCourse(course);
    }
    
    public List<Exam> findByAnyCourseIn(List<Course> courses) {
        log.debug("Finding exams by any course in a list of {} courses", courses.size());
        return examRepository.findByAnyCourseIn(courses);
    }
    
    public List<Exam> findByDateRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding exams between {} and {}", start, end);
        
        if (start.isAfter(end)) {
            log.error("Invalid time range: start time {} is after end time {}", start, end);
            throw new InvalidTimeRangeException(start, end);
        }
        
        return examRepository.findByDateBetween(start, end);
    }
    
    public List<Exam> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        return findByDateRange(start, end);
    }
    
    public List<Exam> findUpcomingExams() {
        log.debug("Finding upcoming exams");
        List<Exam> exams = examRepository.findByDateAfter(LocalDateTime.now());
        if (exams.isEmpty()) {
            log.warn("No upcoming exams found");
        }
        return exams;
    }
    
    public List<Exam> findPastExams() {
        log.debug("Finding past exams");
        List<Exam> exams = examRepository.findByDateBefore(LocalDateTime.now());
        if (exams.isEmpty()) {
            log.warn("No past exams found");
        }
        return exams;
    }
    
    private void validateExam(Exam exam) {
        log.debug("Validating exam: {}", exam.getTitle());
        
        // Controlli di business essenziali
        if (exam.getDuration() <= 0) {
            log.error("Exam duration must be positive: {}", exam.getDuration());
            throw new InvalidExamDataException("La durata dell'esame deve essere positiva");
        }
        
        if (exam.getMaxScore() <= 0) {
            log.error("Max score must be positive: {}", exam.getMaxScore());
            throw new InvalidExamDataException("Il punteggio massimo deve essere positivo");
        }
        
        if (exam.getPassingScore() < 0 || exam.getPassingScore() > exam.getMaxScore()) {
            log.error("Passing score must be between 0 and max score: {}", exam.getPassingScore());
            throw new InvalidExamDataException("Il punteggio di sufficienza deve essere compreso tra 0 e il punteggio massimo");
        }
        
        // Verifica che l'insegnante sia valido
        if (exam.getTeacher() == null || exam.getTeacher().getId() == null) {
            log.error("Teacher is null or has null ID");
            throw new InvalidTeacherException(null);
        }
        
        User teacher = userService.findById(exam.getTeacher().getId());
        if (!userService.isTeacher(teacher)) {
            log.error("User {} is not a teacher", teacher.getId());
            throw new InvalidTeacherException(teacher.getId());
        }
        
        // Verifica classroom e schoolClass
        if (exam.getClassroom() == null || exam.getClassroom().getId() == null) {
            log.error("Classroom is null or has null ID");
            throw new InvalidExamDataException("L'aula non può essere nulla");
        }
        
        if (exam.getSchoolClass() == null || exam.getSchoolClass().getId() == null) {
            log.error("SchoolClass is null or has null ID");
            throw new InvalidExamDataException("La classe scolastica non può essere nulla");
        }
        
        Classroom classroom = classroomService.findById(exam.getClassroom().getId());
        SchoolClass schoolClass = schoolClassService.findById(exam.getSchoolClass().getId());
        
        // Verifica che l'aula abbia una capacità valida
        if (classroom.getCapacity() <= 0) {
            log.error("Classroom {} has invalid capacity: {}", classroom.getId(), classroom.getCapacity());
            throw new InvalidExamDataException("L'aula deve avere una capacità positiva");
        }
        
        // Verifica capienza aula
        int registrationsCount = schoolClass.getRegistrations() != null ? 
                                schoolClass.getRegistrations().size() : 0;
        if (registrationsCount > 0 && !classroomService.hasSufficientCapacity(classroom.getId(), registrationsCount)) {
            log.warn("Classroom {} has insufficient capacity for school class {}", 
                classroom.getId(), schoolClass.getId());
            throw new ClassroomCapacityExceededException(classroom.getId(), classroom.getCapacity(), 
                registrationsCount);
        }
        
        // Verifica disponibilità aula per l'esame
        LocalDateTime examEnd = exam.getDate().plusMinutes(exam.getDuration());
        if (!classroomService.isAvailableForTimeSlot(classroom.getId(), exam.getDate(), examEnd)) {
            log.warn("Classroom {} is not available for exam at {}", classroom.getId(), exam.getDate());
            throw new ClassroomNotAvailableException(classroom.getId(), exam.getDate(), examEnd);
        }
        
        log.debug("Exam validation completed successfully");
    }
}
