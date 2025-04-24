package com.riccardo.giangiulio.gestionescuola.dto.simple;

import java.util.Objects;

public class SchoolClassSimpleDTO {
    private Long id;
    private String name;
    private Integer maxStudents;

    public SchoolClassSimpleDTO() {

    }

    public SchoolClassSimpleDTO(Long id, String name, Integer maxStudents) {
        this.id = id;
        this.name = name;
        this.maxStudents = maxStudents;
    }   

    public Long getId() {
        return id;
    }          

    public String getName() {
        return name;
    }   

    public Integer getMaxStudents() {
        return maxStudents;
    }   

    public void setName(String name) {
        this.name = name;
    }   

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;  
        SchoolClassSimpleDTO that = (SchoolClassSimpleDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(maxStudents, that.maxStudents);
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxStudents);
    }   
}