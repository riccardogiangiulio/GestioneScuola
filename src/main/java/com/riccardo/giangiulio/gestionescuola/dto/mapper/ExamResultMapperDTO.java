package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import com.riccardo.giangiulio.gestionescuola.dto.model.ExamResultDTO;
import com.riccardo.giangiulio.gestionescuola.model.ExamResult;

public class ExamResultMapperDTO {

    public static ExamResultDTO toDTO(ExamResult examResult) {
        return new ExamResultDTO(examResult.getId(), examResult.getScore(), examResult.getNotes(), examResult.getDate(), ExamSimpleMapperDTO.toSimpleDTO(examResult.getExam()), UserMapperDTO.toDTO(examResult.getStudent()));
    }
}
