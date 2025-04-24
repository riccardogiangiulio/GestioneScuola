package com.riccardo.giangiulio.gestionescuola.dto.simple;

import java.time.LocalDateTime;
import java.util.Objects;

import com.riccardo.giangiulio.gestionescuola.model.RegistrationStatus;

public class RegistrationSimpleDTO {
    private Long id;
    private LocalDateTime registrationDate;
    private RegistrationStatus status;

    public RegistrationSimpleDTO() {

    }   

    public RegistrationSimpleDTO(Long id, LocalDateTime registrationDate, RegistrationStatus status) {
        this.id = id;
        this.registrationDate = registrationDate;
        this.status = status;
    }   

    public Long getId() {
        return id;
    }   

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }   

    public RegistrationStatus getStatus() {
        return status;
    }   

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }   

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationSimpleDTO that = (RegistrationSimpleDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(registrationDate, that.registrationDate) && status == that.status;
    }   

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationDate, status);
    }   
}
