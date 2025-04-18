package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class AttendanceNotFoundException extends ResourceNotFoundException {
    public AttendanceNotFoundException(Long id) {
        super("Attendance not found with ID: " + id);
    }
}