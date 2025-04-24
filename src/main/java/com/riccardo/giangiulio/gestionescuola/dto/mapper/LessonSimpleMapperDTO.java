package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.riccardo.giangiulio.gestionescuola.dto.simple.LessonSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.model.Lesson;

public class LessonSimpleMapperDTO {

    public static LessonSimpleDTO toSimpleDTO(Lesson lesson) {
        return new LessonSimpleDTO(lesson.getId(), lesson.getTitle(), lesson.getDescription(), lesson.getStartDateTime(), lesson.getEndDateTime());
    }

    public static Set<LessonSimpleDTO> toSimpleDTOSet(Set<Lesson> lessons) {
        return lessons.stream()
            .map(LessonSimpleMapperDTO::toSimpleDTO)
            .collect(Collectors.toSet());
    }
}
