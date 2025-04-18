package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

public class ClassroomCapacityExceededException extends BusinessValidationException {
    public ClassroomCapacityExceededException(Long classroomId, int capacity, int required) {
        super("Classroom with ID: " + classroomId + " has a capacity of " + capacity + " but " + required + " students are required");
    }
}
