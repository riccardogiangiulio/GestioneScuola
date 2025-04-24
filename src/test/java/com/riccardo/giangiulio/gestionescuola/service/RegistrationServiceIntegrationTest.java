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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.RegistrationNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.DuplicateRegistrationException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidStudentException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.SchoolClassFullException;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegistrationServiceIntegrationTest {
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private SchoolClassService schoolClassService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private SubjectService subjectService;
    
    private User studentUser;
    private User teacherUser;
    private User nonStudentUser;
    private Role studentRole;
    private Role teacherRole;
    private Role adminRole;
    private SchoolClass schoolClass;
    private Course course;
    private Registration testRegistration;
    
    @BeforeEach
    public void setUp() {
        // Ottieni o crea ruoli
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
        
        // Genera timestamp unico
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // Crea utenti
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
        
        // Crea materia
        Subject subject = new Subject();
        subject.setName("Subject Test " + timestamp);
        subject.setDescription("Description subject test");
        subject.setTeacher(teacherUser);
        subject = subjectService.save(subject);
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        
        // Crea corso
        course = new Course();
        course.setTitle("Course Test " + timestamp);
        course.setDescription("Description course test");
        course.setDuration("12 months");
        course.setPrice(java.math.BigDecimal.valueOf(1000.0));
        course.setSubjects(subjects);
        course = courseService.save(course);
        
        // Crea classe
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        schoolClass = new SchoolClass();
        schoolClass.setName("Classe Test " + timestamp);
        schoolClass.setMaxStudents(30);
        schoolClass.setCourse(course);
        schoolClass.setTeachers(teachers);
        schoolClass = schoolClassService.save(schoolClass);
        
        // Crea una registrazione
        testRegistration = new Registration();
        testRegistration.setStudent(studentUser);
        testRegistration.setSchoolClass(schoolClass);
        testRegistration.setStatus(RegistrationStatus.ACTIVE);
        testRegistration.setCourse(course);
        testRegistration.setRegistrationDate(LocalDateTime.now());
        testRegistration = registrationService.save(testRegistration);
    }
    
    @Test
    public void testFindAll() {
        List<Registration> registrations = registrationService.findAll();
        
        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
        assertTrue(registrations.stream().anyMatch(r -> r.getId().equals(testRegistration.getId())));
    }
    
    @Test
    public void testFindById() {
        Registration found = registrationService.findById(testRegistration.getId());
        
        assertNotNull(found);
        assertEquals(testRegistration.getId(), found.getId());
        assertEquals(studentUser.getId(), found.getStudent().getId());
        assertEquals(schoolClass.getId(), found.getSchoolClass().getId());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 9999L;
        
        RegistrationNotFoundException exception = assertThrows(RegistrationNotFoundException.class, () -> {
            registrationService.findById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByStudent() {
        List<Registration> registrations = registrationService.findByStudent(studentUser);
        
        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
        assertTrue(registrations.stream().anyMatch(r -> r.getId().equals(testRegistration.getId())));
    }
    
    @Test
    public void testFindBySchoolClass() {
        List<Registration> registrations = registrationService.findBySchoolClass(schoolClass);
        
        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
        assertTrue(registrations.stream().anyMatch(r -> r.getId().equals(testRegistration.getId())));
    }
    
    @Test
    public void testFindByStatus() {
        List<Registration> registrations = registrationService.findByStatus(RegistrationStatus.ACTIVE);
        
        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
        assertTrue(registrations.stream().anyMatch(r -> r.getId().equals(testRegistration.getId())));
    }
    
    @Test
    public void testFindByStudentAndSchoolClass() {
        Registration found = registrationService.findByStudentAndSchoolClass(studentUser, schoolClass);
        
        assertNotNull(found);
        assertEquals(testRegistration.getId(), found.getId());
    }
    
    @Test
    public void testFindByStudentAndSchoolClassNotFound() {
        // Crea un altro studente senza registrazione
        User anotherStudent = new User();
        anotherStudent.setFirstName("Other");
        anotherStudent.setLastName("Student");
        anotherStudent.setEmail("other_student_" + System.currentTimeMillis() + "@example.com");
        anotherStudent.setPassword("password");
        anotherStudent.setBirthDate(LocalDate.of(2001, 1, 1));
        anotherStudent.setRole(studentRole);
        userService.save(anotherStudent);
        
        RegistrationNotFoundException exception = assertThrows(RegistrationNotFoundException.class, () -> {
            registrationService.findByStudentAndSchoolClass(anotherStudent, schoolClass);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testCountActiveRegistrationsBySchoolClass() {
        Long count = registrationService.countActiveRegistrationsBySchoolClass(schoolClass);
        
        assertEquals(1, count);
    }
    
    @Test
    public void testFindByCourseId() {
        List<Registration> registrations = registrationService.findByCourseId(course.getId());
        
        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
        assertTrue(registrations.stream().anyMatch(r -> r.getId().equals(testRegistration.getId())));
    }
    
    @Test
    public void testFindActiveByStudent() {
        List<Registration> registrations = registrationService.findActiveByStudent(studentUser);
        
        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
        assertTrue(registrations.stream().anyMatch(r -> r.getId().equals(testRegistration.getId())));
    }
    
    @Test
    public void testFindActiveBySchoolClass() {
        List<Registration> registrations = registrationService.findActiveBySchoolClass(schoolClass);
        
        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
        assertTrue(registrations.stream().anyMatch(r -> r.getId().equals(testRegistration.getId())));
    }
    
    @Test
    public void testSave() {
        // Crea un altro studente
        User anotherStudent = new User();
        anotherStudent.setFirstName("New");
        anotherStudent.setLastName("Student");
        anotherStudent.setEmail("new_student_" + System.currentTimeMillis() + "@example.com");
        anotherStudent.setPassword("password");
        anotherStudent.setBirthDate(LocalDate.of(2001, 1, 1));
        anotherStudent.setRole(studentRole);
        anotherStudent = userService.save(anotherStudent);
        
        // Crea una nuova registrazione
        Registration newRegistration = new Registration();
        newRegistration.setStudent(anotherStudent);
        newRegistration.setSchoolClass(schoolClass);
        newRegistration.setStatus(RegistrationStatus.ACTIVE);
        newRegistration.setCourse(course);
        newRegistration.setRegistrationDate(LocalDateTime.now());
        
        Registration saved = registrationService.save(newRegistration);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(anotherStudent.getId(), saved.getStudent().getId());
        assertEquals(schoolClass.getId(), saved.getSchoolClass().getId());
        assertEquals(RegistrationStatus.ACTIVE, saved.getStatus());
    }
    
    @Test
    public void testSaveWithInvalidStudent() {
        // Tenta di registrare un utente non studente
        Registration invalidRegistration = new Registration();
        invalidRegistration.setStudent(nonStudentUser); // Utente non studente
        invalidRegistration.setSchoolClass(schoolClass);
        invalidRegistration.setStatus(RegistrationStatus.ACTIVE);
        invalidRegistration.setCourse(course);
        
        InvalidStudentException exception = assertThrows(InvalidStudentException.class, () -> {
            registrationService.save(invalidRegistration);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithDuplicateRegistration() {
        // Tenta di registrare lo stesso studente nella stessa classe
        Registration duplicateRegistration = new Registration();
        duplicateRegistration.setStudent(studentUser); // Già registrato nella classe
        duplicateRegistration.setSchoolClass(schoolClass);
        duplicateRegistration.setStatus(RegistrationStatus.ACTIVE);
        duplicateRegistration.setCourse(course);
        
        DuplicateRegistrationException exception = assertThrows(DuplicateRegistrationException.class, () -> {
            registrationService.save(duplicateRegistration);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithFullSchoolClass() {
        // Crea una classe con numero massimo di studenti = 1 (già raggiunto)
        SchoolClass smallClass = new SchoolClass();
        smallClass.setName("Full Class " + System.currentTimeMillis());
        smallClass.setMaxStudents(1); // Solo uno studente
        smallClass.setCourse(course);
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        smallClass.setTeachers(teachers);
        smallClass = schoolClassService.save(smallClass);
        
        // Crea un primo studente che riempie la classe
        User firstStudent = new User();
        firstStudent.setFirstName("First");
        firstStudent.setLastName("Student");
        firstStudent.setEmail("first_student_" + System.currentTimeMillis() + "@example.com");
        firstStudent.setPassword("password");
        firstStudent.setBirthDate(LocalDate.of(2001, 1, 1));
        firstStudent.setRole(studentRole);
        firstStudent = userService.save(firstStudent);
        
        Registration firstRegistration = new Registration();
        firstRegistration.setStudent(firstStudent);
        firstRegistration.setSchoolClass(smallClass);
        firstRegistration.setStatus(RegistrationStatus.ACTIVE);
        firstRegistration.setCourse(course);
        firstRegistration = registrationService.save(firstRegistration);
        
        // Aggiorna la classe per riflettere la registrazione
        Set<Registration> registrations = new HashSet<>();
        registrations.add(firstRegistration);
        smallClass.setRegistrations(registrations);
        smallClass = schoolClassService.save(smallClass);
        
        // Ora tenta di registrare un altro studente
        User secondStudent = new User();
        secondStudent.setFirstName("Second");
        secondStudent.setLastName("Student");
        secondStudent.setEmail("second_student_" + System.currentTimeMillis() + "@example.com");
        secondStudent.setPassword("password");
        secondStudent.setBirthDate(LocalDate.of(2002, 1, 1));
        secondStudent.setRole(studentRole);
        secondStudent = userService.save(secondStudent);
        
        Registration secondRegistration = new Registration();
        secondRegistration.setStudent(secondStudent);
        secondRegistration.setSchoolClass(smallClass);
        secondRegistration.setStatus(RegistrationStatus.ACTIVE);
        secondRegistration.setCourse(course);
        
        SchoolClassFullException exception = assertThrows(SchoolClassFullException.class, () -> {
            registrationService.save(secondRegistration);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdate() {
        // Cambia lo stato della registrazione
        Registration updateData = new Registration();
        updateData.setStatus(RegistrationStatus.COMPLETED);
        updateData.setSchoolClass(schoolClass);
        updateData.setStudent(studentUser);
        updateData.setCourse(course);
        
        Registration updated = registrationService.update(testRegistration.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(testRegistration.getId(), updated.getId());
        assertEquals(RegistrationStatus.COMPLETED, updated.getStatus());
    }
    
    @Test
    public void testUpdateNotFound() {
        Long nonExistentId = 9999L;
        
        Registration updateData = new Registration();
        updateData.setStatus(RegistrationStatus.COMPLETED);
        updateData.setSchoolClass(schoolClass);
        
        RegistrationNotFoundException exception = assertThrows(RegistrationNotFoundException.class, () -> {
            registrationService.update(nonExistentId, updateData);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        registrationService.deleteById(testRegistration.getId());
        
        RegistrationNotFoundException exception = assertThrows(RegistrationNotFoundException.class, () -> {
            registrationService.findById(testRegistration.getId());
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 9999L;
        
        RegistrationNotFoundException exception = assertThrows(RegistrationNotFoundException.class, () -> {
            registrationService.deleteById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testChangeStatus() {
        Registration updated = registrationService.changeStatus(testRegistration.getId(), RegistrationStatus.COMPLETED);
        
        assertNotNull(updated);
        assertEquals(testRegistration.getId(), updated.getId());
        assertEquals(RegistrationStatus.COMPLETED, updated.getStatus());
    }
    
    @Test
    public void testChangeStatusToSameStatus() {
        Registration updated = registrationService.changeStatus(testRegistration.getId(), RegistrationStatus.ACTIVE);
        
        assertNotNull(updated);
        assertEquals(testRegistration.getId(), updated.getId());
        assertEquals(RegistrationStatus.ACTIVE, updated.getStatus());
    }
    
    @Test
    public void testChangeStatusNotFound() {
        Long nonExistentId = 9999L;
        
        RegistrationNotFoundException exception = assertThrows(RegistrationNotFoundException.class, () -> {
            registrationService.changeStatus(nonExistentId, RegistrationStatus.COMPLETED);
        });
        assertNotNull(exception.getMessage());
    }
}
