package com.riccardo.giangiulio.gestionescuola.dto.model;

import java.util.Objects;

public class ClassroomDTO {
    
    private Long id;
    private String name;
    private Integer capacity;
    
    public ClassroomDTO() {

    }

    public ClassroomDTO(Long id, String name, Integer capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassroomDTO that = (ClassroomDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(capacity, that.capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity);
    }
    
    
    

}
