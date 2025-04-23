package com.riccardo.giangiulio.gestionescuola.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

@Entity
@Table(name = "exam_results")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The score cannot be null")
    private Double score;

    @NotBlank(message = "The notes cannot be empty")
    private String notes;

    @Past(message = "The date cannot be in the future")
    @NotNull(message = "The date cannot be null")
    private LocalDateTime date;

    @NotNull(message = "The exam cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private Exam exam;

    @NotNull(message = "The student cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private User student;

    public ExamResult() {

    }

    public ExamResult(Double score, String notes, LocalDateTime date, Exam exam, User student) {
        this.score = score;
        this.notes = notes;
        this.date = date;
        this.exam = exam;
        this.student = student;
    }

    public Long getId() {
        return id;      
    }

    public Double getScore() {
        return score;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Exam getExam() {
        return exam;
    }

    public User getStudent() {
        return student;
    }

    public void setScore(Double score) {
        this.score = score;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }   

    public void setExam(Exam exam) {
        this.exam = exam;
    }   

    public void setStudent(User student) {
        this.student = student;
    }          

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamResult that = (ExamResult) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    
}

