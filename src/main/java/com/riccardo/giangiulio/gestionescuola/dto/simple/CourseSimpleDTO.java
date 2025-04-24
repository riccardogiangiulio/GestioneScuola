package com.riccardo.giangiulio.gestionescuola.dto.simple;

import java.math.BigDecimal;
import java.util.Objects;
public class CourseSimpleDTO {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private BigDecimal price;

    public CourseSimpleDTO() {

    }   

    public CourseSimpleDTO(Long id, String title, String description, String duration, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseSimpleDTO that = (CourseSimpleDTO) o; 
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(duration, that.duration) && Objects.equals(price, that.price);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, duration, price);
    }   
}
