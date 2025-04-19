package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class AttendanceNotFoundException extends ResourceNotFoundException {
    public AttendanceNotFoundException(Long id) {
        super("Attendance not found with ID: " + id);
    }

    public AttendanceNotFoundException(Long lessonId, Long studentId) {
        super("Attendance not found for lesson " + lessonId + " and student " + studentId);
    }
}