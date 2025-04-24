package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.riccardo.giangiulio.gestionescuola.dto.simple.ExamSimpleDTO;
import com.riccardo.giangiulio.gestionescuola.model.Exam;

public class ExamSimpleMapperDTO {

    public static ExamSimpleDTO toSimpleDTO(Exam exam) {
        return new ExamSimpleDTO(exam.getId(), exam.getTitle(), exam.getDescription(), exam.getMaxScore(), exam.getPassingScore());
    }

    public static Set<ExamSimpleDTO> toSimpleDTOSet(Set<Exam> exams) {
        return exams.stream()
            .map(ExamSimpleMapperDTO::toSimpleDTO)
            .collect(Collectors.toSet());
    }
}
