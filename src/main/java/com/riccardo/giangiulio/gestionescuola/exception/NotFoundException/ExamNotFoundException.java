package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class ExamNotFoundException extends ResourceNotFoundException {
    public ExamNotFoundException(Long id) {
        super("Exam not found with ID: " + id);
    }
}
