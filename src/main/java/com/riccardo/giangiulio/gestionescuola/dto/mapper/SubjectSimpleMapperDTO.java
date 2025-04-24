package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.riccardo.giangiulio.gestionescuola.dto.simple.SubjectSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.model.Subject;

public class SubjectSimpleMapperDTO {
    
    public static SubjectSimpleDTO toSimpleDTO(Subject subject) {
        return new SubjectSimpleDTO(subject.getId(), subject.getName(), subject.getDescription());
    }

    public static Set<SubjectSimpleDTO> toSimpleDTOSet(Set<Subject> subjects) {
        return subjects.stream()
            .map(SubjectSimpleMapperDTO::toSimpleDTO)
            .collect(Collectors.toSet());
    }
}
