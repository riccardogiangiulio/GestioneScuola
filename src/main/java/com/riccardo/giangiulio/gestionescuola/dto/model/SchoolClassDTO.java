package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.riccardo.giangiulio.gestionescuola.dto.simple.CourseSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.dto.simple.RegistrationSimpleDTO;

public class SchoolClassDTO {
    
    private Long id;
    private String name;
    private Integer maxStudents;
    private CourseSimpleDTO course;
    private Set<UserDTO> teachers = new HashSet<>();
    private Set<RegistrationSimpleDTO> registrations = new HashSet<>();

    public SchoolClassDTO() {

    }

    public SchoolClassDTO(Long id, String name, Integer maxStudents, CourseSimpleDTO course, Set<UserDTO> teachers, Set<RegistrationSimpleDTO> registrations) {
        this.id = id;
        this.name = name;
        this.maxStudents = maxStudents;
        this.course = course;
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

    public CourseSimpleDTO getCourse() {
        return course;
    }          

    public Set<UserDTO> getTeachers() {
        return teachers;
    }   

    public Set<RegistrationSimpleDTO> getRegistrations() {
        return registrations;
    }   

    public void setName(String name) {
        this.name = name;
    }      

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }   

    public void setCourse(CourseSimpleDTO course) {
        this.course = course;
    }   

    public void setTeachers(Set<UserDTO> teachers) {
        this.teachers = teachers;
    }   

    public void setRegistrations(Set<RegistrationSimpleDTO> registrations) {
        this.registrations = registrations;
    }       

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClassDTO that = (SchoolClassDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(maxStudents, that.maxStudents) && Objects.equals(course, that.course) && Objects.equals(teachers, that.teachers) && Objects.equals(registrations, that.registrations);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxStudents, course, teachers, registrations);
    }
}
