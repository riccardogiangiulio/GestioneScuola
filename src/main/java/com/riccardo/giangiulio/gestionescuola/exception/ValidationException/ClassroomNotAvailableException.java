package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

import java.time.LocalDateTime;
 
public class ClassroomNotAvailableException extends BusinessValidationException {
    public ClassroomNotAvailableException(Long classroomId, LocalDateTime start, LocalDateTime end) {
        super("Classroom with ID: " + classroomId + " is not available in the requested time slot: start=" + start + ", end=" + end);
    }
} 