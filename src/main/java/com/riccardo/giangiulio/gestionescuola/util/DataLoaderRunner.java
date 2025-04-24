package com.riccardo.giangiulio.gestionescuola.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.riccardo.giangiulio.gestionescuola.model.Attendance;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;
import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;
import com.riccardo.giangiulio.gestionescuola.model.Registration;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.AttendanceRepository;
import com.riccardo.giangiulio.gestionescuola.repository.ClassroomRepository;
import com.riccardo.giangiulio.gestionescuola.repository.CourseRepository;
import com.riccardo.giangiulio.gestionescuola.repository.ExamRepository;
import com.riccardo.giangiulio.gestionescuola.repository.ExamResultRepository;
import com.riccardo.giangiulio.gestionescuola.repository.LessonRepository;
import com.riccardo.giangiulio.gestionescuola.repository.RegistrationRepository;
import com.riccardo.giangiulio.gestionescuola.repository.RoleRepository;
import com.riccardo.giangiulio.gestionescuola.repository.SchoolClassRepository;
import com.riccardo.giangiulio.gestionescuola.repository.SubjectRepository;
import com.riccardo.giangiulio.gestionescuola.repository.UserRepository;
@Component
public class DataLoaderRunner implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    
    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamResultRepository examResultRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public void run(String... args) throws Exception {
        // Controllo se i dati sono già stati caricati
        if (roleRepository.count() > 0) {
            System.out.println("I dati sono già stati caricati.");
            return;
        }
        
        System.out.println("Caricamento dati di test...");
        
        // Creazione ruoli
        Role adminRole = new Role(ERole.ROLE_ADMIN);
        Role teacherRole = new Role(ERole.ROLE_TEACHER);
        Role studentRole = new Role(ERole.ROLE_STUDENT);
        
        roleRepository.save(adminRole);
        roleRepository.save(teacherRole);
        roleRepository.save(studentRole);
        
        // Creazione utenti
        User admin = new User(
            "Admin", 
            "User", 
            "admin@scuola.it", 
            passwordEncoder.encode("password"), 
            LocalDate.of(1980, 1, 1), 
            adminRole
        );
        
        User teacher1 = new User(
            "Mario", 
            "Rossi", 
            "mario.rossi@scuola.it", 
            passwordEncoder.encode("password"), 
            LocalDate.of(1975, 5, 15), 
            teacherRole
        );
        
        User teacher2 = new User(
            "Giulia", 
            "Bianchi", 
            "giulia.bianchi@scuola.it", 
            passwordEncoder.encode("password"), 
            LocalDate.of(1980, 8, 20), 
            teacherRole
        );
        
        User student1 = new User(
            "Marco", 
            "Verdi", 
            "marco.verdi@scuola.it", 
            passwordEncoder.encode("password"), 
            LocalDate.of(2000, 3, 10), 
            studentRole
        );
        
        User student2 = new User(
            "Laura", 
            "Neri", 
            "laura.neri@scuola.it", 
            passwordEncoder.encode("password"), 
            LocalDate.of(2001, 7, 5), 
            studentRole
        );
        
        User student3 = new User(
            "Paolo", 
            "Gialli", 
            "paolo.gialli@scuola.it", 
            passwordEncoder.encode("password"), 
            LocalDate.of(1999, 12, 30), 
            studentRole
        );
        
        userRepository.save(admin);
        userRepository.save(teacher1);
        userRepository.save(teacher2);
        userRepository.save(student1);
        userRepository.save(student2);
        userRepository.save(student3);
        
        // Creazione aule
        Classroom classroom1 = new Classroom("Aula 1", 30);
        Classroom classroom2 = new Classroom("Aula 2", 25);
        Classroom classroom3 = new Classroom("Laboratorio", 20);
        
        classroomRepository.save(classroom1);
        classroomRepository.save(classroom2);
        classroomRepository.save(classroom3);
        
        // Creazione materie
        Subject subject1 = new Subject("Matematica", "Corso di matematica base", teacher1);
        Subject subject2 = new Subject("Informatica", "Principi di programmazione", teacher2);
        Subject subject3 = new Subject("Inglese", "Corso di inglese B2", teacher1);
        
        subjectRepository.save(subject1);
        subjectRepository.save(subject2);
        subjectRepository.save(subject3);
        
        // Creazione corsi
        Course course1 = new Course(
            "Corso di programmazione", 
            "Corso base di programmazione", 
            "6 mesi", 
            new BigDecimal("500.00")
        );
        course1.getSubjects().add(subject2);
        
        Course course2 = new Course(
            "Corso di lingue", 
            "Corso di lingue straniere", 
            "9 mesi", 
            new BigDecimal("600.00")
        );
        course2.getSubjects().add(subject3);
        
        Course course3 = new Course(
            "Corso completo", 
            "Corso completo di programmazione e lingue", 
            "12 mesi", 
            new BigDecimal("1000.00")
        );
        course3.getSubjects().addAll(Arrays.asList(subject1, subject2, subject3));
        
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        
        // Creazione classi
        Set<User> teachers1 = new HashSet<>();
        teachers1.add(teacher1);
        
        Set<User> teachers2 = new HashSet<>();
        teachers2.add(teacher2);
        
        Set<User> teachers3 = new HashSet<>();
        teachers3.add(teacher1);
        teachers3.add(teacher2);
        
        SchoolClass schoolClass1 = new SchoolClass(
            "Classe A", 
            course1, 
            20, 
            teachers1, 
            new HashSet<>()
        );
        
        SchoolClass schoolClass2 = new SchoolClass(
            "Classe B", 
            course2, 
            25, 
            teachers2, 
            new HashSet<>()
        );
        
        SchoolClass schoolClass3 = new SchoolClass(
            "Classe C", 
            course3, 
            15, 
            teachers3, 
            new HashSet<>()
        );
        
        schoolClassRepository.save(schoolClass1);
        schoolClassRepository.save(schoolClass2);
        schoolClassRepository.save(schoolClass3);
        
        // Creazione registrazioni
        Registration registration1 = new Registration(student1, course1, schoolClass1);
        Registration registration2 = new Registration(student2, course2, schoolClass2);
        Registration registration3 = new Registration(student3, course3, schoolClass3);
        Registration registration4 = new Registration(student1, course3, schoolClass3);
        
        // Cambiare lo stato di una registrazione per test
        registration4.setStatus(RegistrationStatus.COMPLETED);
        
        registrationRepository.save(registration1);
        registrationRepository.save(registration2);
        registrationRepository.save(registration3);
        registrationRepository.save(registration4);
        
        // Creazione lezioni
        LocalDateTime now = LocalDateTime.now();
        
        Lesson lesson1 = new Lesson(
            "Introduzione alla programmazione", 
            "Prima lezione del corso di programmazione", 
            now.plusDays(1).withHour(10).withMinute(0), 
            now.plusDays(1).withHour(12).withMinute(0), 
            schoolClass1, 
            teacher2, 
            classroom1, 
            subject2
        );
        
        Lesson lesson2 = new Lesson(
            "Grammatica inglese", 
            "Prima lezione del corso di inglese", 
            now.plusDays(2).withHour(14).withMinute(0), 
            now.plusDays(2).withHour(16).withMinute(0), 
            schoolClass2, 
            teacher1, 
            classroom2, 
            subject3
        );
        
        Lesson lesson3 = new Lesson(
            "Algebra di base", 
            "Lezione di matematica", 
            now.plusDays(3).withHour(9).withMinute(0), 
            now.plusDays(3).withHour(11).withMinute(0), 
            schoolClass3, 
            teacher1, 
            classroom3, 
            subject1
        );
        
        lessonRepository.save(lesson1);
        lessonRepository.save(lesson2);
        lessonRepository.save(lesson3);
        
        // Creazione esami
        Exam exam1 = new Exam(
            "Esame di programmazione", 
            "Esame finale del corso di programmazione", 
            now.plusMonths(6), 
            120, 
            classroom1, 
            subject2, 
            100.0, 
            60.0, 
            schoolClass1, 
            teacher2
        );
        
        Exam exam2 = new Exam(
            "Esame di inglese", 
            "Esame finale del corso di inglese", 
            now.plusMonths(9), 
            90, 
            classroom2, 
            subject3, 
            100.0, 
            65.0, 
            schoolClass2, 
            teacher1
        );
        
        Exam exam3 = new Exam(
            "Esame intermedio", 
            "Esame intermedio del corso completo", 
            now.plusMonths(3), 
            60, 
            classroom3, 
            subject1, 
            50.0, 
            30.0, 
            schoolClass3, 
            teacher1
        );
        
        examRepository.save(exam1);
        examRepository.save(exam2);
        examRepository.save(exam3);
        
        // Aggiungere gli esami ai corsi
        course1.getExams().add(exam1);
        course2.getExams().add(exam2);
        course3.getExams().add(exam3);
        
        courseRepository.saveAll(Arrays.asList(course1, course2, course3));

        ExamResult examResult1 = new ExamResult(100.0, "Ottimo", LocalDateTime.of(2023, 5, 15, 10, 0), exam1, student1);
        ExamResult examResult2 = new ExamResult(100.0, "Ottimo", LocalDateTime.of(2023, 6, 20, 14, 30), exam2, student2);
        ExamResult examResult3 = new ExamResult(50.0, "Buono", LocalDateTime.of(2023, 7, 10, 9, 0), exam3, student3);
        
        examResultRepository.save(examResult1);
        examResultRepository.save(examResult2);
        examResultRepository.save(examResult3);
        
        // Creazione presenze
        Attendance attendance1 = new Attendance(
            true, 
            lesson1.getStartDateTime().plusMinutes(5),
            lesson1.getEndDateTime().minusMinutes(10),
            student1,
            lesson1
        );
        
        Attendance attendance2 = new Attendance(
            true, 
            lesson2.getStartDateTime(),
            lesson2.getEndDateTime(),
            student2,
            lesson2
        );
        
        // Creare assenze (studenti che hanno dichiarato di non essere presenti ma per cui è necessario avere tempi di ingresso e uscita)
        Attendance attendance3 = new Attendance(
            false, 
            lesson3.getStartDateTime(),
            lesson3.getEndDateTime(),
            student3,
            lesson3
        );
        
        // Creare un'assenza per student1 a lesson3
        Attendance attendance4 = new Attendance(
            false, 
            lesson3.getStartDateTime(),
            lesson3.getEndDateTime(),
            student1,
            lesson3
        );
        
        // Creare una presenza per student3 a lesson1
        Attendance attendance5 = new Attendance(
            true, 
            lesson1.getStartDateTime().plusMinutes(15),
            lesson1.getEndDateTime(),
            student3,
            lesson1
        );
        
        attendanceRepository.save(attendance1);
        attendanceRepository.save(attendance2);
        attendanceRepository.save(attendance3);
        attendanceRepository.save(attendance4);
        attendanceRepository.save(attendance5);
        
        System.out.println("Dati di test caricati con successo!");
    }
}