package com.rest.api.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error no manejado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "code", "INTERNAL_ERROR",
                "message", "Error interno del servidor: " + ex.getMessage(),
                "correlationId", MDC.get("correlationId")
            ));
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "code", "FILE_TOO_LARGE",
                "message", "El archivo excede el tamaño máximo permitido",
                "correlationId", MDC.get("correlationId")
            ));
    }
}