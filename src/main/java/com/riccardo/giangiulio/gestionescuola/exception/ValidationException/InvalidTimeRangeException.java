package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

import java.time.LocalDateTime;

public class InvalidTimeRangeException extends BusinessValidationException {
    public InvalidTimeRangeException(LocalDateTime start, LocalDateTime end) {
        super("The time of end is before the time of start: \n start: " + start + "\n end: " + end);
    }
}
