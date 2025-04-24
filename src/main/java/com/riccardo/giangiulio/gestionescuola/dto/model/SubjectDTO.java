package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.riccardo.giangiulio.gestionescuola.dto.simple.CourseSimpleDTO;

public class SubjectDTO {
    
    private Long id;
    private String name;
    private String description;
    private UserDTO teacher;
    private Set<CourseSimpleDTO> courses = new HashSet<>();

    public SubjectDTO() {

    }

    public SubjectDTO(Long id, String name, String description, UserDTO teacher, Set<CourseSimpleDTO> courses) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.teacher = teacher;
        this.courses = courses;
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

    public UserDTO getTeacher() {
        return teacher;
    }

    public Set<CourseSimpleDTO> getCourses() {
        return courses;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeacher(UserDTO teacher) {
        this.teacher = teacher;
    }

    public void setCourses(Set<CourseSimpleDTO> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectDTO that = (SubjectDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(teacher, that.teacher) && Objects.equals(courses, that.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, teacher, courses);
    }
    
}
