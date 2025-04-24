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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.AttendanceNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.RoleNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidStudentException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.InvalidTimeRangeException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.TimeOutOfBoundsExceptions;
import com.riccardo.giangiulio.gestionescuola.model.Attendance;
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
import com.riccardo.giangiulio.gestionescuola.repository.AttendanceRepository;
import com.riccardo.giangiulio.gestionescuola.repository.LessonRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AttendanceServiceIntegrationTest {
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private ClassroomService classroomService;
    
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
    
    private User teacherUser;
    private User studentUser;
    private User nonStudentUser;
    private SchoolClass testClass;
    private Lesson testLesson;
    private Attendance testAttendance;
    
    @BeforeEach
    public void setUp() {
        attendanceRepository.deleteAll();
        lessonRepository.deleteAll();
        
        Role studentRole;
        Role teacherRole;
        Role adminRole;
        
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
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        teacherUser = new User();
        teacherUser.setFirstName("Teacher");
        teacherUser.setLastName("Test");
        teacherUser.setEmail("teacher_" + timestamp + "@example.com");
        teacherUser.setPassword("password");
        teacherUser.setBirthDate(LocalDate.of(1980, 1, 1));
        teacherUser.setRole(teacherRole);
        teacherUser = userService.save(teacherUser);
        
        studentUser = new User();
        studentUser.setFirstName("Student");
        studentUser.setLastName("Test");
        studentUser.setEmail("student_" + timestamp + "@example.com");
        studentUser.setPassword("password");
        studentUser.setBirthDate(LocalDate.of(2000, 1, 1));
        studentUser.setRole(studentRole);
        studentUser = userService.save(studentUser);
        
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
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        
        Course course = new Course();
        course.setTitle("Course Test " + timestamp);
        course.setDescription("Description course test");
        course.setDuration("12");
        course.setPrice(BigDecimal.valueOf(1000.0));
        course.setSubjects(subjects);
        course = courseService.save(course);
        
        Classroom classroom = new Classroom("Classroom Test", 30);
        classroom = classroomService.save(classroom);
        
        Set<User> teachers = new HashSet<>();
        teachers.add(teacherUser);
        
        testClass = new SchoolClass();
        testClass.setName("Class Test " + timestamp);
        testClass.setMaxStudents(30);
        testClass.setCourse(course);
        testClass.setTeachers(teachers);
        testClass = schoolClassService.save(testClass);
        
        Registration reg = new Registration();
        reg.setStudent(studentUser);
        reg.setCourse(course);
        reg.setSchoolClass(testClass);
        reg.setStatus(RegistrationStatus.ACTIVE);
        reg.setRegistrationDate(LocalDateTime.now());
        registrationService.save(reg);
        
        // Crea lezione (con data futura)
        LocalDateTime lessonStart = LocalDateTime.now().plusDays(1);
        LocalDateTime lessonEnd = lessonStart.plusHours(2);
        
        testLesson = new Lesson();
        testLesson.setTitle("Lesson Test");
        testLesson.setDescription("Description lesson test");
        testLesson.setStartDateTime(lessonStart);
        testLesson.setEndDateTime(lessonEnd);
        testLesson.setSchoolClass(testClass);
        testLesson.setSubject(subject);
        testLesson.setTeacher(teacherUser);
        testLesson.setClassroom(classroom);
        testLesson = lessonRepository.save(testLesson);
        
        // Crea presenza
        testAttendance = new Attendance();
        testAttendance.setPresent(true);
        testAttendance.setEntryTime(lessonStart.plusMinutes(5)); // 5 minuti dopo l'inizio
        testAttendance.setExitTime(lessonEnd.minusMinutes(5));   // 5 minuti prima della fine
        testAttendance.setStudent(studentUser);
        testAttendance.setLesson(testLesson);
        testAttendance = attendanceService.save(testAttendance);
    }
    
    @Test
    public void testFindAll() {
        List<Attendance> attendances = attendanceService.findAll();
        
        assertNotNull(attendances);
        assertFalse(attendances.isEmpty());
        assertTrue(attendances.stream().anyMatch(a -> a.getId().equals(testAttendance.getId())));
    }
    
    @Test
    public void testFindById() {
        Attendance found = attendanceService.findById(testAttendance.getId());
        
        assertNotNull(found);
        assertEquals(testAttendance.getId(), found.getId());
        assertEquals(studentUser.getId(), found.getStudent().getId());
        assertEquals(testLesson.getId(), found.getLesson().getId());
        assertTrue(found.getPresent());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 999999L;
        
        AttendanceNotFoundException exception = assertThrows(AttendanceNotFoundException.class, () -> {
            attendanceService.findById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        // Crea una nuova presenza
        Attendance newAttendance = new Attendance();
        newAttendance.setPresent(false); // Assente
        newAttendance.setEntryTime(testLesson.getStartDateTime());
        newAttendance.setExitTime(testLesson.getEndDateTime());
        newAttendance.setStudent(studentUser);
        newAttendance.setLesson(testLesson);
        
        Attendance saved = attendanceService.save(newAttendance);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertFalse(saved.getPresent());
        
        Attendance retrieved = attendanceService.findById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
    }
    
    @Test
    public void testSaveWithInvalidStudent() {
        Attendance invalidAttendance = new Attendance();
        invalidAttendance.setPresent(true);
        invalidAttendance.setEntryTime(testLesson.getStartDateTime());
        invalidAttendance.setExitTime(testLesson.getEndDateTime());
        invalidAttendance.setStudent(nonStudentUser); // Non è uno studente
        invalidAttendance.setLesson(testLesson);
        
        InvalidStudentException exception = assertThrows(InvalidStudentException.class, () -> {
            attendanceService.save(invalidAttendance);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithInvalidTimeRange() {
        Attendance invalidAttendance = new Attendance();
        invalidAttendance.setPresent(true);
        invalidAttendance.setEntryTime(testLesson.getStartDateTime().plusHours(1));
        invalidAttendance.setExitTime(testLesson.getStartDateTime()); // La fine è prima dell'inizio
        invalidAttendance.setStudent(studentUser);
        invalidAttendance.setLesson(testLesson);
        
        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> {
            attendanceService.save(invalidAttendance);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSaveWithTimeOutOfBounds() {
        Attendance invalidAttendance = new Attendance();
        invalidAttendance.setPresent(true);
        invalidAttendance.setEntryTime(testLesson.getStartDateTime().minusHours(1)); // Prima dell'inizio della lezione
        invalidAttendance.setExitTime(testLesson.getEndDateTime());
        invalidAttendance.setStudent(studentUser);
        invalidAttendance.setLesson(testLesson);
        
        TimeOutOfBoundsExceptions exception = assertThrows(TimeOutOfBoundsExceptions.class, () -> {
            attendanceService.save(invalidAttendance);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testUpdate() {
        Attendance updateData = new Attendance();
        updateData.setPresent(false); // Cambia da presente ad assente
        updateData.setEntryTime(testAttendance.getEntryTime());
        updateData.setExitTime(testAttendance.getExitTime());
        
        Attendance updated = attendanceService.update(testAttendance.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(testAttendance.getId(), updated.getId());
        assertFalse(updated.getPresent()); // Verifica che lo stato sia cambiato
        
        Attendance retrieved = attendanceService.findById(testAttendance.getId());
        assertFalse(retrieved.getPresent());
    }
    
    @Test
    public void testUpdateNotFound() {
        Long nonExistentId = 999999L;
        Attendance updateData = new Attendance();
        updateData.setPresent(false);
        updateData.setEntryTime(testAttendance.getEntryTime());
        updateData.setExitTime(testAttendance.getExitTime());
        
        AttendanceNotFoundException exception = assertThrows(AttendanceNotFoundException.class, () -> {
            attendanceService.update(nonExistentId, updateData);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        // Crea una nuova presenza per eliminarla
        Attendance attendanceToDelete = new Attendance();
        attendanceToDelete.setPresent(true);
        attendanceToDelete.setEntryTime(testLesson.getStartDateTime().plusMinutes(10));
        attendanceToDelete.setExitTime(testLesson.getEndDateTime().minusMinutes(10));
        attendanceToDelete.setStudent(studentUser);
        attendanceToDelete.setLesson(testLesson);
        attendanceToDelete = attendanceService.save(attendanceToDelete);
        
        Long idToDelete = attendanceToDelete.getId();
        
        attendanceService.deleteById(idToDelete);
        
        AttendanceNotFoundException exception = assertThrows(AttendanceNotFoundException.class, () -> {
            attendanceService.findById(idToDelete);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 999999L;
        
        AttendanceNotFoundException exception = assertThrows(AttendanceNotFoundException.class, () -> {
            attendanceService.deleteById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByLesson() {
        List<Attendance> attendances = attendanceService.findByLesson(testLesson);
        
        assertNotNull(attendances);
        assertFalse(attendances.isEmpty());
        assertTrue(attendances.stream().anyMatch(a -> a.getId().equals(testAttendance.getId())));
    }
    
    @Test
    public void testFindByStudent() {
        List<Attendance> attendances = attendanceService.findByStudent(studentUser);
        
        assertNotNull(attendances);
        assertFalse(attendances.isEmpty());
        assertTrue(attendances.stream().anyMatch(a -> a.getId().equals(testAttendance.getId())));
    }
    
    @Test
    public void testFindByLessonAndStudent() {
        Attendance found = attendanceService.findByLessonAndStudent(testLesson, studentUser);
        
        assertNotNull(found);
        assertEquals(testAttendance.getId(), found.getId());
    }
    
    @Test
    public void testFindByPresent() {
        List<Attendance> presentAttendances = attendanceService.findByPresent(true);
        
        assertNotNull(presentAttendances);
        assertFalse(presentAttendances.isEmpty());
        assertTrue(presentAttendances.stream().anyMatch(a -> a.getId().equals(testAttendance.getId())));
        
        // Aggiungi un'assenza
        Attendance absence = new Attendance();
        absence.setPresent(false);
        absence.setEntryTime(testLesson.getStartDateTime().plusMinutes(5));
        absence.setExitTime(testLesson.getEndDateTime().minusMinutes(5));
        absence.setStudent(studentUser);
        absence.setLesson(testLesson);
        Attendance savedAbsence = attendanceService.save(absence);
        
        List<Attendance> absentAttendances = attendanceService.findByPresent(false);
        
        assertNotNull(absentAttendances);
        assertFalse(absentAttendances.isEmpty());
        assertTrue(absentAttendances.stream().anyMatch(a -> a.getId().equals(savedAbsence.getId())));
    }
    
    @Test
    public void testFindBySchoolClass() {
        List<Attendance> attendances = attendanceService.findBySchoolClass(testClass);
        
        assertNotNull(attendances);
        assertFalse(attendances.isEmpty());
        assertTrue(attendances.stream().anyMatch(a -> a.getId().equals(testAttendance.getId())));
    }
    
    @Test
    public void testCountPresentByStudent() {
        Long count = attendanceService.countPresentByStudent(studentUser);
        
        assertNotNull(count);
        assertEquals(1, count);
        
        // Aggiungi un'altra presenza
        Attendance anotherAttendance = new Attendance();
        anotherAttendance.setPresent(true);
        anotherAttendance.setEntryTime(testLesson.getStartDateTime().plusMinutes(10));
        anotherAttendance.setExitTime(testLesson.getEndDateTime().minusMinutes(10));
        anotherAttendance.setStudent(studentUser);
        anotherAttendance.setLesson(testLesson);
        attendanceService.save(anotherAttendance);
        
        Long newCount = attendanceService.countPresentByStudent(studentUser);
        assertEquals(2, newCount);
    }
    
    @Test
    public void testCountAbsentByStudent() {
        // All'inizio non ci sono assenze
        Long count = attendanceService.countAbsentByStudent(studentUser);
        assertEquals(0, count);
        
        // Aggiungi un'assenza
        Attendance absence = new Attendance();
        absence.setPresent(false);
        absence.setEntryTime(testLesson.getStartDateTime().plusMinutes(5));
        absence.setExitTime(testLesson.getEndDateTime().minusMinutes(5));
        absence.setStudent(studentUser);
        absence.setLesson(testLesson);
        attendanceService.save(absence);
        
        Long newCount = attendanceService.countAbsentByStudent(studentUser);
        assertEquals(1, newCount);
    }
}
