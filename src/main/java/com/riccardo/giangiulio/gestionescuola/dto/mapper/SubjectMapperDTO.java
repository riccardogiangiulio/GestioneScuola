package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.SubjectDTO;
import com.riccardo.giangiulio.gestionescuola.model.Subject;

public class SubjectMapperDTO {

    public static SubjectDTO toDTO(Subject subject) {
        return new SubjectDTO(subject.getId(), subject.getName(), subject.getDescription(), UserMapperDTO.toDTO(subject.getTeacher()), CourseSimpleMapperDTO.toSimpleDTOSet(subject.getCourses()));
    }
}
