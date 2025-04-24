package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.dto.simple.ExamSimpleDTO;

public class ExamResultDTO {
    
    private Long id;
    private Double score;
    private String notes;
    private LocalDateTime date;
    private ExamSimpleDTO exam;
    private UserDTO student;

    public ExamResultDTO() {

    }

    public ExamResultDTO(Long id, Double score, String notes, LocalDateTime date, ExamSimpleDTO exam, UserDTO student) {
        this.id = id;
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

    public ExamSimpleDTO getExam() {
        return exam;
    }   

    public UserDTO getStudent() {
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

    public void setExam(ExamSimpleDTO exam) {
        this.exam = exam;
    }   

    public void setStudent(UserDTO student) {
        this.student = student;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamResultDTO that = (ExamResultDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(score, that.score) && Objects.equals(notes, that.notes) && Objects.equals(date, that.date) && Objects.equals(exam, that.exam) && Objects.equals(student, that.student);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, score, notes, date, exam, student);
    }   
    
    
    
    
    
}
