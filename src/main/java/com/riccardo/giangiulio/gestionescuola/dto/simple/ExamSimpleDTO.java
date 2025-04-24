package com.riccardo.giangiulio.gestionescuola.dto.simple;

import java.util.Objects;

public class ExamSimpleDTO {
    private Long id;
    private String title;
    private String description;
    private Double maxScore;
    private Double passingScore;

    public ExamSimpleDTO() {

    }

    public ExamSimpleDTO(Long id, String title, String description, Double maxScore, Double passingScore) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.maxScore = maxScore;
        this.passingScore = passingScore;
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

    public Double getMaxScore() {
        return maxScore;
    }

    public Double getPassingScore() {
        return passingScore;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public void setPassingScore(Double passingScore) {
        this.passingScore = passingScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamSimpleDTO that = (ExamSimpleDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(maxScore, that.maxScore) && Objects.equals(passingScore, that.passingScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, maxScore, passingScore);
    }
    
    
    
    
    
}
