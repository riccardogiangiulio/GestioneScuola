package com.riccardo.giangiulio.gestionescuola.service;

import java.math.BigDecimal;
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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.RoleNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.UserNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.EmailAlreadyExistException;
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
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private SchoolClassService schoolClassService;
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private SubjectService subjectService;

    private User studentUser;
    private User teacherUser;
    private User adminUser;
    private Role studentRole;
    private Role teacherRole;
    private Role adminRole;
    private SchoolClass testClass;
    private Course testCourse;

    @BeforeEach
    public void setUp() {
        // Setup ruoli di base
        try {
            studentRole = roleService.getRoleByName(ERole.ROLE_STUDENT);
        } catch (RoleNotFoundException e) {
            Role newRole = new Role(ERole.ROLE_STUDENT);
            studentRole = roleService.saveRole(newRole);
        }
        
        try {
            teacherRole = roleService.getRoleByName(ERole.ROLE_TEACHER);
        } catch (RoleNotFoundException e) {
            Role newRole = new Role(ERole.ROLE_TEACHER);
            teacherRole = roleService.saveRole(newRole);
        }
        
        try {
            adminRole = roleService.getRoleByName(ERole.ROLE_ADMIN);
        } catch (RoleNotFoundException e) {
            Role newRole = new Role(ERole.ROLE_ADMIN);
            adminRole = roleService.saveRole(newRole);
        }

        // Setup utenti di test con email uniche usando timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        studentUser = new User();
        studentUser.setFirstName("Student");
        studentUser.setLastName("User");
        studentUser.setEmail("student_" + timestamp + "@example.com");
        studentUser.setPassword("password");
        studentUser.setBirthDate(LocalDate.of(2000, 1, 1));
        studentUser.setRole(studentRole);
        studentUser = userService.save(studentUser);
        
        teacherUser = new User();
        teacherUser.setFirstName("Teacher");
        teacherUser.setLastName("User");
        teacherUser.setEmail("teacher_" + timestamp + "@example.com");
        teacherUser.setPassword("password");
        teacherUser.setBirthDate(LocalDate.of(1980, 1, 1));
        teacherUser.setRole(teacherRole);
        teacherUser = userService.save(teacherUser);
        
        adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin_" + timestamp + "@example.com");
        adminUser.setPassword("password");
        adminUser.setBirthDate(LocalDate.of(1975, 1, 1));
        adminUser.setRole(adminRole);
        adminUser = userService.save(adminUser);
        
        // Creiamo almeno una materia per il corso
        Subject subject = new Subject();
        subject.setName("Test Subject " + timestamp);
        subject.setDescription("Test subject description");
        subject.setTeacher(teacherUser);
        subject = subjectService.save(subject);
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        
        // Setup corso di test
        testCourse = new Course();
        testCourse.setTitle("Test Course " + timestamp);
        testCourse.setDescription("A test course");
        testCourse.setDuration("12"); // mesi come String
        testCourse.setPrice(BigDecimal.valueOf(1000.0));
        testCourse.setSubjects(subjects); // Aggiungiamo almeno una materia
        testCourse = courseService.save(testCourse);
        
        // Setup classe di test con insegnanti
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        testClass = new SchoolClass();
        testClass.setName("Test Class " + timestamp);
        testClass.setMaxStudents(30);
        testClass.setCourse(testCourse);
        testClass.setTeachers(teachers);
        testClass = schoolClassService.save(testClass);
        
        // Creiamo una registrazione per lo studentUser alla testClass
        Registration registration = new Registration();
        registration.setStudent(studentUser);
        registration.setSchoolClass(testClass);
        registration.setCourse(testCourse);
        registration.setStatus(RegistrationStatus.ACTIVE);
        registration.setRegistrationDate(LocalDateTime.now());
        registrationService.save(registration);
    }

    @Test
    public void testFindAll() {
        List<User> users = userService.findAll();
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals(studentUser.getEmail())));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals(teacherUser.getEmail())));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals(adminUser.getEmail())));
    }

    @Test
    public void testFindById() {
        User foundUser = userService.findById(studentUser.getId());
        assertEquals(studentUser.getId(), foundUser.getId());
        assertEquals(studentUser.getEmail(), foundUser.getEmail());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 999999L;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testFindByEmail() {
        User foundUser = userService.findByEmail(studentUser.getEmail());
        assertEquals(studentUser.getId(), foundUser.getId());
        assertEquals(studentUser.getEmail(), foundUser.getEmail());
    }
    
    @Test
    public void testFindByEmailNotFound() {
        String nonExistentEmail = "nonexistent" + System.currentTimeMillis() + "@example.com";
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findByEmail(nonExistentEmail);
        });
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testExistsByEmail() {
        Boolean exists = userService.existsByEmail(studentUser.getEmail());
        assertTrue(exists);
        
        Boolean nonExists = userService.existsByEmail("nonexistent" + System.currentTimeMillis() + "@example.com");
        assertFalse(nonExists);
    }

    @Test
    public void testSave() {
        String uniqueEmail = "new_user_" + System.currentTimeMillis() + "@example.com";
        User newUser = new User();
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setEmail(uniqueEmail);
        newUser.setPassword("password");
        newUser.setBirthDate(LocalDate.of(1995, 5, 5));
        newUser.setRole(studentRole);
        
        User savedUser = userService.save(newUser);
        
        assertNotNull(savedUser.getId());
        assertEquals(uniqueEmail, savedUser.getEmail());
        
        // Verifica che l'utente sia stato salvato usando il service
        User retrievedUser = userService.findById(savedUser.getId());
        assertEquals(savedUser.getId(), retrievedUser.getId());
    }
    
    @Test
    public void testSaveEmailAlreadyExists() {
        User duplicateUser = new User();
        duplicateUser.setFirstName("Duplicate");
        duplicateUser.setLastName("User");
        duplicateUser.setEmail(studentUser.getEmail()); // Email già esistente
        duplicateUser.setPassword("password");
        duplicateUser.setBirthDate(LocalDate.of(1990, 10, 10));
        duplicateUser.setRole(studentRole);
            
        EmailAlreadyExistException exception = assertThrows(EmailAlreadyExistException.class, () -> {
            userService.save(duplicateUser);
        });
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testDeleteById() {
        // Crea un nuovo utente per cancellarlo, per evitare conflitti con altri test
        String uniqueEmail = "delete_" + System.currentTimeMillis() + "@example.com";
        User userToDelete = new User();
        userToDelete.setFirstName("Delete");
        userToDelete.setLastName("Me");
        userToDelete.setEmail(uniqueEmail);
        userToDelete.setPassword("password");
        userToDelete.setBirthDate(LocalDate.of(2001, 1, 1));
        userToDelete.setRole(studentRole);
        userToDelete = userService.save(userToDelete);
        
        Long idToDelete = userToDelete.getId();
        
        userService.deleteById(idToDelete);
        
        // Verifica che l'utente sia stato cancellato
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findById(idToDelete);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 999999L;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteById(nonExistentId);
        });
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testUpdate() {
        // Crea un nuovo utente da aggiornare
        String uniqueEmail = "update_" + System.currentTimeMillis() + "@example.com";
        User userToCreate = new User();
        userToCreate.setFirstName("Before");
        userToCreate.setLastName("Update");
        userToCreate.setEmail(uniqueEmail);
        userToCreate.setPassword("password");
        userToCreate.setBirthDate(LocalDate.of(1990, 5, 15));
        userToCreate.setRole(studentRole);
        User createdUser = userService.save(userToCreate);
        
        // Prepara i dati per l'aggiornamento
        String newUniqueEmail = "updated_" + System.currentTimeMillis() + "@example.com";
        User userUpdate = new User();
        userUpdate.setFirstName("After");
        userUpdate.setLastName("Update");
        userUpdate.setEmail(newUniqueEmail);
        userUpdate.setBirthDate(LocalDate.of(1990, 6, 20));
        userUpdate.setRole(teacherRole);
        
        // Aggiorna l'utente
        User updatedUser = userService.update(createdUser.getId(), userUpdate);
        
        // Verifica i cambiamenti
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals("After", updatedUser.getFirstName());
        assertEquals("Update", updatedUser.getLastName());
        assertEquals(newUniqueEmail, updatedUser.getEmail());
        assertEquals(LocalDate.of(1990, 6, 20), updatedUser.getBirthDate());
        assertEquals(teacherRole.getId(), updatedUser.getRole().getId());
        
        // Verifica che i cambiamenti siano stati salvati
        User freshUser = userService.findById(createdUser.getId());
        assertEquals("After", freshUser.getFirstName());
        assertEquals(newUniqueEmail, freshUser.getEmail());
    }
    
    @Test
    public void testUpdateNonExistingUser() {
        Long nonExistentId = 999999L;
        User nonExistingUser = new User();
        nonExistingUser.setFirstName("Non");
        nonExistingUser.setLastName("Existent");
        nonExistingUser.setEmail("non.existent" + System.currentTimeMillis() + "@example.com");
        
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.update(nonExistentId, nonExistingUser);
        });
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdateDuplicateEmail() {
        // Crea un utente da aggiornare
        String uniqueEmail1 = "update1_" + System.currentTimeMillis() + "@example.com";
        User user1 = new User();
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setEmail(uniqueEmail1);
        user1.setPassword("password");
        user1.setBirthDate(LocalDate.of(1991, 1, 1));
        user1.setRole(studentRole);
        User userSaved = userService.save(user1);
        
        // Tenta di aggiornare user1 con l'email di studentUser (che già esiste)
        User updateData = new User();
        updateData.setFirstName("Updated");
        updateData.setLastName("User");
        updateData.setEmail(studentUser.getEmail()); // Email già in uso
        
        EmailAlreadyExistException exception = assertThrows(EmailAlreadyExistException.class, () -> {
            userService.update(userSaved.getId(), updateData);
        });
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testAssignRole() {
        // Crea un nuovo utente
        String uniqueEmail = "role_" + System.currentTimeMillis() + "@example.com";
        User user = new User();
        user.setFirstName("Role");
        user.setLastName("Test");
        user.setEmail(uniqueEmail);
        user.setPassword("password");
        user.setBirthDate(LocalDate.of(1992, 2, 2));
        user.setRole(studentRole);
        User userS = userService.save(user);
        
        // Assegna un nuovo ruolo
        User updatedUser = userService.assignRole(userS.getId(), ERole.ROLE_TEACHER);
        
        // Verifica il cambio di ruolo
        assertEquals(ERole.ROLE_TEACHER, updatedUser.getRole().getName());
        
        // Verifica che il cambio sia stato salvato
        User freshUser = userService.findById(userS.getId());
        assertEquals(ERole.ROLE_TEACHER, freshUser.getRole().getName());
    }

    @Test
    public void testFindByRole() {
        List<User> studentUsers = userService.findByRole(ERole.ROLE_STUDENT);
        
        assertFalse(studentUsers.isEmpty());
        assertTrue(studentUsers.stream().anyMatch(u -> u.getId().equals(studentUser.getId())));
        
        List<User> teacherUsers = userService.findByRole(ERole.ROLE_TEACHER);
        assertFalse(teacherUsers.isEmpty());
        assertTrue(teacherUsers.stream().anyMatch(u -> u.getId().equals(teacherUser.getId())));
        
        List<User> adminUsers = userService.findByRole(ERole.ROLE_ADMIN);
        assertFalse(adminUsers.isEmpty());
        assertTrue(adminUsers.stream().anyMatch(u -> u.getId().equals(adminUser.getId())));
    }

    @Test
    public void testIsTeacherStudentAdmin() {
        // Testa i metodi di controllo ruolo
        assertTrue(userService.isStudent(studentUser));
        assertFalse(userService.isTeacher(studentUser));
        assertFalse(userService.isAdmin(studentUser));
        
        assertTrue(userService.isTeacher(teacherUser));
        assertFalse(userService.isStudent(teacherUser));
        assertFalse(userService.isAdmin(teacherUser));
        
        assertTrue(userService.isAdmin(adminUser));
        assertFalse(userService.isStudent(adminUser));
        assertFalse(userService.isTeacher(adminUser));
    }
    
    @Test
    public void testFindStudentsBySchoolClass() {
        List<User> students = userService.findStudentsBySchoolClass(testClass);
        
        assertFalse(students.isEmpty());
        assertEquals(1, students.size());
        assertEquals(studentUser.getId(), students.get(0).getId());
    }
    
    @Test
    public void testFindTeachersBySchoolClass() {
        List<User> teachers = userService.findTeachersBySchoolClass(testClass);
        
        assertFalse(teachers.isEmpty());
        assertEquals(1, teachers.size());
        assertEquals(teacherUser.getId(), teachers.get(0).getId());
    }
    
    @Test
    public void testFindStudentsWithoutActiveRegistrations() {
        // Crea un nuovo studente senza registrazione
        String uniqueEmail = "noreg_" + System.currentTimeMillis() + "@example.com";
        User studentWithoutReg = new User();
        studentWithoutReg.setFirstName("No");
        studentWithoutReg.setLastName("Registration");
        studentWithoutReg.setEmail(uniqueEmail);
        studentWithoutReg.setPassword("password");
        studentWithoutReg.setBirthDate(LocalDate.of(2002, 2, 2));
        studentWithoutReg.setRole(studentRole);
        User studentSaved = userService.save(studentWithoutReg);
        
        List<User> studentsWithoutReg = userService.findStudentsWithoutActiveRegistrations();
        
        assertNotNull(studentsWithoutReg);
        assertFalse(studentsWithoutReg.isEmpty());
        assertTrue(studentsWithoutReg.stream().anyMatch(u -> u.getId().equals(studentSaved.getId())));
        assertFalse(studentsWithoutReg.stream().anyMatch(u -> u.getId().equals(studentUser.getId())));
    }
}