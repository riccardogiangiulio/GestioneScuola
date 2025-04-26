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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.SchoolClassNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ActiveRegistrationsException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.MinimumTeachersException;
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
public class SchoolClassServiceIntegrationTest {

    @Autowired
    private SchoolClassService schoolClassService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private SubjectService subjectService;
    
    private User teacherUser;
    private User secondTeacherUser;
    private User studentUser;
    private Role teacherRole;
    private Role studentRole;
    private SchoolClass testClass;
    private Course testCourse;
    
    @BeforeEach
    public void setUp() {
        // Genera timestamp unico
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // Ottieni o crea ruoli
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
        
        // Crea utenti
        teacherUser = new User();
        teacherUser.setFirstName("Teacher");
        teacherUser.setLastName("Test");
        teacherUser.setEmail("teacher_" + timestamp + "@example.com");
        teacherUser.setPassword("password");
        teacherUser.setBirthDate(LocalDate.of(1980, 1, 1));
        teacherUser.setRole(teacherRole);
        teacherUser = userService.save(teacherUser);
        
        secondTeacherUser = new User();
        secondTeacherUser.setFirstName("Second");
        secondTeacherUser.setLastName("Teacher");
        secondTeacherUser.setEmail("teacher2_" + timestamp + "@example.com");
        secondTeacherUser.setPassword("password");
        secondTeacherUser.setBirthDate(LocalDate.of(1975, 1, 1));
        secondTeacherUser.setRole(teacherRole);
        secondTeacherUser = userService.save(secondTeacherUser);
        
        studentUser = new User();
        studentUser.setFirstName("Student");
        studentUser.setLastName("Test");
        studentUser.setEmail("student_" + timestamp + "@example.com");
        studentUser.setPassword("password");
        studentUser.setBirthDate(LocalDate.of(2000, 1, 1));
        studentUser.setRole(studentRole);
        studentUser = userService.save(studentUser);
        
        // Crea materia
        Subject subject = new Subject();
        subject.setName("Subject Test " + timestamp);
        subject.setDescription("Description subject test");
        subject.setTeacher(teacherUser);
        subject = subjectService.save(subject);
        
        // Crea corso
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        testCourse = new Course();
        testCourse.setTitle("Course Test " + timestamp);
        testCourse.setDescription("Description course test");
        testCourse.setDuration("12 months");
        testCourse.setPrice(java.math.BigDecimal.valueOf(1000.0));
        testCourse.setSubjects(subjects);
        testCourse = courseService.save(testCourse);
        
        // Crea classe
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        testClass = new SchoolClass();
        testClass.setName("Class Test " + timestamp);
        testClass.setMaxStudents(30);
        testClass.setCourse(testCourse);
        testClass.setTeachers(teachers);
        testClass = schoolClassService.save(testClass);
        
        // Crea una registrazione
        Registration registration = new Registration();
        registration.setStudent(studentUser);
        registration.setSchoolClass(testClass);
        registration.setStatus(RegistrationStatus.ACTIVE);
        registration.setCourse(testCourse);
        registration.setRegistrationDate(LocalDateTime.now());
        registration = registrationService.save(registration);
        
        // Aggiorna le registrazioni nella classe
        Set<Registration> registrations = new HashSet<>();
        registrations.add(registration);
        testClass.setRegistrations(registrations);
        testClass = schoolClassService.save(testClass);
    }
    
    @Test
    public void testFindAll() {
        List<SchoolClass> classes = schoolClassService.findAll();
        
        assertNotNull(classes);
        assertFalse(classes.isEmpty());
        assertTrue(classes.stream().anyMatch(c -> c.getId().equals(testClass.getId())));
    }
    
    @Test
    public void testFindById() {
        SchoolClass found = schoolClassService.findById(testClass.getId());
        
        assertNotNull(found);
        assertEquals(testClass.getId(), found.getId());
        assertEquals(testClass.getName(), found.getName());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 9999L;
        
        SchoolClassNotFoundException exception = assertThrows(SchoolClassNotFoundException.class, () -> {
            schoolClassService.findById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        SchoolClass newClass = new SchoolClass();
        newClass.setName("New Class " + timestamp);
        newClass.setMaxStudents(25);
        newClass.setCourse(testCourse);
        newClass.setTeachers(teachers);
        
        SchoolClass saved = schoolClassService.save(newClass);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("New Class " + timestamp, saved.getName());
        assertEquals(25, saved.getMaxStudents());
        assertEquals(testCourse.getId(), saved.getCourse().getId());
        assertEquals(1, saved.getTeachers().size());
    }
    
    @Test
    public void testSaveWithInvalidMaxStudents() {
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        SchoolClass invalidClass = new SchoolClass();
        invalidClass.setName("Invalid Class");
        invalidClass.setMaxStudents(0); // Invalido
        invalidClass.setCourse(testCourse);
        invalidClass.setTeachers(teachers);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            schoolClassService.save(invalidClass);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithoutTeachers() {
        SchoolClass invalidClass = new SchoolClass();
        invalidClass.setName("Class without teachers");
        invalidClass.setMaxStudents(20);
        invalidClass.setCourse(testCourse);
        invalidClass.setTeachers(new HashSet<>()); // Nessun docente
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            schoolClassService.save(invalidClass);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidTeacher() {
        Set<User> invalidTeachers = new HashSet<>();
        invalidTeachers.add(studentUser); // Non è un docente
        
        SchoolClass invalidClass = new SchoolClass();
        invalidClass.setName("Class with invalid teacher");
        invalidClass.setMaxStudents(20);
        invalidClass.setCourse(testCourse);
        invalidClass.setTeachers(invalidTeachers);
        
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            schoolClassService.save(invalidClass);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdate() {
        SchoolClass updateData = new SchoolClass();
        updateData.setName("Updated Class");
        updateData.setMaxStudents(40);
        updateData.setCourse(testCourse);
        
        Set<User> updatedTeachers = new HashSet<>();
        updatedTeachers.add(teacherUser);
        updatedTeachers.add(secondTeacherUser);
        updateData.setTeachers(updatedTeachers);
        
        SchoolClass updated = schoolClassService.update(testClass.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(testClass.getId(), updated.getId());
        assertEquals("Updated Class", updated.getName());
        assertEquals(40, updated.getMaxStudents());
        assertEquals(2, updated.getTeachers().size());
    }
    
    @Test
    public void testUpdateReducingMaxStudentsBelowCurrentRegistrations() {
        SchoolClass updateData = new SchoolClass();
        updateData.setName(testClass.getName());
        updateData.setMaxStudents(0); // Meno delle registrazioni attuali
        updateData.setCourse(testCourse);
        updateData.setTeachers(testClass.getTeachers());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            schoolClassService.update(testClass.getId(), updateData);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdateWithoutTeachers() {
        SchoolClass updateData = new SchoolClass();
        updateData.setName(testClass.getName());
        updateData.setMaxStudents(testClass.getMaxStudents());
        updateData.setCourse(testCourse);
        updateData.setTeachers(new HashSet<>()); // Nessun docente
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            schoolClassService.update(testClass.getId(), updateData);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdateWithInvalidTeacher() {
        Set<User> invalidTeachers = new HashSet<>();
        invalidTeachers.add(studentUser); // Non è un docente
        
        SchoolClass updateData = new SchoolClass();
        updateData.setName(testClass.getName());
        updateData.setMaxStudents(testClass.getMaxStudents());
        updateData.setCourse(testCourse);
        updateData.setTeachers(invalidTeachers);
        
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            schoolClassService.update(testClass.getId(), updateData);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        // Prima creo una classe senza registrazioni
        String timestamp = String.valueOf(System.currentTimeMillis());
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        SchoolClass classToDelete = new SchoolClass();
        classToDelete.setName("Class to delete " + timestamp);
        classToDelete.setMaxStudents(20);
        classToDelete.setCourse(testCourse);
        classToDelete.setTeachers(teachers);
        classToDelete = schoolClassService.save(classToDelete);
        
        // Poi la elimino
        Long idToDelete = classToDelete.getId();
        schoolClassService.deleteById(idToDelete);
        
        // Verifico che sia stata eliminata
        SchoolClassNotFoundException exception = assertThrows(SchoolClassNotFoundException.class, () -> {
            schoolClassService.findById(idToDelete);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdWithActiveRegistrations() {
        // La classe di test ha registrazioni attive
        ActiveRegistrationsException exception = assertThrows(ActiveRegistrationsException.class, () -> {
            schoolClassService.deleteById(testClass.getId());
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByCourse() {
        List<SchoolClass> classes = schoolClassService.findByCourse(testCourse);
        
        assertNotNull(classes);
        assertFalse(classes.isEmpty());
        assertTrue(classes.stream().anyMatch(c -> c.getId().equals(testClass.getId())));
    }
    
    @Test
    public void testFindByTeacher() {
        List<SchoolClass> classes = schoolClassService.findByTeacher(teacherUser);
        
        assertNotNull(classes);
        assertFalse(classes.isEmpty());
        assertTrue(classes.stream().anyMatch(c -> c.getId().equals(testClass.getId())));
    }
    
    @Test
    public void testFindByName() {
        SchoolClass classes = schoolClassService.findByName(testClass.getName());
        
        assertNotNull(classes);
        assertTrue(classes.getId().equals(testClass.getId()));
    }
    
    @Test
    public void testFindAvailable() {
        List<SchoolClass> classes = schoolClassService.findAvailable();
        
        assertNotNull(classes);
        // La classe di test dovrebbe essere disponibile (max 30, solo 1 registrazione)
        assertTrue(classes.stream().anyMatch(c -> c.getId().equals(testClass.getId())));
    }
    
    @Test
    public void testFindFull() {
        // Prima creo una classe piena
        String timestamp = String.valueOf(System.currentTimeMillis());
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        SchoolClass fullClass = new SchoolClass();
        fullClass.setName("Full Class " + timestamp);
        fullClass.setMaxStudents(1); // Esattamente 1 posto
        fullClass.setCourse(testCourse);
        fullClass.setTeachers(teachers);
        schoolClassService.save(fullClass);
        
        // Registro uno studente
        User anotherStudent = new User();
        anotherStudent.setFirstName("Other");
        anotherStudent.setLastName("Student");
        anotherStudent.setEmail("other_" + timestamp + "@example.com");
        anotherStudent.setPassword("password");
        anotherStudent.setBirthDate(LocalDate.of(2001, 1, 1));
        anotherStudent.setRole(studentRole);
        userService.save(anotherStudent);
        
        Registration reg = new Registration();
        reg.setStudent(anotherStudent);
        reg.setSchoolClass(fullClass);
        reg.setStatus(RegistrationStatus.ACTIVE);
        reg.setCourse(testCourse);
        reg = registrationService.save(reg);
        
        // Aggiorno le registrazioni
        Set<Registration> registrations = new HashSet<>();
        registrations.add(reg);
        fullClass.setRegistrations(registrations);
        schoolClassService.save(fullClass);
        
        // Ora cerco le classi piene
        List<SchoolClass> classes = schoolClassService.findFull();
        
        assertNotNull(classes);
        // La classe appena creata dovrebbe essere piena
        assertTrue(classes.stream().anyMatch(c -> c.getId().equals(fullClass.getId())));
    }
    
    @Test
    public void testAddTeacher() {
        SchoolClass updated = schoolClassService.addTeacher(testClass.getId(), secondTeacherUser.getId());
        
        assertNotNull(updated);
        assertEquals(2, updated.getTeachers().size());
        assertTrue(updated.getTeachers().stream().anyMatch(t -> t.getId().equals(secondTeacherUser.getId())));
    }
    
    @Test
    public void testAddInvalidTeacher() {
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            schoolClassService.addTeacher(testClass.getId(), studentUser.getId());
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testAddTeacherAlreadyAssigned() {
        // Il docente è già assegnato
        SchoolClass updated = schoolClassService.addTeacher(testClass.getId(), teacherUser.getId());
        
        assertNotNull(updated);
        // Non dovrebbe cambiare il numero di docenti
        assertEquals(1, updated.getTeachers().size());
    }
    
    @Test
    public void testRemoveTeacher() {
        // Prima aggiungo un secondo docente
        schoolClassService.addTeacher(testClass.getId(), secondTeacherUser.getId());
        
        // Poi rimuovo il primo docente
        SchoolClass updated = schoolClassService.removeTeacher(testClass.getId(), teacherUser.getId());
        
        assertNotNull(updated);
        assertEquals(1, updated.getTeachers().size());
        assertTrue(updated.getTeachers().stream().anyMatch(t -> t.getId().equals(secondTeacherUser.getId())));
    }
    
    @Test
    public void testRemoveTeacherMinimumException() {
        // La classe ha solo un docente, non si può rimuovere
        MinimumTeachersException exception = assertThrows(MinimumTeachersException.class, () -> {
            schoolClassService.removeTeacher(testClass.getId(), teacherUser.getId());
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testGetAvailableSeats() {
        int availableSeats = schoolClassService.getAvailableSeats(testClass.getId());
        
        // La classe ha max 30 studenti e 1 registrazione
        assertEquals(29, availableSeats);
    }
    
    @Test
    public void testIsFull() {
        // Creo una classe piena
        String timestamp = String.valueOf(System.currentTimeMillis());
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        SchoolClass fullClass = new SchoolClass();
        fullClass.setName("Full Class " + timestamp);
        fullClass.setMaxStudents(1); // Esattamente 1 posto
        fullClass.setCourse(testCourse);
        fullClass.setTeachers(teachers);
        schoolClassService.save(fullClass);
        
        // Registro uno studente
        User anotherStudent = new User();
        anotherStudent.setFirstName("Other");
        anotherStudent.setLastName("Student");
        anotherStudent.setEmail("other_" + timestamp + "@example.com");
        anotherStudent.setPassword("password");
        anotherStudent.setBirthDate(LocalDate.of(2001, 1, 1));
        anotherStudent.setRole(studentRole);
        anotherStudent = userService.save(anotherStudent);
        
        Registration reg = new Registration();
        reg.setStudent(anotherStudent);
        reg.setSchoolClass(fullClass);
        reg.setStatus(RegistrationStatus.ACTIVE);
        reg.setCourse(testCourse);
        reg = registrationService.save(reg);
        
        // Aggiorno le registrazioni
        Set<Registration> registrations = new HashSet<>();
        registrations.add(reg);
        fullClass.setRegistrations(registrations);
        schoolClassService.save(fullClass);
        
        // Verifico che la classe sia piena
        SchoolClassFullException exception = assertThrows(SchoolClassFullException.class, () -> {
            schoolClassService.isFull(fullClass.getId());
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testGetActiveRegistrations() {
        List<Registration> activeRegistrations = schoolClassService.getActiveRegistrations(testClass.getId());
        
        assertNotNull(activeRegistrations);
        assertEquals(1, activeRegistrations.size());
        assertEquals(RegistrationStatus.ACTIVE, activeRegistrations.get(0).getStatus());
    }
}
