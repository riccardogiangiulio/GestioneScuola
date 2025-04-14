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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "subjects")
@NoArgsConstructor
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

    public Subject(String name, String description, User teacher) {
        this.name = name;
        this.description = description;
        this.teacher = teacher;
    }
}