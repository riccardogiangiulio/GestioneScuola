package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.riccardo.giangiulio.gestionescuola.dto.simple.RegistrationSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.model.Registration;

public class RegistrationSimpleMapperDTO {

    public static RegistrationSimpleDTO toSimpleDTO(Registration registration) {
        return new RegistrationSimpleDTO(registration.getId(), registration.getRegistrationDate(), registration.getStatus());
    }

    public static Set<RegistrationSimpleDTO> toSimpleDTOSet(Set<Registration> registrations) {
        return registrations.stream()
            .map(RegistrationSimpleMapperDTO::toSimpleDTO)
            .collect(Collectors.toSet());
    }
}
