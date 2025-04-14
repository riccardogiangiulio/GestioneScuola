package com.riccardo.giangiulio.gestionescuola.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "exams")
@NoArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The title cannot be empty")
    private String title;

    @NotBlank(message = "The description cannot be empty")
    private String description;

    @NotNull(message = "The date cannot be null")
    @Future(message = "The date cannot be in the past")
    private LocalDateTime date;

    @NotNull(message = "The duration cannot be null")
    private Integer duration;

    @NotNull(message = "The max score cannot be null")
    private Double maxScore;

    @NotNull(message = "The passing score cannot be null")
    private Double passingScore;

    @NotNull(message = "The classroom cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private Classroom classroom;

    @NotNull(message = "The subject cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private Subject subject;

    @NotNull(message = "The school class cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private SchoolClass schoolClass;    
    
    @NotNull(message = "The teacher cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private User teacher;

    @ManyToMany(mappedBy = "exams", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();

    public Exam(String title, String description, LocalDateTime date, Integer duration, Classroom classroom, Subject subject, Double maxScore, Double passingScore, SchoolClass schoolClass, User teacher) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.duration = duration;
        this.maxScore = maxScore;
        this.passingScore = passingScore;
        this.classroom = classroom;
        this.subject = subject;
        this.schoolClass = schoolClass;
        this.teacher = teacher;
    }
}
