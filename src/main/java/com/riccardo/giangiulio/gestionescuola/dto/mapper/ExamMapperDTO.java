package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.ExamDTO;
import com.riccardo.giangiulio.gestionescuola.model.Exam;

public class ExamMapperDTO {

    public static ExamDTO toDTO(Exam exam) {
        return new ExamDTO(exam.getId(), exam.getTitle(), exam.getDescription(), exam.getDate(), exam.getDuration(), exam.getMaxScore(), exam.getPassingScore(), ClassroomMapperDTO.toDTO(exam.getClassroom()), SubjectSimpleMapperDTO.toSimpleDTO(exam.getSubject()), SchoolClassSimpleMapperDTO.toSimpleDTO(exam.getSchoolClass()), UserMapperDTO.toDTO(exam.getTeacher()));
    }
}
