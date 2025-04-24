package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.riccardo.giangiulio.gestionescuola.dto.simple.SchoolClassSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;

public class SchoolClassSimpleMapperDTO {
    
    public static SchoolClassSimpleDTO toSimpleDTO(SchoolClass schoolClass) {
        return new SchoolClassSimpleDTO(schoolClass.getId(), schoolClass.getName(), schoolClass.getMaxStudents());
    }

    public static Set<SchoolClassSimpleDTO> toSimpleDTOSet(Set<SchoolClass> schoolClasses) {
        return schoolClasses.stream()
            .map(SchoolClassSimpleMapperDTO::toSimpleDTO)
            .collect(Collectors.toSet());
    }
}
