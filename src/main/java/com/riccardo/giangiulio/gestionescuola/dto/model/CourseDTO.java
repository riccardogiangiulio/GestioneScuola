package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.riccardo.giangiulio.gestionescuola.dto.simple.ExamSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.dto.simple.SubjectSimpleDTO;

public class CourseDTO {
    
    private Long id;
    private String title;
    private String description;
    private String duration;
    private BigDecimal price;
    private Set<SubjectSimpleDTO> subjects = new HashSet<>();
    private Set<ExamSimpleDTO> exams = new HashSet<>();
    

    public CourseDTO() {

    }

    public CourseDTO(Long id, String title, String description, String duration, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
    }

    public CourseDTO(Long id, String title, String description, String duration, BigDecimal price, Set<SubjectSimpleDTO> subjects, Set<ExamSimpleDTO> exams) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.subjects = subjects;
        this.exams = exams;
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

    public String getDuration() {
        return duration;
    }

    public BigDecimal getPrice() {
        return price;   
    }

    public Set<SubjectSimpleDTO> getSubjects() {
        return subjects;
    }

    public Set<ExamSimpleDTO> getExams() {
        return exams;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setSubjects(Set<SubjectSimpleDTO> subjects) {
        this.subjects = subjects;
    }   

    public void setExams(Set<ExamSimpleDTO> exams) {
        this.exams = exams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseDTO courseDTO = (CourseDTO) o;
        return Objects.equals(id, courseDTO.id) && Objects.equals(title, courseDTO.title) && Objects.equals(description, courseDTO.description) && Objects.equals(duration, courseDTO.duration) && Objects.equals(price, courseDTO.price) && Objects.equals(subjects, courseDTO.subjects) && Objects.equals(exams, courseDTO.exams);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, duration, price, subjects, exams);
    }
    
    
}
