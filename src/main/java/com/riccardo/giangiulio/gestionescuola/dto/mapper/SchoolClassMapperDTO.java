package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.SchoolClassDTO;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;

public class SchoolClassMapperDTO {
    
    public static SchoolClassDTO toDTO(SchoolClass schoolClass) {
        return new SchoolClassDTO(schoolClass.getId(), schoolClass.getName(), schoolClass.getMaxStudents(), CourseSimpleMapperDTO.toSimpleDTO(schoolClass.getCourse()), UserMapperDTO.toDTOSet(schoolClass.getTeachers()), RegistrationSimpleMapperDTO.toSimpleDTOSet(schoolClass.getRegistrations()));
    }
}
