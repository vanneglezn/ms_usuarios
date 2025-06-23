package com.ecomarket.ms_usuarios.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de validación (body @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationError(MethodArgumentNotValidException ex) {
        List<ValidationError> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ValidationError(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errores);
    }

    // Errores de validación (query params / path variables)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Parámetro inválido",
                ex.getMessage()
        ));
    }

    // Violación de integridad desde la base de datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // SOLUCIÓN: Almacenar el rootCause en una variable temporal para evitar múltiples llamadas y potencial NPE
        Throwable rootCause = ex.getRootCause();
        String errorMessage = (rootCause != null) ? rootCause.getMessage() : ex.getMessage();

        return ResponseEntity.badRequest().body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Violación de integridad de datos",
                errorMessage
        ));
    }

    // Errores de negocio lanzados manualmente
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Error de argumento",
                ex.getMessage()
        ));
    }

    // Fallback para errores inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno del servidor",
                        ex.getMessage()
                )
        );
    }

    // Estructura de error para errores de validación
    public record ValidationError(String campo, String mensaje) {}

    // Estructura general de error
    public record ErrorResponse(int status, String error, String message) {}
}