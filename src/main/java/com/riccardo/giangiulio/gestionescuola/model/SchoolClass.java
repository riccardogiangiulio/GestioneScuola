package com.riccardo.giangiulio.gestionescuola.model;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "school_classes")
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The name cannot be empty")
    private String name;
    
    @NotNull(message = "The max students cannot be empty")
    private Integer maxStudents;

    @NotNull(message = "The course cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @NotNull(message = "The teachers cannot be null")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "class_teacher",
        joinColumns = @JoinColumn(name = "class_id"),
        inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private Set<User> teachers = new HashSet<>();

    @NotNull(message = "The registrations cannot be null")
    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<Registration> registrations = new HashSet<>();

    public SchoolClass() {
    
    }   

    public SchoolClass(String name, Course course, Integer maxStudents, Set<User> teachers, Set<Registration> registrations) {
        this.name = name;
        this.course = course;
        this.maxStudents = maxStudents;
        this.teachers = teachers;
        this.registrations = registrations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public Course getCourse() {
        return course;
    }   

    public Set<User> getTeachers() {
        return teachers;
    }      

    public Set<Registration> getRegistrations() {
        return registrations;
    }      

    public void setName(String name) {
        this.name = name;
    }   

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }      

    public void setCourse(Course course) {
        this.course = course;
    }   

    public void setTeachers(Set<User> teachers) {
        this.teachers = teachers;
    }   

    public void setRegistrations(Set<Registration> registrations) {
        this.registrations = registrations;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClass that = (SchoolClass) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}


