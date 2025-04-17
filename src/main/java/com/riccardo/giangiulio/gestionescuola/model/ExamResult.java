package com.riccardo.giangiulio.gestionescuola.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The score cannot be null")
    private Double score;

    @NotBlank(message = "The notes cannot be empty")
    private String notes;

    @Past(message = "The date cannot be in the future")
    @NotNull(message = "The date cannot be null")
    private LocalDateTime date;

    @NotNull(message = "The exam cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private Exam exam;

    @NotNull(message = "The student cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    private User student;

    public ExamResult(Double score, String notes, LocalDateTime date, Exam exam, User student) {
        this.score = score;
        this.notes = notes;
        this.date = date;
        this.exam = exam;
        this.student = student;
    }
}

