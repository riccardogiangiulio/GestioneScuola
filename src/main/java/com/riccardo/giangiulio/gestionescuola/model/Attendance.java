package com.riccardo.giangiulio.gestionescuola.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "attendances")
@NoArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The presence cannot be null")
    private Boolean present;

    @NotNull(message = "The entry time cannot be null")
    private LocalDateTime entryTime;

    @NotNull(message = "The exit time cannot be null")
    private LocalDateTime exitTime;

    @NotNull(message = "The student cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;
    
    @NotNull(message = "The lesson cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    public Attendance(Boolean present, LocalDateTime entryTime, LocalDateTime exitTime, User student, Lesson lesson) {
        this.present = true;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.student = student;
        this.lesson = lesson;
    }
}
