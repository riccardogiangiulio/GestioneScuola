package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.CourseDTO;
import com.riccardo.giangiulio.gestionescuola.model.Course;

public class CourseMapperDTO {

    public static CourseDTO toDTO(Course course) {
        return new CourseDTO(course.getId(), course.getTitle(), course.getDescription(), course.getDuration(), course.getPrice(), SubjectSimpleMapperDTO.toSimpleDTOSet(course.getSubjects()), ExamSimpleMapperDTO.toSimpleDTOSet(course.getExams()));
    }
}
