package com.riccardo.giangiulio.gestionescuola.model;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The name cannot be empty")
    private String name;

    @NotBlank(message = "The description cannot be empty")
    private String description;

    @NotNull(message = "The teacher cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private User teacher;

    @ManyToMany(mappedBy = "subjects", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();

    public Subject() {
    }   

    public Subject(String name, String description, User teacher) {
        this.name = name;
        this.description = description;
        this.teacher = teacher;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }   

    public User getTeacher() {
        return teacher;
    }   

    public Set<Course> getCourses() {
        return courses;
    }   

    public void setName(String name) {
        this.name = name;
    }   

    public void setDescription(String description) {
        this.description = description;
    }   

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }   

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }   
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return id != null ? id.equals(subject.id) : subject.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}