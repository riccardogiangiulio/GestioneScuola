package com.riccardo.giangiulio.gestionescuola.model;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "courses")
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The title cannot be empty")
    private String title;

    @NotBlank(message = "The description cannot be empty")
    private String description;

    @NotBlank(message = "The duration cannot be empty")
    private String duration;

    @NotNull(message = "The price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "the price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "The subjects cannot be null")
    @ManyToMany
    @JoinTable(
        name = "course_subject",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects;

    @ManyToMany
    @JoinTable(
        name = "course_exam",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "exam_id")
    )
    private Set<Exam> exams;

    public Course(String title, String description, String duration, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
    }

    public Course(String title, String description, String duration, BigDecimal price, Set<Subject> subjects, Set<Exam> exams) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.subjects = subjects;
        this.exams = exams;
    }
}
