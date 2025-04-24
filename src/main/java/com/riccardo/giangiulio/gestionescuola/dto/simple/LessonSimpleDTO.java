package com.riccardo.giangiulio.gestionescuola.dto.simple;

import java.time.LocalDateTime;
import java.util.Objects;
public class LessonSimpleDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public LessonSimpleDTO() {

    }

    public LessonSimpleDTO(Long id, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonSimpleDTO that = (LessonSimpleDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(startDateTime, that.startDateTime) && Objects.equals(endDateTime, that.endDateTime);
    }       

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, startDateTime, endDateTime);
    }   
    
    
    
    
}
