package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.dto.simple.SchoolClassSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.dto.simple.SubjectSimpleDTO;

public class LessonDTO {
    
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private SchoolClassSimpleDTO schoolClass;
    private UserDTO teacher;
    private ClassroomDTO classroom;
    private SubjectSimpleDTO subject;

    public LessonDTO() {

    }       

    public LessonDTO(Long id, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, SchoolClassSimpleDTO schoolClass, UserDTO teacher, ClassroomDTO classroom, SubjectSimpleDTO subject) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.schoolClass = schoolClass;
        this.teacher = teacher;
        this.classroom = classroom;
        this.subject = subject;
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

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }   

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }   

    public SchoolClassSimpleDTO getSchoolClass() {
        return schoolClass;
    }   

    public UserDTO getTeacher() {
        return teacher;
    }   
    
    public ClassroomDTO getClassroom() {
        return classroom;
    }   

    public SubjectSimpleDTO getSubject() {
        return subject;
    }   

    public void setTitle(String title) {
        this.title = title;
    }   

    public void setDescription(String description) {
        this.description = description;
    }   

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }   

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }   

    public void setSchoolClass(SchoolClassSimpleDTO schoolClass) {
        this.schoolClass = schoolClass;
    }   

    public void setTeacher(UserDTO teacher) {
        this.teacher = teacher;
    }   

    public void setClassroom(ClassroomDTO classroom) {
        this.classroom = classroom;
    }   

    public void setSubject(SubjectSimpleDTO subject) {
        this.subject = subject;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonDTO lessonDTO = (LessonDTO) o;    
        return Objects.equals(id, lessonDTO.id) && Objects.equals(title, lessonDTO.title) && Objects.equals(description, lessonDTO.description) && Objects.equals(startDateTime, lessonDTO.startDateTime) && Objects.equals(endDateTime, lessonDTO.endDateTime) && Objects.equals(schoolClass, lessonDTO.schoolClass) && Objects.equals(teacher, lessonDTO.teacher) && Objects.equals(classroom, lessonDTO.classroom) && Objects.equals(subject, lessonDTO.subject);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, startDateTime, endDateTime, schoolClass, teacher, classroom, subject);
    }   
    
}
