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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "school-classes")
@NoArgsConstructor
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The name cannot be empty")
    private String name;

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

    @NotNull(message = "The max students cannot be empty")
    private Integer maxStudents;

    public SchoolClass(String name, Course course, Integer maxStudents, Set<User> teachers) {
        this.name = name;
        this.course = course;
        this.maxStudents = maxStudents;
        this.teachers = teachers;
    }
}



