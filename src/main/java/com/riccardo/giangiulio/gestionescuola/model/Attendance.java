package com.riccardo.giangiulio.gestionescuola.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "attendances")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The presence cannot be null")
    private Boolean present;

    @NotNull(message = "The entry time cannot be null")
    private LocalDateTime entryTime;

    @NotNull(message = "The exit time cannot be null")
    private LocalDateTime exitTime;

    @NotNull(message = "The student cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;
    
    @NotNull(message = "The lesson cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    public Attendance() {

    }   

    public Attendance(Boolean present, LocalDateTime entryTime, LocalDateTime exitTime, User student, Lesson lesson) {
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

    public User getStudent() {
        return student;
    }

    public Lesson getLesson() {
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

    public void setStudent(User student) {
        this.student = student;
    }   

    public void setLesson(Lesson lesson) {
       this.lesson = lesson;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }   

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }   
    
}
