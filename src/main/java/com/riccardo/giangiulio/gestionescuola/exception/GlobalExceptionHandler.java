package com.riccardo.giangiulio.gestionescuola.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ResourceNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.BusinessValidationException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.ClassroomCapacityExceededException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.DuplicateRegistrationException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.EmailAlreadyExistException;
import com.riccardo.giangiulio.gestionescuola.exception.ValidationException.SchoolClassFullException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<String> handleBusinessValidation(BusinessValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

     // Gestione specifica per duplicazioni (conflict)
     @ExceptionHandler({
        DuplicateRegistrationException.class,
        EmailAlreadyExistException.class
    })
    public ResponseEntity<String> handleConflict(BusinessValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    // Gestione specifica per risorse piene
    @ExceptionHandler({
        SchoolClassFullException.class,
        ClassroomCapacityExceededException.class
    })
    public ResponseEntity<String> handleResourceFull(BusinessValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    // Gestione generica per eccezioni non gestite
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return new ResponseEntity<>("Internal error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
