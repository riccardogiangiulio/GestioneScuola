package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.AttendanceDTO;
import com.riccardo.giangiulio.gestionescuola.model.Attendance;

public class AttendanceMapperDTO {

    public static AttendanceDTO toDTO(Attendance attendance) {
        return new AttendanceDTO(attendance.getId(), attendance.getPresent(), attendance.getEntryTime(), attendance.getExitTime(), UserMapperDTO.toDTO(attendance.getStudent()), LessonSimpleMapperDTO.toSimpleDTO(attendance.getLesson()));
    }   
}
