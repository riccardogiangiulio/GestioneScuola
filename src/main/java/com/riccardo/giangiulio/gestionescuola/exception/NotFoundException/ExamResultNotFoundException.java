package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class ExamResultNotFoundException extends ResourceNotFoundException {
    public ExamResultNotFoundException(Long id) {
        super("Exam result not found with ID: " + id);
    }
}
