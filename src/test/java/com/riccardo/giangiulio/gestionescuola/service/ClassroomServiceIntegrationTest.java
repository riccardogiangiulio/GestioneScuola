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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ClassroomNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.RoleNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomCapacityExceededException;
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
import com.riccardo.giangiulio.gestionescuola.repository.ClassroomRepository;
import com.riccardo.giangiulio.gestionescuola.repository.LessonRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClassroomServiceIntegrationTest {

    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Autowired
    private SchoolClassService schoolClassService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private RegistrationService registrationService;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    private Classroom classroom1;
    private Classroom classroom2;
    private Classroom classroom3;
    private SchoolClass testClass;
    private User teacherUser;
    private User student1;
    private User student2;
    private User student3;
    private Lesson lesson1;
    private Lesson lesson2;
    
    @BeforeEach
    public void setUp() {
        
        lessonRepository.deleteAll();
        classroomRepository.deleteAll();
        
        // Crea alcune aule di test
        classroom1 = new Classroom("Classroom 101", 30);
        classroom1 = classroomService.save(classroom1);
        
        classroom2 = new Classroom("Classroom 102", 50);
        classroom2 = classroomService.save(classroom2);
        
        classroom3 = new Classroom("Laboratory", 2); // Capacità piccola per il test
        classroom3 = classroomService.save(classroom3);
        
        Role studentRole;
        Role teacherRole;
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
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        teacherUser = new User();
        teacherUser.setFirstName("Teacher");
        teacherUser.setLastName("Test");
        teacherUser.setEmail("teacher_" + timestamp + "@example.com");
        teacherUser.setPassword("password");
        teacherUser.setBirthDate(LocalDate.of(1980, 1, 1));
        teacherUser.setRole(teacherRole);
        teacherUser = userService.save(teacherUser);
        
        student1 = new User();
        student1.setFirstName("Student1");
        student1.setLastName("Test");
        student1.setEmail("student1_" + timestamp + "@example.com");
        student1.setPassword("password");
        student1.setBirthDate(LocalDate.of(2000, 1, 1));
        student1.setRole(studentRole);
        student1 = userService.save(student1);
        
        student2 = new User();
        student2.setFirstName("Student2");
        student2.setLastName("Test");
        student2.setEmail("student2_" + timestamp + "@example.com");
        student2.setPassword("password");
        student2.setBirthDate(LocalDate.of(2001, 2, 2));
        student2.setRole(studentRole);
        student2 = userService.save(student2);
        
        student3 = new User();
        student3.setFirstName("Student3");
        student3.setLastName("Test");
        student3.setEmail("student3_" + timestamp + "@example.com");
        student3.setPassword("password");
        student3.setBirthDate(LocalDate.of(2002, 3, 3));
        student3.setRole(studentRole);
        student3 = userService.save(student3);
        
        Subject subject = new Subject();
        subject.setName("Subject Test " + timestamp);
        subject.setDescription("Description subject test");
        subject.setTeacher(teacherUser);
        subject = subjectService.save(subject);
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        
        Course course = new Course();
        course.setTitle("Course Test " + timestamp);
        course.setDescription("Description course test");
        course.setDuration("12");
        course.setPrice(BigDecimal.valueOf(1000.0));
        course.setSubjects(subjects);
        course = courseService.save(course);
        
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        testClass = new SchoolClass();
        testClass.setName("Class Test " + timestamp);
        testClass.setMaxStudents(30);
        testClass.setCourse(course);
        testClass.setTeachers(teachers);
        testClass = schoolClassService.save(testClass);
        
        Registration reg1 = new Registration();
        reg1.setStudent(student1);
        reg1.setSchoolClass(testClass);
        reg1.setCourse(course);
        reg1.setStatus(RegistrationStatus.ACTIVE);
        reg1.setRegistrationDate(LocalDateTime.now());
        registrationService.save(reg1);
        
        Registration reg2 = new Registration();
        reg2.setStudent(student2);
        reg2.setSchoolClass(testClass);
        reg2.setCourse(course);
        reg2.setStatus(RegistrationStatus.ACTIVE);
        reg2.setRegistrationDate(LocalDateTime.now());
        registrationService.save(reg2);
        
        Registration reg3 = new Registration();
        reg3.setStudent(student3);
        reg3.setSchoolClass(testClass);
        reg3.setCourse(course);
        reg3.setStatus(RegistrationStatus.ACTIVE);
        reg3.setRegistrationDate(LocalDateTime.now());
        registrationService.save(reg3);
        
        LocalDateTime now = LocalDateTime.now().plusDays(1); // Orario futuro
        LocalDateTime nowPlus2Hours = now.plusHours(2);
        
        lesson1 = new Lesson();
        lesson1.setTitle("Lesson 1");
        lesson1.setDescription("First lesson of test");
        lesson1.setSchoolClass(testClass);
        lesson1.setSubject(subject);
        lesson1.setTeacher(teacherUser);
        lesson1.setClassroom(classroom1);
        lesson1.setStartDateTime(now);
        lesson1.setEndDateTime(nowPlus2Hours);
        
        lesson1 = lessonRepository.save(lesson1);
        
        // Una lezione domani dalla stessa ora per 2 ore
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime tomorrowPlus2Hours = tomorrow.plusHours(2);
        
        lesson2 = new Lesson();
        lesson2.setTitle("Lesson 2");
        lesson2.setDescription("Second lesson of test");
        lesson2.setSchoolClass(testClass);
        lesson2.setSubject(subject);
        lesson2.setTeacher(teacherUser);
        lesson2.setClassroom(classroom2);
        lesson2.setStartDateTime(tomorrow);
        lesson2.setEndDateTime(tomorrowPlus2Hours);
        
        lesson2 = lessonRepository.save(lesson2);
    }
    
    @Test
    public void testFindAll() {
        List<Classroom> classrooms = classroomService.findAll();
        
        assertNotNull(classrooms);
        assertEquals(3, classrooms.size());
        assertTrue(classrooms.contains(classroom1));
        assertTrue(classrooms.contains(classroom2));
        assertTrue(classrooms.contains(classroom3));
    }
    
    @Test
    public void testFindById() {
        Classroom found = classroomService.findById(classroom1.getId());
        
        assertNotNull(found);
        assertEquals(classroom1.getId(), found.getId());
        assertEquals("Classroom 101", found.getName());
        assertEquals(30, found.getCapacity());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 999999L;
        
        ClassroomNotFoundException exception = assertThrows(ClassroomNotFoundException.class, () -> {
            classroomService.findById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        Classroom newClassroom = new Classroom("Classroom Magna", 100);
        Classroom saved = classroomService.save(newClassroom);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Classroom Magna", saved.getName());
        assertEquals(100, saved.getCapacity());
        
        // Verifica che l'aula sia stata salvata correttamente
        Classroom retrieved = classroomService.findById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
    }
    
    @Test
    public void testSaveInvalidCapacity() {
        Classroom invalidClassroom = new Classroom("Classroom Invalid", -10);
        
        ClassroomCapacityExceededException exception = assertThrows(ClassroomCapacityExceededException.class, () -> {
            classroomService.save(invalidClassroom);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdate() {
        Classroom updateData = new Classroom("Classroom 101 Updated", 40);
        
        Classroom updated = classroomService.update(classroom1.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(classroom1.getId(), updated.getId());
        assertEquals("Classroom 101 Updated", updated.getName());
        assertEquals(40, updated.getCapacity());
        
        Classroom retrieved = classroomService.findById(classroom1.getId());
        assertEquals("Classroom 101 Updated", retrieved.getName());
        assertEquals(40, retrieved.getCapacity());
    }
    
    @Test
    public void testUpdateNotFound() {
        Long nonExistentId = 999999L;
        Classroom updateData = new Classroom("Update Non Existent", 25);
        
        ClassroomNotFoundException exception = assertThrows(ClassroomNotFoundException.class, () -> {
            classroomService.update(nonExistentId, updateData);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdateInvalidCapacity() {
        Classroom updateData = new Classroom("Classroom 101", -5);
        
        ClassroomCapacityExceededException exception = assertThrows(ClassroomCapacityExceededException.class, () -> {
            classroomService.update(classroom1.getId(), updateData);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        // Crea una nuova aula per cancellarla, evitando conflitti con altri test
        Classroom classroomToDelete = new Classroom("Classroom to Delete", 15);
        classroomToDelete = classroomService.save(classroomToDelete);
        Long idToDelete = classroomToDelete.getId();
        
        classroomService.deleteById(idToDelete);
        
        ClassroomNotFoundException exception = assertThrows(ClassroomNotFoundException.class, () -> {
            classroomService.findById(idToDelete);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 999999L;
        
        ClassroomNotFoundException exception = assertThrows(ClassroomNotFoundException.class, () -> {
            classroomService.deleteById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByName() {
        List<Classroom> classrooms = classroomService.findByName("Classroom 101");
        
        assertNotNull(classrooms);
        assertEquals(1, classrooms.size());
        assertEquals(classroom1.getId(), classrooms.get(0).getId());
        
        List<Classroom> nonExistentClassrooms = classroomService.findByName("Non Existent");
        assertTrue(nonExistentClassrooms.isEmpty());
    }
    
    @Test
    public void testFindByMinCapacity() {
        List<Classroom> largeClassrooms = classroomService.findByMinCapacity(40);
        
        assertNotNull(largeClassrooms);
        assertEquals(1, largeClassrooms.size());
        assertEquals(classroom2.getId(), largeClassrooms.get(0).getId());
        
        // Test con una capacità più piccola che dovrebbe restituire tutte le aule
        List<Classroom> allClassrooms = classroomService.findByMinCapacity(2);
        assertEquals(3, allClassrooms.size());
    }
    
    @Test
    public void testFindByMinCapacityInvalid() {
        ClassroomCapacityExceededException exception = assertThrows(ClassroomCapacityExceededException.class, () -> {
            classroomService.findByMinCapacity(-10);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testHasSufficientCapacity() {
        boolean hasCapacity = classroomService.hasSufficientCapacity(classroom2.getId(), 40);
        assertTrue(hasCapacity);
        
        boolean insufficientCapacity = classroomService.hasSufficientCapacity(classroom3.getId(), 30);
        assertFalse(insufficientCapacity);
    }
    
    @Test
    public void testHasSufficientCapacityInvalidRequirement() {
        ClassroomCapacityExceededException exception = assertThrows(ClassroomCapacityExceededException.class, () -> {
            classroomService.hasSufficientCapacity(classroom1.getId(), -5);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindAvailableInTimeRange() {
        // Cerca aule disponibili in un orario in cui l'aula 1 è occupata
        LocalDateTime startTime = lesson1.getStartDateTime();
        LocalDateTime endTime = lesson1.getEndDateTime();
        
        List<Classroom> availableClassrooms = classroomService.findAvailableInTimeRange(startTime, endTime);
        
        assertNotNull(availableClassrooms);
        assertFalse(availableClassrooms.isEmpty());
        
        boolean containsClassroom2 = availableClassrooms.stream()
                .anyMatch(c -> c.getId().equals(classroom2.getId()));
        boolean containsClassroom3 = availableClassrooms.stream()
                .anyMatch(c -> c.getId().equals(classroom3.getId()));
        boolean containsClassroom1 = availableClassrooms.stream()
                .anyMatch(c -> c.getId().equals(classroom1.getId()));
        
        assertTrue(containsClassroom2);
        assertTrue(containsClassroom3);
        assertFalse(containsClassroom1); // Non dovrebbe essere disponibile
        
        // Cerca aule disponibili in un orario libero (dopo le lezioni programmate)
        LocalDateTime laterStartTime = lesson2.getEndDateTime().plusHours(1);
        LocalDateTime laterEndTime = laterStartTime.plusHours(2);
        
        List<Classroom> allAvailableClassrooms = classroomService.findAvailableInTimeRange(laterStartTime, laterEndTime);
        
        assertNotNull(allAvailableClassrooms);
        
        assertEquals(3, allAvailableClassrooms.size());
        
        boolean laterContainsClassroom1 = allAvailableClassrooms.stream()
                .anyMatch(c -> c.getId().equals(classroom1.getId()));
        boolean laterContainsClassroom2 = allAvailableClassrooms.stream()
                .anyMatch(c -> c.getId().equals(classroom2.getId()));
        boolean laterContainsClassroom3 = allAvailableClassrooms.stream()
                .anyMatch(c -> c.getId().equals(classroom3.getId()));
        
        assertTrue(laterContainsClassroom1);
        assertTrue(laterContainsClassroom2);
        assertTrue(laterContainsClassroom3);
    }
    
    @Test
    public void testFindAvailableInTimeRangeInvalidRange() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earlier = now.minusHours(2);
        
        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> {
            classroomService.findAvailableInTimeRange(now, earlier);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testIsAvailableForTimeSlot() {
        // Verifica se l'aula 1 è disponibile nel suo orario di lezione (dovrebbe essere falso)
        LocalDateTime startTime = lesson1.getStartDateTime();
        LocalDateTime endTime = lesson1.getEndDateTime();
        
        boolean isAvailable = classroomService.isAvailableForTimeSlot(classroom1.getId(), startTime, endTime);
        assertFalse(isAvailable);
        
        // Verifica se l'aula 2 è disponibile nello stesso orario (dovrebbe essere vero)
        boolean isAvailable2 = classroomService.isAvailableForTimeSlot(classroom2.getId(), startTime, endTime);
        assertTrue(isAvailable2);
        
        // Verifica se l'aula 1 è disponibile in un orario libero (dovrebbe essere vero)
        LocalDateTime laterStartTime = lesson2.getEndDateTime().plusHours(1);
        LocalDateTime laterEndTime = laterStartTime.plusHours(2);
        
        boolean isAvailableLater = classroomService.isAvailableForTimeSlot(classroom1.getId(), laterStartTime, laterEndTime);
        assertTrue(isAvailableLater);
    }
    
    @Test
    public void testIsAvailableForTimeSlotInvalidRange() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earlier = now.minusHours(2);
        
        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> {
            classroomService.isAvailableForTimeSlot(classroom1.getId(), now, earlier);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindWithSufficientCapacityForSchoolClass() {
        // La classe di test ha 3 studenti registrati
        List<Classroom> classroomsWithCapacity = classroomService.findWithSufficientCapacityForSchoolClass(testClass.getId());
        
        assertNotNull(classroomsWithCapacity);
        assertFalse(classroomsWithCapacity.isEmpty());
        
        // L'aula 3 ha capacità 2, che è insufficiente per 3 studenti
        boolean containsClassroom1 = classroomsWithCapacity.stream()
                .anyMatch(c -> c.getId().equals(classroom1.getId()));
        boolean containsClassroom2 = classroomsWithCapacity.stream()
                .anyMatch(c -> c.getId().equals(classroom2.getId()));
        boolean containsClassroom3 = classroomsWithCapacity.stream()
                .anyMatch(c -> c.getId().equals(classroom3.getId()));
        
        assertTrue(containsClassroom1);  // Capacità 30
        assertTrue(containsClassroom2);  // Capacità 50
        assertFalse(containsClassroom3); // Capacità 2 (insufficiente)
    }
    
    @Test
    public void testFindWithSufficientCapacityForSchoolClassExceptionWhenNoClassroomsAvailable() {
        // Prima modifichiamo tutte le aule per avere capacità insufficiente
        classroom1.setCapacity(1);
        classroomRepository.save(classroom1);
        
        classroom2.setCapacity(1);
        classroomRepository.save(classroom2);
        
        classroom3.setCapacity(1);
        classroomRepository.save(classroom3);
        
        ClassroomCapacityExceededException exception = assertThrows(ClassroomCapacityExceededException.class, () -> {
            classroomService.findWithSufficientCapacityForSchoolClass(testClass.getId());
        });
        
        assertNotNull(exception.getMessage());
    }
}