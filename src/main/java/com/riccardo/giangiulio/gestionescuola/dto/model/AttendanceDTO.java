package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.dto.simple.LessonSimpleDTO;

public class AttendanceDTO {
    
    private Long id;
    private Boolean present;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private UserDTO student;
    private LessonSimpleDTO lesson;

    public AttendanceDTO() {

    }

    public AttendanceDTO(Long id, Boolean present, LocalDateTime entryTime, LocalDateTime exitTime, UserDTO student, LessonSimpleDTO lesson) {
        this.id = id;
        this.present = true;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.student = student;
        this.lesson = lesson;
    }

    public Long getId() {
        return id;
    }

    public Boolean getPresent() {
        return present;
    }   

    public LocalDateTime getEntryTime() {
        return entryTime;
    }   

    public LocalDateTime getExitTime() {
        return exitTime;    
    }   

    public UserDTO getStudent() {
        return student;
    }      

    public LessonSimpleDTO getLesson() {
        return lesson;
    }      

    public void setPresent(Boolean present) {
        this.present = present;
    }   

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }   

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }   

    public void setStudent(UserDTO student) {
        this.student = student;
    }      

    public void setLesson(LessonSimpleDTO lesson) {
        this.lesson = lesson;
    }          

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceDTO that = (AttendanceDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(present, that.present) && Objects.equals(entryTime, that.entryTime) && Objects.equals(exitTime, that.exitTime) && Objects.equals(student, that.student) && Objects.equals(lesson, that.lesson);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, present, entryTime, exitTime, student, lesson);
    }   
    
    
    
    
}
