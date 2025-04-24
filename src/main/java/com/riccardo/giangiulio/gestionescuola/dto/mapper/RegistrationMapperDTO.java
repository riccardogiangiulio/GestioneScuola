package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.RegistrationDTO;
import com.riccardo.giangiulio.gestionescuola.model.Registration;

public class RegistrationMapperDTO {

    public static RegistrationDTO toDTO(Registration registration) {
        return new RegistrationDTO(registration.getId(), registration.getRegistrationDate(), registration.getStatus(), UserMapperDTO.toDTO(registration.getStudent()), CourseSimpleMapperDTO.toSimpleDTO(registration.getCourse()), SchoolClassSimpleMapperDTO.toSimpleDTO(registration.getSchoolClass()));
    }
}
