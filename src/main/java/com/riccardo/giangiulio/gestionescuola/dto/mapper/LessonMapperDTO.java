package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.LessonDTO;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;

public class LessonMapperDTO {
    
    public static LessonDTO toDTO(Lesson lesson) {
        return new LessonDTO(lesson.getId(), lesson.getTitle(), lesson.getDescription(), lesson.getStartDateTime(), lesson.getEndDateTime(), SchoolClassSimpleMapperDTO.toSimpleDTO(lesson.getSchoolClass()), UserMapperDTO.toDTO(lesson.getTeacher()), ClassroomMapperDTO.toDTO(lesson.getClassroom()), SubjectSimpleMapperDTO.toSimpleDTO(lesson.getSubject()));
    }
}
