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

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.CourseNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ExamNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.SubjectNotFoundException;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.CourseRepository;
import com.riccardo.giangiulio.gestionescuola.repository.ExamRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseServiceIntegrationTest {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private SchoolClassService schoolClassService;
    
    @Autowired
    private ClassroomService classroomService;
    
    
    private Course testCourse;
    private Subject testSubject;
    private Exam testExam;
    private User testTeacher;
    
    @BeforeEach
    public void setUp() {
        
        courseRepository.deleteAll();
        examRepository.deleteAll();
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        Role teacherRole;
        try {
            teacherRole = roleService.getRoleByName(ERole.ROLE_TEACHER);
        } catch (Exception e) {
            teacherRole = new Role(ERole.ROLE_TEACHER);
            teacherRole = roleService.saveRole(teacherRole);
        }
        
        Role studentRole;
        try {
            roleService.getRoleByName(ERole.ROLE_STUDENT);
        } catch (Exception e) {
            studentRole = new Role(ERole.ROLE_STUDENT);
            roleService.saveRole(studentRole);
        }
        
        testTeacher = new User();
        testTeacher.setFirstName("Teacher Test");
        testTeacher.setLastName("Course");
        testTeacher.setEmail("teacher_course_" + timestamp + "@example.com");
        testTeacher.setPassword("password");
        testTeacher.setBirthDate(LocalDate.of(1980, 1, 1));
        testTeacher.setRole(teacherRole);
        testTeacher = userService.save(testTeacher);
        
        testSubject = new Subject();
        testSubject.setName("Subject Test " + timestamp);
        testSubject.setDescription("Description subject test");
        testSubject.setTeacher(testTeacher);
        testSubject = subjectService.save(testSubject);
        
        testCourse = new Course();
        testCourse.setTitle("Course Test " + timestamp);
        testCourse.setDescription("Description course test");
        testCourse.setDuration("12 months");
        testCourse.setPrice(BigDecimal.valueOf(1000.0));
        
        Set<Subject> subjects = new HashSet<>();
        subjects.add(testSubject);
        testCourse.setSubjects(subjects);
        
        testCourse = courseService.save(testCourse);
        
        Set<User> teachers = new HashSet<>();
        teachers.add(testTeacher);
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName("School Class Test " + timestamp);
        schoolClass.setMaxStudents(30);
        schoolClass.setCourse(testCourse);
        schoolClass.setTeachers(teachers);
        schoolClass = schoolClassService.save(schoolClass);
        
        Classroom classroom = new Classroom("Classroom Test " + timestamp, 30);
        classroom = classroomService.save(classroom);
        
        // Crea un esame completo con tutti i campi obbligatori
        LocalDateTime examDate = LocalDateTime.now().plusDays(7); // Data futura per rispettare @Future
        
        testExam = new Exam();
        testExam.setTitle("Exam Test " + timestamp);
        testExam.setDescription("Description exam test");
        testExam.setDate(examDate);
        testExam.setDuration(120); // 2 ore
        testExam.setMaxScore(30.0);
        testExam.setPassingScore(18.0);
        testExam.setClassroom(classroom);
        testExam.setSubject(testSubject);
        testExam.setSchoolClass(schoolClass);
        testExam.setTeacher(testTeacher);
        
        testExam = examRepository.save(testExam);
    }
    
    @Test
    public void testFindAll() {
        List<Course> courses = courseService.findAll();
        
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        assertTrue(courses.stream().anyMatch(c -> c.getId().equals(testCourse.getId())));
    }
    
    @Test
    public void testFindById() {
        Course found = courseService.findById(testCourse.getId());
        
        assertNotNull(found);
        assertEquals(testCourse.getId(), found.getId());
        assertEquals(testCourse.getTitle().substring(10), found.getTitle().substring(10));
        assertEquals(testCourse.getDescription(),found.getDescription());
    }
    
    @Test
    public void testFindByIdNotFound() {
        Long nonExistentId = 999999L;
        
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.findById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testSave() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Course newCourse = new Course();
        newCourse.setTitle("New Course " + timestamp);
        newCourse.setDescription("Description new course");
        newCourse.setDuration("6 months");
        newCourse.setPrice(BigDecimal.valueOf(500.0));
        
        Set<Subject> subjects = new HashSet<>();
        subjects.add(testSubject);
        newCourse.setSubjects(subjects);
        
        Course saved = courseService.save(newCourse);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("New Course " + timestamp, saved.getTitle());
        assertEquals(BigDecimal.valueOf(500.0), saved.getPrice());
        
        Course retrieved = courseService.findById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
    }
    
    @Test
    public void testUpdate() {
        testCourse.setTitle("Course Updated");
        testCourse.setDescription("Updated description");
        testCourse.setPrice(BigDecimal.valueOf(1200.0));
        
        Course updated = courseService.update(testCourse.getId(), testCourse);
        
        assertNotNull(updated);
        assertEquals(testCourse.getId(), updated.getId());
        assertEquals("Course Updated", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(BigDecimal.valueOf(1200.0), updated.getPrice());
        
        Course retrieved = courseService.findById(testCourse.getId());
        assertEquals("Course Updated", retrieved.getTitle());
    }
    
    @Test
    public void testUpdateNotFound() {
        Long nonExistentId = 999999L;
        Course updateData = new Course();
        updateData.setTitle("Course Not Found");
        updateData.setDescription("Description course not found");
        updateData.setDuration("3 months");
        updateData.setPrice(BigDecimal.valueOf(300.0));
        
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.update(nonExistentId, updateData);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteById() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Course courseToDelete = new Course();
        courseToDelete.setTitle("Course to Delete " + timestamp);
        courseToDelete.setDescription("Description course to delete");
        courseToDelete.setDuration("9 months");
        courseToDelete.setPrice(BigDecimal.valueOf(750.0));
        
        courseToDelete = courseService.save(courseToDelete);
        Long idToDelete = courseToDelete.getId();
        
        courseService.deleteById(idToDelete);
        
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.findById(idToDelete);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testDeleteByIdNotFound() {
        Long nonExistentId = 999999L;
        
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.deleteById(nonExistentId);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testFindByTitle() {
        Course found = courseService.findByTitle(testCourse.getTitle());
        
        assertNotNull(found);
        assertEquals(testCourse.getId(), found.getId());
    }
    
    @Test
    public void testFindByTitleNotFound() {
        String nonExistentTitle = "Course Not Found XYZ";
        
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class, () -> {
            courseService.findByTitle(nonExistentTitle);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testAddSubject() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Subject newSubject = new Subject();
        newSubject.setName("New Subject " + timestamp);
        newSubject.setDescription("Description new subject");
        newSubject.setTeacher(testTeacher);
        Subject savedSubject = subjectService.save(newSubject);
        
        // Aggiungi il subject al corso
        courseService.addSubject(testCourse.getId(), savedSubject.getId());
        
        Course updated = courseService.findById(testCourse.getId());
        assertTrue(updated.getSubjects().stream().anyMatch(s -> s.getId().equals(newSubject.getId())));
    }
    
    @Test
    public void testAddSubjectAlreadyAssociated() {
        // Il subject è già associato al corso
        courseService.addSubject(testCourse.getId(), testSubject.getId());
        
        Course updated = courseService.findById(testCourse.getId());
        assertTrue(updated.getSubjects().stream().anyMatch(s -> s.getId().equals(testSubject.getId())));
    }
    
    @Test
    public void testRemoveSubject() {
        // Rimuovi il subject dal corso
        courseService.removeSubject(testCourse.getId(), testSubject.getId());
        
        Course updated = courseService.findById(testCourse.getId());
        assertFalse(updated.getSubjects().stream().anyMatch(s -> s.getId().equals(testSubject.getId())));
    }
    
    @Test
    public void testRemoveSubjectNotAssociated() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Subject unassociatedSubject = new Subject();
        unassociatedSubject.setName("Subject Not Associated " + timestamp);
        unassociatedSubject.setDescription("Description subject not associated");
        unassociatedSubject.setTeacher(testTeacher);
        Subject savedUnassociatedSubject = subjectService.save(unassociatedSubject);
        
        // Tenta di rimuovere un subject non associato
        SubjectNotFoundException exception = assertThrows(SubjectNotFoundException.class, () -> {
            courseService.removeSubject(testCourse.getId(), savedUnassociatedSubject.getId());
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    public void testAddExam() {
        courseService.addExam(testCourse.getId(), testExam.getId());
        
        Course updated = courseService.findById(testCourse.getId());
        assertTrue(updated.getExams().stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testAddExamAlreadyAssociated() {
        courseService.addExam(testCourse.getId(), testExam.getId());
        
        // Tenta di aggiungere lo stesso esame di nuovo
        courseService.addExam(testCourse.getId(), testExam.getId());
        
        // Verifica che non ci siano errori e che l'esame sia ancora associato
        Course updated = courseService.findById(testCourse.getId());
        assertTrue(updated.getExams().stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testRemoveExam() {
        // Aggiungi l'esame al corso
        courseService.addExam(testCourse.getId(), testExam.getId());
        
        // Rimuovi l'esame dal corso
        courseService.removeExam(testCourse.getId(), testExam.getId());
        
        // Verifica che l'esame sia stato rimosso
        Course updated = courseService.findById(testCourse.getId());
        assertFalse(updated.getExams().stream().anyMatch(e -> e.getId().equals(testExam.getId())));
    }
    
    @Test
    public void testRemoveExamNotAssociated() {
        // Crea un nuovo esame non associato ma completo con tutti i campi obbligatori
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // Crea una classe scolastica per l'esame non associato (o usa quella esistente)
        SchoolClass schoolClass = testExam.getSchoolClass();
        
        // Crea un'aula per l'esame non associato (o usa quella esistente)
        Classroom classroom = testExam.getClassroom();
        
        // Crea un nuovo esame
        Exam unassociatedExam = new Exam();
        unassociatedExam.setTitle("Unassociated Exam " + timestamp);
        unassociatedExam.setDescription("Description unassociated exam");
        unassociatedExam.setDate(LocalDateTime.now().plusDays(14)); // Data futura
        unassociatedExam.setDuration(90); // 1.5 ore
        unassociatedExam.setMaxScore(30.0);
        unassociatedExam.setPassingScore(18.0);
        unassociatedExam.setClassroom(classroom);
        unassociatedExam.setSubject(testSubject);
        unassociatedExam.setSchoolClass(schoolClass);
        unassociatedExam.setTeacher(testTeacher);
        
        // Salva l'esame
        Exam savedUnassociatedExam = examRepository.save(unassociatedExam);
        
        // Tenta di rimuovere un esame non associato
        ExamNotFoundException exception = assertThrows(ExamNotFoundException.class, () -> {
            courseService.removeExam(testCourse.getId(), savedUnassociatedExam.getId());
        });
        
        assertNotNull(exception.getMessage());
    }
}
