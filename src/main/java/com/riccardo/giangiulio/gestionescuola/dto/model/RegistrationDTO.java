package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.dto.simple.CourseSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.dto.simple.SchoolClassSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;

public class RegistrationDTO {
    
    private Long id;
    private LocalDateTime registrationDate;
    private RegistrationStatus status;
    private UserDTO student;
    private CourseSimpleDTO course;
    private SchoolClassSimpleDTO schoolClass;

    public RegistrationDTO() {

    }   

    public RegistrationDTO(Long id, LocalDateTime registrationDate, RegistrationStatus status, UserDTO student, CourseSimpleDTO course, SchoolClassSimpleDTO schoolClass) {
        this.id = id;
        this.registrationDate = registrationDate;
        this.status = status;
        this.student = student;
        this.course = course;
        this.schoolClass = schoolClass;
    }   

    public Long getId() {
        return id;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }   

    public RegistrationStatus getStatus() {
        return status;
    }      

    public UserDTO getStudent() {
        return student;
    }   

    public CourseSimpleDTO getCourse() {
        return course;
    }   

    public SchoolClassSimpleDTO getSchoolClass() {
        return schoolClass;
    }   

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }   

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }   

    public void setStudent(UserDTO student) {
        this.student = student;
    }   

    public void setCourse(CourseSimpleDTO course) {
        this.course = course;
    }   

    public void setSchoolClass(SchoolClassSimpleDTO schoolClass) {
        this.schoolClass = schoolClass;
    }   
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationDTO that = (RegistrationDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(registrationDate, that.registrationDate) && status == that.status && Objects.equals(student, that.student) && Objects.equals(course, that.course) && Objects.equals(schoolClass, that.schoolClass);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationDate, status, student, course, schoolClass);
    }   
    
    
}