package com.edu.udistrital.backend.carrera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Clase encargada de el manejo de errores del aplicativo, intercepta los errores y
 * devuelve los http correspondientes
 */
@RestControllerAdvice
public class ManejoErrores {

    /**
     * Captura excepciones de tipo RuntimeException
     * @param e excepción capturada
     * @return ResponseEntity con código 404 y mensaje del error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Captura excepciones de argumentos inválidos o campos vacíos
     * @param e excepción capturada
     * @return ResponseEntity con código 400 y mensaje del error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Captura cualquier otra excepción no controlada
     * @param e excepción capturada
     * @return ResponseEntity con código 500 y mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
    }
}
