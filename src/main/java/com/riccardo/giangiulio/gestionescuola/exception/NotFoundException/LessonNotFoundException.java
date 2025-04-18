package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class LessonNotFoundException extends ResourceNotFoundException {
    public LessonNotFoundException(Long id) {
        super("Lesson not found with ID: " + id);
    }
}
