package com.riccardo.giangiulio.gestionescuola.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.LessonNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomCapacityExceededException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomNotAvailableException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTimeRangeException;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.LessonRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LessonServiceIntegrationTest {
    
    @Autowired
    private LessonService lessonService;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private SchoolClassService schoolClassService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private RegistrationService registrationService;
    
    private User teacherUser;
    private User nonTeacherUser;
    private User studentUser;
    private Subject subject;
    private SchoolClass schoolClass;
    private Classroom classroom;
    private Lesson testLesson;
    private Course course;
    
    @BeforeEach
    public void setUp() {
        lessonRepository.deleteAll();
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        Role teacherRole;
        Role studentRole;
        
        try {
            teacherRole = roleService.getRoleByName(ERole.ROLE_TEACHER);
        } catch (Exception e) {
            teacherRole = new Role(ERole.ROLE_TEACHER);
            teacherRole = roleService.saveRole(teacherRole);
        }
        
        try {
            studentRole = roleService.getRoleByName(ERole.ROLE_STUDENT);
        } catch (Exception e) {
            studentRole = new Role(ERole.ROLE_STUDENT);
            studentRole = roleService.saveRole(studentRole);
        }
        
        teacherUser = new User();
        teacherUser.setFirstName("Teacher");
        teacherUser.setLastName("Test");
        teacherUser.setEmail("teacher_" + timestamp + "@example.com");
        teacherUser.setPassword("password");
        teacherUser.setBirthDate(LocalDate.of(1980, 1, 1));
        teacherUser.setRole(teacherRole);
        teacherUser = userService.save(teacherUser);
        
        nonTeacherUser = new User();
        nonTeacherUser.setFirstName("Non Teacher");
        nonTeacherUser.setLastName("Test");
        nonTeacherUser.setEmail("non_teacher_" + timestamp + "@example.com");
        nonTeacherUser.setPassword("password");
        nonTeacherUser.setBirthDate(LocalDate.of(1990, 1, 1));
        nonTeacherUser.setRole(studentRole);
        nonTeacherUser = userService.save(nonTeacherUser);
        
        subject = new Subject();
        subject.setName("Subject Test " + timestamp);
        subject.setDescription("Description subject test");
        subject.setTeacher(teacherUser);
        subject = subjectService.save(subject);
        
        course = new Course();
        course.setTitle("Course Test " + timestamp);
        course.setDescription("Description course test");
        course.setDuration("12 months");
        course.setPrice(java.math.BigDecimal.valueOf(1000.0));
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        course.setSubjects(subjects);
        course = courseService.save(course);
        
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        schoolClass = new SchoolClass();
        schoolClass.setName("Class Test " + timestamp);
        schoolClass.setMaxStudents(30);
        schoolClass.setCourse(course);
        schoolClass.setTeachers(teachers);
        schoolClass = schoolClassService.save(schoolClass);
        
        classroom = new Classroom("Classroom Test " + timestamp, 50);
        classroom = classroomService.save(classroom);
        
        studentUser = new User();
        studentUser.setFirstName("Student");
        studentUser.setLastName("Test");
        studentUser.setEmail("student_" + timestamp + "@example.com");
        studentUser.setPassword("password");
        studentUser.setBirthDate(LocalDate.of(2000, 1, 1));
        studentUser.setRole(studentRole);
        studentUser = userService.save(studentUser);

        Registration registration = new Registration();
        registration.setStudent(studentUser);
        registration.setSchoolClass(schoolClass);
        registration.setStatus(RegistrationStatus.ACTIVE);
        registration.setCourse(course);
        
        Set<Registration> registrations = new HashSet<>();
        registrations.add(registration);
        schoolClass.setRegistrations(registrations);

        schoolClassService.save(schoolClass);
        
        registrationService.save(registration);

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(7);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        testLesson = new Lesson(
            "Lesson Test " + timestamp,
            "Description lesson test",
            startDateTime,
            endDateTime,
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        testLesson = lessonRepository.save(testLesson);
    }
    
    @Test
    public void testFindAll() {
        // Crea un'altra lezione
        Lesson anotherLesson = new Lesson(
            "Another Lesson Test",
            "Description another lesson test",
            LocalDateTime.now().plusDays(8),
            LocalDateTime.now().plusDays(8).plusHours(2),
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        lessonRepository.save(anotherLesson);
        
        // Ottieni tutte le lezioni
        List<Lesson> lessons = lessonService.findAll();
        
        // Verifica se ci sono almeno due lezioni
        assertNotNull(lessons);
        assertTrue(lessons.size() >= 2);
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(testLesson.getId())));
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(anotherLesson.getId())));
    }
    
    @Test
    public void testFindById() {
        Lesson foundLesson = lessonService.findById(testLesson.getId());
        
        assertNotNull(foundLesson);
        assertEquals(testLesson.getId(), foundLesson.getId());
        assertEquals(testLesson.getTitle(), foundLesson.getTitle());
        assertEquals(testLesson.getDescription(), foundLesson.getDescription());
    }
    
    @Test
    public void testFindByIdNotFound() {
        // Prova a ottenere una lezione con ID non esistente
        Long nonExistentId = 9999L;
        
        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class, () -> {
            lessonService.findById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        // Ricarica la classe per ottenere le registrazioni aggiornate
        schoolClass = schoolClassService.findById(schoolClass.getId());
        
        assertFalse(schoolClass.getRegistrations().isEmpty(), "The class must have at least one registration");
        
        // Crea una nuova lezione
        String title = "New Lesson Test";
        String description = "Description new lesson test";
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(10);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        
        Lesson newLesson = new Lesson(
            title,
            description,
            startDateTime,
            endDateTime,
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        
        Lesson savedLesson = lessonService.save(newLesson);
        
        assertNotNull(savedLesson);
        assertNotNull(savedLesson.getId());
        assertEquals(title, savedLesson.getTitle());
        assertEquals(description, savedLesson.getDescription());
        assertEquals(startDateTime, savedLesson.getStartDateTime());
        assertEquals(endDateTime, savedLesson.getEndDateTime());
        assertEquals(schoolClass.getId(), savedLesson.getSchoolClass().getId());
        assertEquals(teacherUser.getId(), savedLesson.getTeacher().getId());
        assertEquals(classroom.getId(), savedLesson.getClassroom().getId());
        assertEquals(subject.getId(), savedLesson.getSubject().getId());
    }
    
    @Test
    public void testSaveWithInvalidTimeRange() {
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(10);
        LocalDateTime endDateTime = startDateTime.minusHours(1);
        
        Lesson invalidLesson = new Lesson(
            "Lesson with invalid time range",
            "Description lesson with invalid time range",
            startDateTime,
            endDateTime,
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        
        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> {
            lessonService.save(invalidLesson);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidTeacher() {
        // Crea una lezione con un utente che non è un insegnante
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(10);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        
        Lesson invalidLesson = new Lesson(
            "Lesson with invalid teacher",
            "Description lesson with invalid teacher",
            startDateTime,
            endDateTime,
            schoolClass,
            nonTeacherUser,
            classroom,
            subject
        );
        
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            lessonService.save(invalidLesson);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithClassroomCapacityExceeded() {
        // Aggiungi un altro studente alla classe per avere 2 registrazioni
        User secondStudent = new User();
        secondStudent.setFirstName("Studente2");
        secondStudent.setLastName("Test");
        secondStudent.setEmail("studente2_" + System.currentTimeMillis() + "@example.com");
        secondStudent.setPassword("password");
        secondStudent.setBirthDate(LocalDate.of(2000, 1, 1));
        secondStudent.setRole(roleService.getRoleByName(ERole.ROLE_STUDENT));
        secondStudent = userService.save(secondStudent);
        
        // Crea una nuova registrazione per il secondo studente
        Registration registration2 = new Registration();
        registration2.setStudent(secondStudent);
        registration2.setSchoolClass(schoolClass);
        registration2.setStatus(RegistrationStatus.ACTIVE);
        registration2.setCourse(course);
        registrationService.save(registration2);
        
        // Aggiorna la classe con la nuova registrazione
        Set<Registration> registrations = new HashSet<>(schoolClass.getRegistrations());
        registrations.add(registration2);
        schoolClass.setRegistrations(registrations);
        schoolClassService.save(schoolClass);
        
        schoolClass = schoolClassService.findById(schoolClass.getId());
        
        assertEquals(2, schoolClass.getRegistrations().size(), "The class must have 2 registrations");
        
        Classroom smallClassroom = new Classroom("Small Classroom", 1);
        smallClassroom = classroomService.save(smallClassroom);
        
        // Crea una lezione che usa l'aula piccola
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(10);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        
        Lesson invalidLesson = new Lesson(
            "Lesson with too small classroom",
            "Description lesson with too small classroom",
            startDateTime,
            endDateTime,
            schoolClass,
            teacherUser,
            smallClassroom,
            subject
        );
        
        // Verifica che venga lanciata l'eccezione
        ClassroomCapacityExceededException exception = assertThrows(ClassroomCapacityExceededException.class, () -> {
            lessonService.save(invalidLesson);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithClassroomNotAvailable() {
        // Crea prima una lezione per occupare l'aula
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(15);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        
        Lesson firstLesson = new Lesson(
            "First Lesson Test",
            "Description first lesson test",
            startDateTime,
            endDateTime,
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        lessonService.save(firstLesson);
        
        // Crea una seconda lezione che si sovrappone temporalmente alla prima
        LocalDateTime overlappingStart = startDateTime.plusMinutes(30); // Inizia durante la prima lezione
        LocalDateTime overlappingEnd = endDateTime.plusMinutes(30); // Finisce dopo la prima lezione
        
        Lesson overlappingLesson = new Lesson(
            "Overlapping Lesson Test",
            "Description overlapping lesson test",
            overlappingStart,
            overlappingEnd,
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        
        // Verifica che venga lanciata l'eccezione
        ClassroomNotAvailableException exception = assertThrows(ClassroomNotAvailableException.class, () -> {
            lessonService.save(overlappingLesson);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdate() {
        Long lessonId = testLesson.getId();
        
        Classroom originalClassroom = testLesson.getClassroom();

        LocalDateTime newStartDateTime = LocalDateTime.now().plusDays(30);
        LocalDateTime newEndDateTime = newStartDateTime.plusHours(2);
        
        // Crea l'oggetto di aggiornamento mantenendo gli stessi altri dati ma modificando solo quelli che vogliamo testare
        Lesson updateData = new Lesson();
        updateData.setTitle("Lesson Updated");
        updateData.setDescription("Updated description");
        updateData.setStartDateTime(newStartDateTime);
        updateData.setEndDateTime(newEndDateTime);
        updateData.setClassroom(originalClassroom);
        updateData.setSchoolClass(schoolClass);
        updateData.setTeacher(teacherUser);
        updateData.setSubject(subject);

        Lesson updated = lessonService.update(lessonId, updateData);
        
        assertNotNull(updated);
        assertEquals(lessonId, updated.getId());
        assertEquals("Lesson Updated", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(newStartDateTime, updated.getStartDateTime());
        assertEquals(newEndDateTime, updated.getEndDateTime());
        assertEquals(originalClassroom.getId(), updated.getClassroom().getId());
        assertEquals(schoolClass.getId(), updated.getSchoolClass().getId());
        assertEquals(teacherUser.getId(), updated.getTeacher().getId());
        assertEquals(subject.getId(), updated.getSubject().getId());
        
        Lesson retrieved = lessonService.findById(lessonId);
        assertEquals("Lesson Updated", retrieved.getTitle());
        assertEquals("Updated description", retrieved.getDescription());
        assertEquals(newStartDateTime, retrieved.getStartDateTime());
        assertEquals(newEndDateTime, retrieved.getEndDateTime());
        assertEquals(originalClassroom.getId(), retrieved.getClassroom().getId());
        
        Classroom newClassroom = new Classroom("Classroom Test Update " + System.currentTimeMillis(), 50);
        classroomService.save(newClassroom);
        
        updateData.setClassroom(newClassroom);
        
        Lesson updatedWithNewClassroom = lessonService.update(lessonId, updateData);
        assertEquals(newClassroom.getId(), updatedWithNewClassroom.getClassroom().getId());
    }
    
    @Test
    public void testUpdateNotFound() {
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(14);
        LocalDateTime endDateTime = startDateTime.plusHours(3);
        
        Lesson lessonToUpdate = new Lesson(
            "Updated Title",
            "Updated Description",
            startDateTime,
            endDateTime,
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        
        // Prova ad aggiornare una lezione con ID non esistente
        Long nonExistentId = 9999L;
        
        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class, () -> {
            lessonService.update(nonExistentId, lessonToUpdate);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        lessonService.deleteById(testLesson.getId());
        
        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class, () -> {
            lessonService.findById(testLesson.getId());
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        // Prova a eliminare una lezione con ID non esistente
        Long nonExistentId = 9999L;
        
        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class, () -> {
            lessonService.deleteById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByTeacher() {
        // Trova lezioni per insegnante
        List<Lesson> lessons = lessonService.findByTeacher(teacherUser);
        
        assertNotNull(lessons);
        assertFalse(lessons.isEmpty());
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(testLesson.getId())));
    }
    
    @Test
    public void testFindBySchoolClass() {
        // Trova lezioni per classe
        List<Lesson> lessons = lessonService.findBySchoolClass(schoolClass);
        
        assertNotNull(lessons);
        assertFalse(lessons.isEmpty());
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(testLesson.getId())));
    }
    
    @Test
    public void testFindByClassroom() {
        // Trova lezioni per aula
        List<Lesson> lessons = lessonService.findByClassroom(classroom);
        
        assertNotNull(lessons);
        assertFalse(lessons.isEmpty());
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(testLesson.getId())));
    }
    
    @Test
    public void testFindBySubject() {
        // Trova lezioni per materia
        List<Lesson> lessons = lessonService.findBySubject(subject);
        
        assertNotNull(lessons);
        assertFalse(lessons.isEmpty());
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(testLesson.getId())));
    }
    
    @Test
    public void testFindByDateRange() {
        // Definisci un intervallo di date che includa la lezione di test
        LocalDateTime start = testLesson.getStartDateTime().minusDays(1);
        LocalDateTime end = testLesson.getEndDateTime().plusDays(1);
        
        List<Lesson> lessons = lessonService.findByDateRange(start, end);
        
        assertNotNull(lessons);
        assertFalse(lessons.isEmpty());
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(testLesson.getId())));
    }
    
    @Test
    public void testFindByDateRangeWithInvalidRange() {
        // Definisci un intervallo di date non valido (inizio dopo fine)
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = LocalDateTime.now().plusDays(3); // Prima dell'inizio
        
        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> {
            lessonService.findByDateRange(start, end);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindUpcomingLessonsBySchoolClass() {
        // Trova lezioni future per la classe
        List<Lesson> lessons = lessonService.findUpcomingLessonsBySchoolClass(schoolClass);
        
        assertNotNull(lessons);
        assertFalse(lessons.isEmpty());
        assertTrue(lessons.stream().anyMatch(l -> l.getId().equals(testLesson.getId())));
    }
    
    @Test
    public void testFindTodayLessons() {
        // Crea una lezione per oggi
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startToday = today.withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endToday = today.withHour(12).withMinute(0).withSecond(0).withNano(0);
        
        Lesson todayLesson = new Lesson(
            "Today Lesson",
            "Today lesson description",
            startToday,
            endToday,
            schoolClass,
            teacherUser,
            classroom,
            subject
        );
        lessonRepository.save(todayLesson);
        
        List<Lesson> todayLessons = lessonService.findTodayLessons();
        
        assertNotNull(todayLessons);
        assertFalse(todayLessons.isEmpty());
        assertTrue(todayLessons.stream().anyMatch(l -> l.getId().equals(todayLesson.getId())));
    }
}
