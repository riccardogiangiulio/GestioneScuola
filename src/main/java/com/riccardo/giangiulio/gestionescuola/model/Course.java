package com.riccardo.giangiulio.gestionescuola.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Entity
@Table(name = "courses")
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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_subject",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_exam",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "exam_id")
    )
    private Set<Exam> exams = new HashSet<>();

    public Course() {

    }

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

    public Long getId() {
        return id;
    }   

    public String getTitle() {
        return title;
    }   

    public String getDescription() {
        return description;
    }   

    public String getDuration() {
        return duration;
    }   

    public BigDecimal getPrice() {
        return price;
    }   

    public Set<Subject> getSubjects() {
        return subjects;
    }   

    public Set<Exam> getExams() {
        return exams;
    }   

    public void setTitle(String title) {
        this.title = title;
    }   

    public void setDescription(String description) {
        this.description = description;
    }   

    public void setDuration(String duration) {
        this.duration = duration;
    }   

    public void setPrice(BigDecimal price) {
        this.price = price;
    }   

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }   

    public void setExams(Set<Exam> exams) {
        this.exams = exams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id != null ? id.equals(course.id) : course.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
