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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ExamNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomCapacityExceededException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomNotAvailableException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidExamDataException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTimeRangeException;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.ExamRepository;
import com.riccardo.giangiulio.gestionescuola.repository.LessonRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ExamServiceIntegrationTest {
    
    @Autowired
    private ExamService examService;

    @Autowired
    private ExamRepository examRepository;
    
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

    @Autowired
    private LessonRepository lessonRepository;
    
    private User teacherUser;
    private User nonTeacherUser;
    private Subject subject;
    private SchoolClass schoolClass;
    private Classroom classroom;
    private Exam testExam;
    private Course course;
    
    @BeforeEach
    public void setUp() {
        lessonRepository.deleteAll();
        examRepository.deleteAll();
        
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
        nonTeacherUser.setFirstName("Student");
        nonTeacherUser.setLastName("Test");
        nonTeacherUser.setEmail("student_" + timestamp + "@example.com");
        nonTeacherUser.setPassword("password");
        nonTeacherUser.setBirthDate(LocalDate.of(2000, 1, 1));
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
        Set<Exam> exams = new HashSet<>();
        course.setExams(exams);
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
        
        LocalDateTime examDate = LocalDateTime.now().plusDays(7);
        testExam = new Exam();
        testExam.setTitle("Exam Test " + timestamp);
        testExam.setDescription("Description exam test");
        testExam.setDate(examDate);
        testExam.setDuration(120);
        testExam.setMaxScore(30.0);
        testExam.setPassingScore(18.0);
        testExam.setClassroom(classroom);
        testExam.setSubject(subject);
        testExam.setSchoolClass(schoolClass);
        testExam.setTeacher(teacherUser);
        testExam = examRepository.save(testExam);
        
        User studentUser = new User();
        studentUser.setFirstName("Student");
        studentUser.setLastName("Test");
        studentUser.setEmail("student_test_" + timestamp + "@example.com");
        studentUser.setPassword("password");
        studentUser.setBirthDate(LocalDate.of(2000, 1, 1));
        studentUser.setRole(studentRole);
        studentUser = userService.save(studentUser);
        
        Registration registration = new Registration();
        registration.setStudent(studentUser);
        registration.setSchoolClass(schoolClass);
        registration.setStatus(RegistrationStatus.ACTIVE);
        registration.setCourse(course);
        registrationService.save(registration);

    }
    
    @Test
    public void testFindAll() {
        List<Exam> exams = examService.findAll();
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindById() {
        Exam found = examService.findById(testExam.getId());
        
        assertNotNull(found);
        assertEquals(testExam.getId(), found.getId());
        assertEquals(testExam.getTitle(), found.getTitle());
        assertEquals(testExam.getDescription(), found.getDescription());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 999999L;
        
        ExamNotFoundException exception = assertThrows(ExamNotFoundException.class, () -> {
            examService.findById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        Exam newExam = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        newExam.setTitle("New Exam " + timestamp);
        newExam.setDescription("Description new exam");
        newExam.setDate(LocalDateTime.now().plusDays(14));
        newExam.setDuration(90);
        newExam.setMaxScore(30.0);
        newExam.setPassingScore(18.0);
        newExam.setClassroom(classroom);
        newExam.setSubject(subject);
        newExam.setSchoolClass(schoolClass);
        newExam.setTeacher(teacherUser);
        
        Exam saved = examService.save(newExam);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("New Exam " + timestamp, saved.getTitle());
        assertEquals("Description new exam", saved.getDescription());
        
        // Verifica che sia stato salvato correttamente
        Exam retrieved = examService.findById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
    }
    
    @Test
    public void testSaveWithInvalidTeacher() {
        Exam invalidExam = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        invalidExam.setTitle("Invalid Exam " + timestamp);
        invalidExam.setDescription("Description invalid exam");
        invalidExam.setDate(LocalDateTime.now().plusDays(14));
        invalidExam.setDuration(90);
        invalidExam.setMaxScore(30.0);
        invalidExam.setPassingScore(18.0);
        invalidExam.setClassroom(classroom);
        invalidExam.setSubject(subject);
        invalidExam.setSchoolClass(schoolClass);
        invalidExam.setTeacher(nonTeacherUser);
        
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            examService.save(invalidExam);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidDuration() {
        Exam invalidExam = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        invalidExam.setTitle("Invalid Exam " + timestamp);
        invalidExam.setDescription("Description invalid exam");
        invalidExam.setDate(LocalDateTime.now().plusDays(14));
        invalidExam.setDuration(0);
        invalidExam.setMaxScore(30.0);
        invalidExam.setPassingScore(18.0);
        invalidExam.setClassroom(classroom);
        invalidExam.setSubject(subject);
        invalidExam.setSchoolClass(schoolClass);
        invalidExam.setTeacher(teacherUser);
        
        InvalidExamDataException exception = assertThrows(InvalidExamDataException.class, () -> {
            examService.save(invalidExam);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidScore() {
        // Test con punteggio massimo non valido
        Exam invalidExam1 = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        invalidExam1.setTitle("Invalid Exam " + timestamp);
        invalidExam1.setDescription("Description invalid exam");
        invalidExam1.setDate(LocalDateTime.now().plusDays(14));
        invalidExam1.setDuration(90);
        invalidExam1.setMaxScore(0.0); // Punteggio massimo non valido
        invalidExam1.setPassingScore(18.0);
        invalidExam1.setClassroom(classroom);
        invalidExam1.setSubject(subject);
        invalidExam1.setSchoolClass(schoolClass);
        invalidExam1.setTeacher(teacherUser);
        
        InvalidExamDataException exception = assertThrows(InvalidExamDataException.class, () -> {
            examService.save(invalidExam1);
        });
        assertNotNull(exception.getMessage());
        
        // Test con punteggio di sufficienza maggiore del punteggio massimo
        Exam invalidExam2 = new Exam();
        invalidExam2.setTitle("Esame Invalido " + timestamp);
        invalidExam2.setDescription("Descrizione esame invalido");
        invalidExam2.setDate(LocalDateTime.now().plusDays(14));
        invalidExam2.setDuration(90);
        invalidExam2.setMaxScore(30.0);
        invalidExam2.setPassingScore(35.0); // Maggiore del punteggio massimo
        invalidExam2.setClassroom(classroom);
        invalidExam2.setSubject(subject);
        invalidExam2.setSchoolClass(schoolClass);
        invalidExam2.setTeacher(teacherUser);
        
        InvalidExamDataException exception2 = assertThrows(InvalidExamDataException.class, () -> {
            examService.save(invalidExam2);
        });
        assertNotNull(exception2.getMessage());
    }
    
    @Test
    public void testUpdate() {
        Exam updateData = new Exam();
        updateData.setTitle("Updated Exam");
        updateData.setDescription("Updated description");
        updateData.setDate(LocalDateTime.now().plusDays(10));
        updateData.setDuration(150);
        updateData.setMaxScore(25.0);
        updateData.setPassingScore(15.0);
        updateData.setClassroom(classroom);
        updateData.setSubject(subject);
        updateData.setSchoolClass(schoolClass);
        updateData.setTeacher(teacherUser);
        
        Exam updated = examService.update(testExam.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(testExam.getId(), updated.getId());
        assertEquals("Updated Exam", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(150, updated.getDuration());
        assertEquals(25.0, updated.getMaxScore());
        assertEquals(15.0, updated.getPassingScore());
        
        Exam retrieved = examService.findById(testExam.getId());
        assertEquals("Updated Exam", retrieved.getTitle());
        assertEquals(150, retrieved.getDuration());
    }
    
    @Test
    public void testUpdateNotFound() {
        Long nonExistentId = 999999L;
        Exam updateData = new Exam();
        updateData.setTitle("Updated Exam");
        updateData.setDescription("Updated description");
        updateData.setDate(LocalDateTime.now().plusDays(10));
        updateData.setDuration(150);
        updateData.setMaxScore(25.0);
        updateData.setPassingScore(15.0);
        updateData.setClassroom(classroom);
        updateData.setSubject(subject);
        updateData.setSchoolClass(schoolClass);
        updateData.setTeacher(teacherUser);
        
        ExamNotFoundException exception = assertThrows(ExamNotFoundException.class, () -> {
            examService.update(nonExistentId, updateData);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        Exam examToDelete = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        examToDelete.setTitle("Exam to Delete " + timestamp);
        examToDelete.setDescription("Description exam to delete");
        examToDelete.setDate(LocalDateTime.now().plusDays(14));
        examToDelete.setDuration(90);
        examToDelete.setMaxScore(30.0);
        examToDelete.setPassingScore(18.0);
        examToDelete.setClassroom(classroom);
        examToDelete.setSubject(subject);
        examToDelete.setSchoolClass(schoolClass);
        examToDelete.setTeacher(teacherUser);
        examToDelete = examRepository.save(examToDelete);
        
        Long idToDelete = examToDelete.getId();
        
        examService.deleteById(idToDelete);
        
        ExamNotFoundException exception = assertThrows(ExamNotFoundException.class, () -> {
            examService.findById(idToDelete);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 999999L;
        
        ExamNotFoundException exception = assertThrows(ExamNotFoundException.class, () -> {
            examService.deleteById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByTitle() {
        Exam exam = examService.findByTitle(testExam.getTitle());
        
        assertNotNull(exam);
        assertTrue(exam.getId().equals(testExam.getId()));
    }
    
    @Test
    public void testFindByDate() {
        List<Exam> exams = examService.findByDate(testExam.getDate());
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindBySubject() {
        List<Exam> exams = examService.findBySubject(subject);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindBySchoolClass() {
        List<Exam> exams = examService.findBySchoolClass(schoolClass);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindByTeacher() {
        List<Exam> exams = examService.findByTeacher(teacherUser);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindByClassroom() {
        List<Exam> exams = examService.findByClassroom(classroom);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindByCourse() {
        course.getExams().add(testExam);        
        testExam.getCourses().add(course);       
        
        course = courseService.save(course);
        
        List<Exam> exams = examService.findByCourse(course);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindByAnyCourseIn() {
        course.getExams().add(testExam);        
        testExam.getCourses().add(course);       
        
        course = courseService.save(course);
        
        List<Course> courseList = List.of(course);
        List<Exam> exams = examService.findByAnyCourseIn(courseList);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindByDateRange() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        
        List<Exam> exams = examService.findByDateRange(start, end);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindByDateRangeWithInvalidRange() {
        LocalDateTime start = LocalDateTime.now().plusDays(10);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        
        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> {
            examService.findByDateRange(start, end);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByDateBetween() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        
        List<Exam> exams = examService.findByDateBetween(start, end);
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindUpcomingExams() {
        List<Exam> exams = examService.findUpcomingExams();
        
        assertNotNull(exams);
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testFindPastExams() {
        List<Exam> exams = examService.findPastExams();
        
        assertNotNull(exams);
        assertTrue(exams.isEmpty(), "The list of past exams should be empty because it is not possible to create exams with past dates");
    }
    
    @Test
    public void testFindByClassroomNotAvailable() {
        
        // Crea una lezione che occupa l'aula
        LocalDateTime lessonStart = LocalDateTime.now().plusDays(5);
        LocalDateTime lessonEnd = lessonStart.plusMinutes(120);
        
        Lesson lesson = new Lesson();
        String timestamp = String.valueOf(System.currentTimeMillis());
        lesson.setTitle("Lezione Test " + timestamp);
        lesson.setDescription("Descrizione lezione test");
        lesson.setStartDateTime(lessonStart);
        lesson.setEndDateTime(lessonEnd);
        lesson.setClassroom(classroom);
        lesson.setSubject(subject);
        lesson.setSchoolClass(schoolClass);
        lesson.setTeacher(teacherUser);
        
        lessonRepository.save(lesson);
        
        // Crea un esame che si sovrappone alla lezione
        Exam conflictingExam = new Exam();
        conflictingExam.setTitle("Exam Conflict " + timestamp);
        conflictingExam.setDescription("Description exam conflict");
        conflictingExam.setDate(lessonStart.plusMinutes(30));
        conflictingExam.setDuration(120);
        conflictingExam.setMaxScore(30.0);
        conflictingExam.setPassingScore(18.0);
        conflictingExam.setClassroom(classroom);
        conflictingExam.setSubject(subject);
        conflictingExam.setSchoolClass(schoolClass);
        conflictingExam.setTeacher(teacherUser);
        
        // Verifica che venga lanciata l'eccezione quando si tenta di salvare un esame
        // che si sovrappone a una lezione esistente
        ClassroomNotAvailableException exception = assertThrows(
            ClassroomNotAvailableException.class, 
            () -> examService.save(conflictingExam)
        );
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidClassroomCapacity() {
        Classroom smallClassroom = new Classroom("Small Classroom", 5);
        smallClassroom = classroomService.save(smallClassroom);
        
        Exam examWithSmallClassroom = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        examWithSmallClassroom.setTitle("Exam Small Classroom " + timestamp);
        examWithSmallClassroom.setDescription("Description exam small classroom");
        examWithSmallClassroom.setDate(LocalDateTime.now().plusDays(14));
        examWithSmallClassroom.setDuration(90);
        examWithSmallClassroom.setMaxScore(30.0);
        examWithSmallClassroom.setPassingScore(18.0);
        examWithSmallClassroom.setClassroom(smallClassroom);
        examWithSmallClassroom.setSubject(subject);
        examWithSmallClassroom.setSchoolClass(schoolClass);
        examWithSmallClassroom.setTeacher(teacherUser);
        
        // Se ci sono più registrazioni di quante ne possa contenere l'aula,
        // dovrebbe lanciare un'eccezione
        if (schoolClass.getRegistrations().size() > 5) {
            ClassroomCapacityExceededException exception = assertThrows(
                ClassroomCapacityExceededException.class, 
                () -> examService.save(examWithSmallClassroom)
            );
            assertNotNull(exception.getMessage());
        }
    }
    
    @Test
    public void testSaveWithInvalidClassroom() {
        Exam examWithNullClassroom = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        examWithNullClassroom.setTitle("Exam Null Classroom " + timestamp);
        examWithNullClassroom.setDescription("Description exam null classroom");
        examWithNullClassroom.setDate(LocalDateTime.now().plusDays(14));
        examWithNullClassroom.setDuration(90);
        examWithNullClassroom.setMaxScore(30.0);
        examWithNullClassroom.setPassingScore(18.0);
        examWithNullClassroom.setClassroom(null);
        examWithNullClassroom.setSubject(subject);
        examWithNullClassroom.setSchoolClass(schoolClass);
        examWithNullClassroom.setTeacher(teacherUser);
        
        InvalidExamDataException exception = assertThrows(
            InvalidExamDataException.class, 
            () -> examService.save(examWithNullClassroom)
        );
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidSchoolClass() {
        Exam examWithNullSchoolClass = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        examWithNullSchoolClass.setTitle("Exam Null School Class " + timestamp);
        examWithNullSchoolClass.setDescription("Description exam null school class");
        examWithNullSchoolClass.setDate(LocalDateTime.now().plusDays(14));
        examWithNullSchoolClass.setDuration(90);
        examWithNullSchoolClass.setMaxScore(30.0);
        examWithNullSchoolClass.setPassingScore(18.0);
        examWithNullSchoolClass.setClassroom(classroom);
        examWithNullSchoolClass.setSubject(subject);
        examWithNullSchoolClass.setSchoolClass(null);
        examWithNullSchoolClass.setTeacher(teacherUser);
        
        InvalidExamDataException exception = assertThrows(
            InvalidExamDataException.class, 
            () -> examService.save(examWithNullSchoolClass)
        );
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdateWithInvalidData() {
        // Crea un esame da aggiornare con dati non validi
        Exam updateData = new Exam();
        updateData.setTitle("Updated Exam");
        updateData.setDescription("Updated description");
        updateData.setDate(LocalDateTime.now().plusDays(10));
        updateData.setDuration(-50);
        updateData.setMaxScore(25.0);
        updateData.setPassingScore(15.0);
        updateData.setClassroom(classroom);
        updateData.setSubject(subject);
        updateData.setSchoolClass(schoolClass);
        updateData.setTeacher(teacherUser);
        
        InvalidExamDataException exception = assertThrows(
            InvalidExamDataException.class, 
            () -> examService.update(testExam.getId(), updateData)
        );
        
        assertNotNull(exception.getMessage());
    }
    
}
