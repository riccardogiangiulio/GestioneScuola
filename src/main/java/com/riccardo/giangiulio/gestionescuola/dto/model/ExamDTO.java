package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.dto.simple.SchoolClassSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.dto.simple.SubjectSimpleDTO;

public class ExamDTO {
    
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private Integer duration;
    private Double maxScore;
    private Double passingScore;
    private ClassroomDTO classroom;
    private SubjectSimpleDTO subject;
    private SchoolClassSimpleDTO schoolClass;
    private UserDTO teacher;

    public ExamDTO() {

    }

    public ExamDTO(Long id, String title, String description, LocalDateTime date, Integer duration, Double maxScore, Double passingScore, ClassroomDTO classroom, SubjectSimpleDTO subject, SchoolClassSimpleDTO schoolClass, UserDTO teacher) {
        this.id = id;
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

    public Long getId() {
        return id;
    }   

    public String getTitle() {
        return title;
    }   

    public String getDescription() {
        return description;
    }   

    public LocalDateTime getDate() {
        return date;
    }   

    public Integer getDuration() {
        return duration;
    }   

    public Double getMaxScore() {
        return maxScore;
    }   

    public Double getPassingScore() {
        return passingScore;
    }   

    public ClassroomDTO getClassroom() {
        return classroom;
    }   

    public SubjectSimpleDTO getSubject() {
        return subject;
    }   
    
    public SchoolClassSimpleDTO getSchoolClass() {
        return schoolClass;
    }   

    public UserDTO getTeacher() {
        return teacher;
    }      

    public void setTitle(String title) {
        this.title = title;
    }      

    public void setDescription(String description) {
        this.description = description;
    }          

    public void setDate(LocalDateTime date) {
        this.date = date;
    }      

    public void setDuration(Integer duration) {
        this.duration = duration;
    }                 

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }   

    public void setPassingScore(Double passingScore) {
        this.passingScore = passingScore;
    }       

    public void setClassroom(ClassroomDTO classroom) {
        this.classroom = classroom;
    }   

    public void setSubject(SubjectSimpleDTO subject) {
        this.subject = subject;
    }   

    public void setSchoolClass(SchoolClassSimpleDTO schoolClass) {
        this.schoolClass = schoolClass;
    }   

    public void setTeacher(UserDTO teacher) {
        this.teacher = teacher;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamDTO examDTO = (ExamDTO) o;
        return Objects.equals(id, examDTO.id) && Objects.equals(title, examDTO.title) && Objects.equals(description, examDTO.description) && Objects.equals(date, examDTO.date) && Objects.equals(duration, examDTO.duration) && Objects.equals(maxScore, examDTO.maxScore) && Objects.equals(passingScore, examDTO.passingScore) && Objects.equals(classroom, examDTO.classroom) && Objects.equals(subject, examDTO.subject) && Objects.equals(schoolClass, examDTO.schoolClass) && Objects.equals(teacher, examDTO.teacher);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, date, duration, maxScore, passingScore, classroom, subject, schoolClass, teacher);
    }   

}
