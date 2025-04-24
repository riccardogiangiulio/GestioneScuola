package com.riccardo.giangiulio.gestionescuola.service;

import java.time.LocalDate;
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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.SubjectNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTeacherException;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SubjectServiceIntegrationTest {

    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private CourseService courseService;
    
    private User teacherUser;
    private User nonTeacherUser;
    private Subject testSubject;
    private Course testCourse;
    
    @BeforeEach
    public void setUp() {
        // Genera timestamp unico
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // Ottieni o crea ruoli
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
        
        // Crea utenti
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
        
        // Crea materia
        testSubject = new Subject();
        testSubject.setName("Subject Test " + timestamp);
        testSubject.setDescription("Description subject test");
        testSubject.setTeacher(teacherUser);
        testSubject = subjectService.save(testSubject);
        
        // Crea corso
        Set<Subject> subjects = new HashSet<>();
        subjects.add(testSubject);
        testCourse = new Course();
        testCourse.setTitle("Course Test " + timestamp);
        testCourse.setDescription("Description course test");
        testCourse.setDuration("12 months");
        testCourse.setPrice(java.math.BigDecimal.valueOf(1000.0));
        testCourse.setSubjects(subjects);
        courseService.save(testCourse);
        
        // Aggiorna la materia per impostare il corso
        Set<Course> courses = new HashSet<>();
        courses.add(testCourse);
        testSubject.setCourses(courses);
        testSubject = subjectService.update(testSubject.getId(), testSubject);
    }
    
    @Test
    public void testFindAll() {
        List<Subject> subjects = subjectService.findAll();
        
        assertNotNull(subjects);
        assertFalse(subjects.isEmpty());
        assertTrue(subjects.stream().anyMatch(s -> s.getId().equals(testSubject.getId())));
    }
    
    @Test
    public void testFindById() {
        Subject found = subjectService.findById(testSubject.getId());
        
        assertNotNull(found);
        assertEquals(testSubject.getId(), found.getId());
        assertEquals(testSubject.getName(), found.getName());
        assertEquals(testSubject.getDescription(), found.getDescription());
        assertEquals(teacherUser.getId(), found.getTeacher().getId());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 9999L;
        
        SubjectNotFoundException exception = assertThrows(SubjectNotFoundException.class, () -> {
            subjectService.findById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        Subject newSubject = new Subject();
        newSubject.setName("New Subject " + timestamp);
        newSubject.setDescription("Description new subject test");
        newSubject.setTeacher(teacherUser);
        
        Subject saved = subjectService.save(newSubject);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("New Subject " + timestamp, saved.getName());
        assertEquals("Description new subject test", saved.getDescription());
        assertEquals(teacherUser.getId(), saved.getTeacher().getId());
    }
    
    @Test
    public void testSaveWithInvalidTeacher() {
        Subject invalidSubject = new Subject();
        invalidSubject.setName("Invalid Subject ");
        invalidSubject.setDescription("Description invalid subject test");
        invalidSubject.setTeacher(nonTeacherUser); // utente non insegnante
        
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            subjectService.save(invalidSubject);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdate() {
        Subject updateData = new Subject();
        updateData.setName("Updated Subject");
        updateData.setDescription("Description updated subject test");
        updateData.setTeacher(teacherUser);
        
        Subject updated = subjectService.update(testSubject.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(testSubject.getId(), updated.getId());
        assertEquals("Updated Subject", updated.getName());
        assertEquals("Description updated subject test", updated.getDescription());
    }
    
    @Test
    public void testUpdateWithInvalidTeacher() {
        Subject updateData = new Subject();
        updateData.setName("Updated Subject");
        updateData.setDescription("Description updated subject test");
        updateData.setTeacher(nonTeacherUser); // utente non insegnante
        
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            subjectService.update(testSubject.getId(), updateData);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        // Prima creo una nuova materia da eliminare
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        Subject subjectToDelete = new Subject();
        subjectToDelete.setName("Subject to Delete " + timestamp);
        subjectToDelete.setDescription("Description subject to delete");
        subjectToDelete.setTeacher(teacherUser);
        subjectToDelete = subjectService.save(subjectToDelete);
        
        Long idToDelete = subjectToDelete.getId();
        
        // Poi la elimino
        subjectService.deleteById(idToDelete);
        
        // Verifico che sia stata eliminata
        SubjectNotFoundException exception = assertThrows(SubjectNotFoundException.class, () -> {
            subjectService.findById(idToDelete);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 9999L;
        
        SubjectNotFoundException exception = assertThrows(SubjectNotFoundException.class, () -> {
            subjectService.deleteById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByName() {
        Subject found = subjectService.findByName(testSubject.getName());
        
        assertNotNull(found);
        assertEquals(testSubject.getId(), found.getId());
        assertEquals(testSubject.getName(), found.getName());
    }
    
    @Test
    public void testFindByNameNotFound() {
        String nonExistentName = "Subject Not Existent " + System.currentTimeMillis();
        
        SubjectNotFoundException exception = assertThrows(SubjectNotFoundException.class, () -> {
            subjectService.findByName(nonExistentName);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByCourse() {
        List<Subject> subjects = subjectService.findByCourse(testCourse);
        
        assertNotNull(subjects);
        assertFalse(subjects.isEmpty());
        assertTrue(subjects.stream().anyMatch(s -> s.getId().equals(testSubject.getId())));
    }
    
    @Test
    public void testAssignTeacher() {
        // Prima creo un nuovo insegnante
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        User newTeacher = new User();
        newTeacher.setFirstName("New");
        newTeacher.setLastName("Teacher");
        newTeacher.setEmail("new_teacher_" + timestamp + "@example.com");
        newTeacher.setPassword("password");
        newTeacher.setBirthDate(LocalDate.of(1985, 1, 1));
        newTeacher.setRole(roleService.getRoleByName(ERole.ROLE_TEACHER));
        newTeacher = userService.save(newTeacher);
        
        // Poi lo assegno alla materia
        Subject updated = subjectService.assignTeacher(testSubject.getId(), newTeacher.getId());
        
        assertNotNull(updated);
        assertEquals(testSubject.getId(), updated.getId());
        assertEquals(newTeacher.getId(), updated.getTeacher().getId());
    }
    
    @Test
    public void testAssignInvalidTeacher() {
        InvalidTeacherException exception = assertThrows(InvalidTeacherException.class, () -> {
            subjectService.assignTeacher(testSubject.getId(), nonTeacherUser.getId());
        });
        assertNotNull(exception.getMessage());
    }
}
