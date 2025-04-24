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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ExamResultNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidExamDataException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidStudentException;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.ExamRepository;
import com.riccardo.giangiulio.gestionescuola.repository.ExamResultRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ExamResultServiceIntegrationTest {
    
    @Autowired
    private ExamResultService examResultService;
    
    @Autowired
    private ExamResultRepository examResultRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private SchoolClassService schoolClassService;
    
    private Exam testExam;
    private User studentUser;
    private User teacherUser;
    private User nonStudentUser;
    private ExamResult testResult;
    
    @BeforeEach
    public void setUp() {
        examResultRepository.deleteAll();
        examRepository.deleteAll();
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        Role studentRole;
        Role teacherRole;
        Role adminRole;
        
        try {
            studentRole = roleService.getRoleByName(ERole.ROLE_STUDENT);
        } catch (Exception e) {
            studentRole = new Role(ERole.ROLE_STUDENT);
            studentRole = roleService.saveRole(studentRole);
        }
        
        try {
            teacherRole = roleService.getRoleByName(ERole.ROLE_TEACHER);
        } catch (Exception e) {
            teacherRole = new Role(ERole.ROLE_TEACHER);
            teacherRole = roleService.saveRole(teacherRole);
        }
        
        try {
            adminRole = roleService.getRoleByName(ERole.ROLE_ADMIN);
        } catch (Exception e) {
            adminRole = new Role(ERole.ROLE_ADMIN);
            adminRole = roleService.saveRole(adminRole);
        }
        
        studentUser = new User();
        studentUser.setFirstName("Student");
        studentUser.setLastName("Test");
        studentUser.setEmail("student_" + timestamp + "@example.com");
        studentUser.setPassword("password");
        studentUser.setBirthDate(LocalDate.of(2000, 1, 1));
        studentUser.setRole(studentRole);
        studentUser = userService.save(studentUser);
        
        teacherUser = new User();
        teacherUser.setFirstName("Teacher");
        teacherUser.setLastName("Test");
        teacherUser.setEmail("teacher_" + timestamp + "@example.com");
        teacherUser.setPassword("password");
        teacherUser.setBirthDate(LocalDate.of(1980, 1, 1));
        teacherUser.setRole(teacherRole);
        teacherUser = userService.save(teacherUser);
        
        nonStudentUser = new User();
        nonStudentUser.setFirstName("Admin");
        nonStudentUser.setLastName("Test");
        nonStudentUser.setEmail("admin_" + timestamp + "@example.com");
        nonStudentUser.setPassword("password");
        nonStudentUser.setBirthDate(LocalDate.of(1990, 1, 1));
        nonStudentUser.setRole(adminRole);
        nonStudentUser = userService.save(nonStudentUser);
        
        Subject subject = new Subject();
        subject.setName("Subject Test " + timestamp);
        subject.setDescription("Description subject test");
        subject.setTeacher(teacherUser);
        subject = subjectService.save(subject);
        
        Course course = new Course();
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
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName("Class Test " + timestamp);
        schoolClass.setMaxStudents(30);
        schoolClass.setCourse(course);
        schoolClass.setTeachers(teachers);
        schoolClass = schoolClassService.save(schoolClass);
        
        Classroom classroom = new Classroom("Classroom Test " + timestamp, 30);
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
        
        testResult = new ExamResult();
        testResult.setScore(25.0);
        testResult.setNotes("Excellent performance");
        testResult.setDate(LocalDateTime.now().minusDays(1)); // Data passata per rispettare @Past
        testResult.setExam(testExam);
        testResult.setStudent(studentUser);
        testResult = examResultRepository.save(testResult);
    }
    
    @Test
    public void testFindAll() {
        List<ExamResult> results = examResultService.findAll();
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(testResult.getId())));
    }
    
    @Test
    public void testFindById() {
        ExamResult found = examResultService.findById(testResult.getId());
        
        assertNotNull(found);
        assertEquals(testResult.getId(), found.getId());
        assertEquals(25.0, found.getScore());
        assertEquals("Excellent performance", found.getNotes());
        assertEquals(studentUser.getId(), found.getStudent().getId());
        assertEquals(testExam.getId(), found.getExam().getId());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 999999L;
        
        ExamResultNotFoundException exception = assertThrows(ExamResultNotFoundException.class, () -> {
            examResultService.findById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        ExamResult newResult = new ExamResult();
        newResult.setScore(20.0);
        newResult.setNotes("Good performance");
        newResult.setDate(LocalDateTime.now().minusDays(1));
        newResult.setExam(testExam);
        newResult.setStudent(studentUser);
        
        ExamResult saved = examResultService.save(newResult);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(20.0, saved.getScore());
        assertEquals("Good performance", saved.getNotes());
        
        ExamResult retrieved = examResultService.findById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
    }
    
    @Test
    public void testSaveWithInvalidStudent() {
        ExamResult invalidResult = new ExamResult();
        invalidResult.setScore(20.0);
        invalidResult.setNotes("Test note");
        invalidResult.setDate(LocalDateTime.now().minusDays(1));
        invalidResult.setExam(testExam);
        invalidResult.setStudent(nonStudentUser);
        
        InvalidStudentException exception = assertThrows(InvalidStudentException.class, () -> {
            examResultService.save(invalidResult);
        });
        assertNotNull("The user is not a student", exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidScore() {
        // Test con punteggio negativo
        ExamResult invalidResult = new ExamResult();
        invalidResult.setScore(-5.0); // Punteggio negativo non valido
        invalidResult.setNotes("Test Note");
        invalidResult.setDate(LocalDateTime.now().minusDays(1));
        invalidResult.setExam(testExam);
        invalidResult.setStudent(studentUser);
        
        InvalidExamDataException exception = assertThrows(InvalidExamDataException.class, () -> {
            examResultService.save(invalidResult);
        });
        assertNotNull("Invalid score", exception.getMessage());
        
        // Test con punteggio maggiore del massimo
        ExamResult invalidResult2 = new ExamResult();
        invalidResult2.setScore(35.0); // Maggiore del punteggio massimo (30.0)
        invalidResult2.setNotes("Test Note");
        invalidResult2.setDate(LocalDateTime.now().minusDays(1));
        invalidResult2.setExam(testExam);
        invalidResult2.setStudent(studentUser);
        
        InvalidExamDataException exception2 = assertThrows(InvalidExamDataException.class, () -> {
            examResultService.save(invalidResult2);
        });
        assertNotNull("Invalid score", exception2.getMessage());
    }
    
    @Test
    public void testUpdate() {
        ExamResult updateData = new ExamResult();
        updateData.setScore(28.0);
        updateData.setNotes("Excellent performance");
        updateData.setDate(testResult.getDate());
        updateData.setExam(testResult.getExam());
        updateData.setStudent(testResult.getStudent());
        
        ExamResult updated = examResultService.update(testResult.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(testResult.getId(), updated.getId());
        assertEquals(28.0, updated.getScore());
        assertEquals("Excellent performance", updated.getNotes());
        
        ExamResult retrieved = examResultService.findById(testResult.getId());
        assertEquals(28.0, retrieved.getScore());
        assertEquals("Excellent performance", retrieved.getNotes());
    }
    
    @Test
    public void testUpdateNotFound() {
        Long nonExistentId = 999999L;
        ExamResult updateData = new ExamResult();
        updateData.setScore(22.0);
        updateData.setNotes("Update note");
        updateData.setDate(LocalDateTime.now().minusDays(1));
        updateData.setExam(testExam);
        updateData.setStudent(studentUser);
        
        ExamResultNotFoundException exception = assertThrows(ExamResultNotFoundException.class, () -> {
            examResultService.update(nonExistentId, updateData);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        ExamResult resultToDelete = new ExamResult();
        resultToDelete.setScore(15.0);
        resultToDelete.setNotes("Result to delete");
        resultToDelete.setDate(LocalDateTime.now().minusDays(1));
        resultToDelete.setExam(testExam);
        resultToDelete.setStudent(studentUser);
        resultToDelete = examResultRepository.save(resultToDelete);
        
        Long idToDelete = resultToDelete.getId();
        
        examResultService.deleteById(idToDelete);
        
        ExamResultNotFoundException exception = assertThrows(ExamResultNotFoundException.class, () -> {
            examResultService.findById(idToDelete);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 999999L;
        
        ExamResultNotFoundException exception = assertThrows(ExamResultNotFoundException.class, () -> {
            examResultService.deleteById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByExam() {
        List<ExamResult> results = examResultService.findByExam(testExam);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(testResult.getId())));
    }
    
    @Test
    public void testFindByStudent() {
        List<ExamResult> results = examResultService.findByStudent(studentUser);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(testResult.getId())));
    }
    
    @Test
    public void testFindByStudentWithInvalidStudent() {
        InvalidStudentException exception = assertThrows(InvalidStudentException.class, () -> {
            examResultService.findByStudent(nonStudentUser);
        });
        assertNotNull("The user is not a student", exception.getMessage());
    }
    
    @Test
    public void testFindByExamAndStudent() {
        ExamResult found = examResultService.findByExamAndStudent(testExam, studentUser);
        
        assertNotNull(found);
        assertEquals(testResult.getId(), found.getId());
    }
    
    @Test
    public void testFindByExamAndStudentNotFound() {
        // Crea un nuovo studente che non ha fatto l'esame
        User newStudent = new User();
        String timestamp = String.valueOf(System.currentTimeMillis());
        newStudent.setFirstName("New Student");
        newStudent.setLastName("Test");
        newStudent.setEmail("new_student_" + timestamp + "@example.com");
        newStudent.setPassword("password");
        newStudent.setBirthDate(LocalDate.of(2001, 2, 2));
        newStudent.setRole(studentUser.getRole());
        User savedStudent = userService.save(newStudent);
        
        ExamResultNotFoundException exception = assertThrows(ExamResultNotFoundException.class, () -> {
            examResultService.findByExamAndStudent(testExam, savedStudent);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindAllPassed() {
        // Il risultato di test ha 25.0, che è superiore al passing score di 18.0
        List<ExamResult> passedResults = examResultService.findAllPassed();
        
        assertNotNull(passedResults);
        assertFalse(passedResults.isEmpty());
        assertTrue(passedResults.stream().anyMatch(r -> r.getId().equals(testResult.getId())));
        
        // Aggiungi un risultato non passato
        ExamResult failedResult = new ExamResult();
        failedResult.setScore(15.0); // Inferiore al passing score
        failedResult.setNotes("Insufficient result");
        failedResult.setDate(LocalDateTime.now().minusDays(1));
        failedResult.setExam(testExam);
        failedResult.setStudent(studentUser);
        ExamResult savedFailedResult = examResultRepository.save(failedResult);
        
        // Verifica che il nuovo risultato non sia incluso nei risultati passati
        passedResults = examResultService.findAllPassed();
        assertTrue(passedResults.stream().noneMatch(r -> r.getId().equals(savedFailedResult.getId())));
    }
    
    @Test
    public void testFindAllFailed() {
        // Aggiungi un risultato non passato
        ExamResult failedResult = new ExamResult();
        failedResult.setScore(15.0); // Inferiore al passing score
        failedResult.setNotes("Insufficient result");
        failedResult.setDate(LocalDateTime.now().minusDays(1));
        failedResult.setExam(testExam);
        failedResult.setStudent(studentUser);
        ExamResult savedFailedResult = examResultRepository.save(failedResult);
        
        List<ExamResult> failedResults = examResultService.findAllFailed();
        
        assertNotNull(failedResults);
        assertFalse(failedResults.isEmpty());
        assertTrue(failedResults.stream().anyMatch(r -> r.getId().equals(savedFailedResult.getId())));
        
        assertTrue(failedResults.stream().noneMatch(r -> r.getId().equals(testResult.getId())));
    }
    
    @Test
    public void testFindAverageScoreByExam() {
        ExamResult anotherResult = new ExamResult();
        anotherResult.setScore(15.0);
        anotherResult.setNotes("Another result");
        anotherResult.setDate(LocalDateTime.now().minusDays(1));
        anotherResult.setExam(testExam);
        anotherResult.setStudent(studentUser);
        examResultRepository.save(anotherResult);
        
        Double averageScore = examResultService.findAverageScoreByExam(testExam);
        
        assertNotNull(averageScore);
        assertEquals(20.0, averageScore); // (25.0 + 15.0) / 2 = 20.0
    }
    
    @Test
    public void testFindAverageScoreByStudent() {
        // Aggiungi un altro esame e risultato
        Exam anotherExam = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        anotherExam.setTitle("Another Exam " + timestamp);
        anotherExam.setDescription("Description another exam");
        anotherExam.setDate(LocalDateTime.now().plusDays(14));
        anotherExam.setDuration(90);
        anotherExam.setMaxScore(30.0);
        anotherExam.setPassingScore(18.0);
        anotherExam.setClassroom(testExam.getClassroom());
        anotherExam.setSubject(testExam.getSubject());
        anotherExam.setSchoolClass(testExam.getSchoolClass());
        anotherExam.setTeacher(testExam.getTeacher());
        anotherExam = examRepository.save(anotherExam);
        
        ExamResult anotherResult = new ExamResult();
        anotherResult.setScore(27.0);
        anotherResult.setNotes("Another result");
        anotherResult.setDate(LocalDateTime.now().minusDays(1));
        anotherResult.setExam(anotherExam);
        anotherResult.setStudent(studentUser);
        examResultRepository.save(anotherResult);
        
        Double averageScore = examResultService.findAverageScoreByStudent(studentUser);
        
        assertNotNull(averageScore);
        assertEquals(26.0, averageScore); // (25.0 + 27.0) / 2 = 26.0
    }
    
    @Test
    public void testCountPassedByExam() {
        // Aggiungi un altro risultato passato
        User anotherStudent = new User();
        String timestamp = String.valueOf(System.currentTimeMillis());
        anotherStudent.setFirstName("Another Student");
        anotherStudent.setLastName("Test");
        anotherStudent.setEmail("another_student_" + timestamp + "@example.com");
        anotherStudent.setPassword("password");
        anotherStudent.setBirthDate(LocalDate.of(2001, 3, 3));
        anotherStudent.setRole(studentUser.getRole());
        anotherStudent = userService.save(anotherStudent);
        
        ExamResult anotherPassedResult = new ExamResult();
        anotherPassedResult.setScore(20.0); // Superiore al passing score
        anotherPassedResult.setNotes("Sufficient result");
        anotherPassedResult.setDate(LocalDateTime.now().minusDays(1));
        anotherPassedResult.setExam(testExam);
        anotherPassedResult.setStudent(anotherStudent);
        examResultService.save(anotherPassedResult);
        
        // Aggiungi un risultato non passato
        ExamResult failedResult = new ExamResult();
        failedResult.setScore(15.0); // Inferiore al passing score
        failedResult.setNotes("Risultato insufficiente");
        failedResult.setDate(LocalDateTime.now().minusDays(1));
        failedResult.setExam(testExam);
        failedResult.setStudent(studentUser);
        examResultService.save(failedResult);
        
        Long passedCount = examResultService.countPassedByExam(testExam);
        
        assertEquals(2L, passedCount); // testResult e anotherPassedResult
    }
    
    @Test
    public void testCountPassedByStudent() {
        // Aggiungi un altro esame con risultato passato
        Exam anotherExam = new Exam();
        String timestamp = String.valueOf(System.currentTimeMillis());
        anotherExam.setTitle("Another Exam " + timestamp);
        anotherExam.setDescription("Description another exam");
        anotherExam.setDate(LocalDateTime.now().plusDays(14));
        anotherExam.setDuration(90);
        anotherExam.setMaxScore(30.0);
        anotherExam.setPassingScore(18.0);
        anotherExam.setClassroom(testExam.getClassroom());
        anotherExam.setSubject(testExam.getSubject());
        anotherExam.setSchoolClass(testExam.getSchoolClass());
        anotherExam.setTeacher(testExam.getTeacher());
        anotherExam = examRepository.save(anotherExam);
        
        ExamResult anotherPassedResult = new ExamResult();
        anotherPassedResult.setScore(20.0); // Superiore al passing score
        anotherPassedResult.setNotes("Another passed result");
        anotherPassedResult.setDate(LocalDateTime.now().minusDays(1));
        anotherPassedResult.setExam(anotherExam);
        anotherPassedResult.setStudent(studentUser);
        examResultService.save(anotherPassedResult);
        
        // Aggiungi un esame con risultato non passato
        Exam thirdExam = new Exam();
        thirdExam.setTitle("Third Exam " + timestamp);
        thirdExam.setDescription("Description third exam");
        thirdExam.setDate(LocalDateTime.now().plusDays(21));
        thirdExam.setDuration(90);
        thirdExam.setMaxScore(30.0);
        thirdExam.setPassingScore(18.0);
        thirdExam.setClassroom(testExam.getClassroom());
        thirdExam.setSubject(testExam.getSubject());
        thirdExam.setSchoolClass(testExam.getSchoolClass());
        thirdExam.setTeacher(testExam.getTeacher());
        thirdExam = examRepository.save(thirdExam);
        
        ExamResult failedResult = new ExamResult();
        failedResult.setScore(15.0); // Inferiore al passing score
        failedResult.setNotes("Insufficient result");
        failedResult.setDate(LocalDateTime.now().minusDays(1));
        failedResult.setExam(thirdExam);
        failedResult.setStudent(studentUser);
        examResultService.save(failedResult);
        
        Long passedCount = examResultService.countPassedByStudent(studentUser);
        
        assertEquals(2L, passedCount); // testResult e anotherPassedResult
    }
}
