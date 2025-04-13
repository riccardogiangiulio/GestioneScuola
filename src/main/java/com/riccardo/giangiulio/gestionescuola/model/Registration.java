package com.riccardo.giangiulio.gestionescuola.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "registrations")
@NoArgsConstructor
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    @NotNull(message = "The student cannot be null")
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    
    @NotNull(message = "The course cannot be null")
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    public Registration(User student, Course course, SchoolClass schoolClass) {
        this.registrationDate = LocalDateTime.now();
        this.student = student;
        this.course = course;
        this.schoolClass = schoolClass;
        this.status = RegistrationStatus.ACTIVE;
    }
}


