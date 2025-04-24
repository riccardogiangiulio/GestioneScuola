package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.riccardo.giangiulio.gestionescuola.dto.simple.CourseSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.model.Course;

public class CourseSimpleMapperDTO {
    public static CourseSimpleDTO toSimpleDTO(Course course) {
        return new CourseSimpleDTO(course.getId(), course.getTitle(), course.getDescription(), course.getDuration(), course.getPrice());
    }

    public static Set<CourseSimpleDTO> toSimpleDTOSet(Set<Course> courses) {
        return courses.stream()
            .map(CourseSimpleMapperDTO::toSimpleDTO)
            .collect(Collectors.toSet());
    }
}
