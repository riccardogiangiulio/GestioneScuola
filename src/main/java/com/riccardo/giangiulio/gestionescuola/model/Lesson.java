package com.riccardo.giangiulio.gestionescuola.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "lessons")
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The title cannot be empty")
    private String title;

    @NotBlank(message = "The description cannot be empty")
    private String description;

    @NotNull(message = "The start date and time cannot be empty")
    @Future(message = "The start date and time cannot be in the past")
    private LocalDateTime startDateTime;

    @NotNull(message = "The end date and time cannot be empty")
    @Future(message = "The end date and time cannot be in the past")
    private LocalDateTime endDateTime;

    @NotNull(message = "The school class cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @NotNull(message = "The teacher cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @NotNull(message = "The classroom cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @NotNull(message = "The subject cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    public Lesson(String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, SchoolClass schoolClass, User teacher, Classroom classroom, Subject subject) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.schoolClass = schoolClass;
        this.teacher = teacher;
        this.classroom = classroom;
        this.subject = subject;
    }
}