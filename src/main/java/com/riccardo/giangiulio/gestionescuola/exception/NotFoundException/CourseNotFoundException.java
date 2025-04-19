package com.riccardo.giangiulio.gestionescuola.exception.NotFoundException;

public class CourseNotFoundException extends ResourceNotFoundException {
    public CourseNotFoundException(Long id) {
        super("Course not found with ID: " + id);
    }

    public CourseNotFoundException(String title) {
        super("Course not found with title: " + title);
    }
}
