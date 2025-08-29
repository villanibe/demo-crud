package com.democrud.config.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response structure for API errors.
 * 
 * This class provides a consistent format for all error responses
 * across the application, following REST API best practices.
 */
@Data
@Builder
@Schema(description = "Standard error response format")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type/category", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "Human-readable error message", example = "Validation failed for the request")
    private String message;

    @Schema(description = "Detailed error description", example = "The title field cannot be empty")
    private String details;

    @Schema(description = "API path where the error occurred", example = "/api/articles")
    private String path;

    @Schema(description = "Timestamp when the error occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    @Schema(description = "List of validation errors", nullable = true)
    private List<ValidationError> validationErrors;

    @Schema(description = "Unique error tracking ID for debugging", example = "ERR-2025-001")
    private String errorId;

    /**
     * Individual validation error details.
     */
    @Data
    @Builder
    @Schema(description = "Individual field validation error")
    public static class ValidationError {
        
        @Schema(description = "Field name that failed validation", example = "title")
        private String field;
        
        @Schema(description = "Value that was rejected", example = "")
        private Object rejectedValue;
        
        @Schema(description = "Validation error message", example = "must not be blank")
        private String message;
    }
}
