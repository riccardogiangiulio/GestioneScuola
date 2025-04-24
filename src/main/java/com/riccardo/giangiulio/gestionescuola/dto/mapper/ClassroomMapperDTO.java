package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.ClassroomDTO;
import com.riccardo.giangiulio.gestionescuola.model.Classroom;

public class ClassroomMapperDTO {

    public static ClassroomDTO toDTO(Classroom classroom) {
        return new ClassroomDTO(classroom.getId(), classroom.getName(), classroom.getCapacity());
    }
}
