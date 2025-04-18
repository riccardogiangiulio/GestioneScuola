package com.riccardo.giangiulio.gestionescuola.exception.ValidationException;

import java.time.LocalDateTime;

public class TimeOutOfBoundsExceptions extends BusinessValidationException {
    public TimeOutOfBoundsExceptions(LocalDateTime lessonTime, LocalDateTime attendanceStart, LocalDateTime attendanceEnd) {
        super("The lesson time is out of bounds: \n lessonTime: " + lessonTime + "\n attendanceStart: " + attendanceStart + "\n attendanceEnd: " + attendanceEnd);
    }
}
