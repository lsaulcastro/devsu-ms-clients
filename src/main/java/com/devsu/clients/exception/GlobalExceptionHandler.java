package com.devsu.clients.exception;

import com.devsu.clients.domain.exception.BusinessRuleViolationException;
import com.devsu.clients.domain.exception.EntityNotFoundException;
import com.devsu.clients.domain.exception.InvalidDataException;
import com.devsu.clients.dto.common.ApiError;
import com.devsu.clients.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidData(InvalidDataException ex,
                                                               HttpServletRequest request) {
        log.warn("Invalid data at {}: {}", request.getRequestURI(), ex.getMessage());
        ApiError error = ApiError.of("INVALID_DATA", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), error));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex,
                                                                  HttpServletRequest request) {
        log.warn("Resource not found at {}: type={}, id={}",
                request.getRequestURI(), ex.getEntityType(), ex.getEntityId());
        Map<String, Object> details = Map.of(
                "entityType", ex.getEntityType(),
                "entityId", ex.getEntityId()
        );
        ApiError error = ApiError.of("ENTITY_NOT_FOUND", ex.getMessage(), details);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), request.getRequestURI(), error));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRuleViolation(BusinessRuleViolationException ex,
                                                                         HttpServletRequest request) {
        log.warn("Business rule violation at {}: rule={}, message={}",
                request.getRequestURI(), ex.getRuleCode(), ex.getMessage());
        Map<String, Object> details = Map.of("ruleCode", ex.getRuleCode());
        ApiError error = ApiError.of("BUSINESS_RULE_VIOLATION", ex.getMessage(), details);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), request.getRequestURI(), error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleBeanValidation(MethodArgumentNotValidException ex,
                                                                  HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed at {}: {}", request.getRequestURI(), fieldErrors);
        Map<String, Object> details = Map.of("fields", fieldErrors);
        ApiError error = ApiError.of("VALIDATION_ERROR", "Request payload validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        ApiError error = ApiError.of("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(), error));
    }
}