package com.democrud.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Demo CRUD API.
 * 
 * This class handles all exceptions thrown by controllers and provides
 * consistent error responses following REST API best practices.
 * 
 * Features:
 * - Structured error responses
 * - Proper HTTP status codes
 * - Detailed logging for debugging
 * - Swagger documentation integration
 * - Unique error tracking IDs
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles ResponseStatusException thrown by service layer.
     * This is the main exception type used in the current codebase.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        
        log.warn("ResponseStatusException [{}]: {} - Path: {}", 
                errorId, ex.getReason(), getPath(request));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(mapStatusToErrorType(status).getDescription())
                .message(ex.getReason())
                .details("The requested operation could not be completed")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles custom ArticleException for business logic errors.
     */
    @ExceptionHandler(ArticleException.class)
    public ResponseEntity<ErrorResponse> handleArticleException(
            ArticleException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.warn("ArticleException [{}]: {} - Path: {}", 
                errorId, ex.getMessage(), getPath(request));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ex.getStatus().value())
                .error(ex.getErrorType().getDescription())
                .message(ex.getMessage())
                .details("Article operation failed due to business logic constraints")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    /**
     * Handles validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.warn("Validation error [{}]: {} validation errors - Path: {}", 
                errorId, ex.getBindingResult().getErrorCount(), getPath(request));

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorType.VALIDATION_ERROR.getDescription())
                .message("Validation failed for one or more fields")
                .details("Please check the provided data and try again")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles binding exceptions (form validation errors).
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.warn("Bind exception [{}]: {} binding errors - Path: {}", 
                errorId, ex.getBindingResult().getErrorCount(), getPath(request));

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorType.VALIDATION_ERROR.getDescription())
                .message("Request binding failed")
                .details("Invalid request format or missing required fields")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles malformed JSON requests.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.warn("Malformed request [{}]: {} - Path: {}", 
                errorId, ex.getMessage(), getPath(request));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorType.BAD_REQUEST.getDescription())
                .message("Malformed JSON request")
                .details("The request body contains invalid JSON or missing required fields")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles method argument type mismatch (e.g., string where number expected).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.warn("Argument type mismatch [{}]: Parameter '{}' - Path: {}", 
                errorId, ex.getName(), getPath(request));

        String requiredType = "unknown";
        Class<?> requiredClass = ex.getRequiredType();
        if (requiredClass != null) {
            requiredType = requiredClass.getSimpleName();
        }
        
        String details = String.format("Parameter '%s' should be of type %s but received: %s", 
                ex.getName(), 
                requiredType,
                ex.getValue() != null ? ex.getValue() : "null");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ErrorType.BAD_REQUEST.getDescription())
                .message("Invalid parameter type")
                .details(details)
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles unsupported HTTP methods.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.warn("Method not supported [{}]: {} - Path: {}", 
                errorId, ex.getMethod(), getPath(request));

        String details = String.format("Method '%s' is not supported for this endpoint. Supported methods: %s", 
                ex.getMethod(), 
                ex.getSupportedHttpMethods());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(ErrorType.BAD_REQUEST.getDescription())
                .message("HTTP method not supported")
                .details(details)
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handles 404 Not Found errors when no handler is found.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.warn("No handler found [{}]: {} {} - Path: {}", 
                errorId, ex.getHttpMethod(), ex.getRequestURL(), getPath(request));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(ErrorType.RESOURCE_NOT_FOUND.getDescription())
                .message("Endpoint not found")
                .details(String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()))
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles database access exceptions.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.error("Database error [{}]: {} - Path: {}", 
                errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ErrorType.DATABASE_ERROR.getDescription())
                .message("Database operation failed")
                .details("An error occurred while accessing the database. Please try again later.")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        String errorId = generateErrorId();
        
        log.error("Unexpected error [{}]: {} - Path: {}", 
                errorId, ex.getMessage(), getPath(request), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ErrorType.INTERNAL_SERVER_ERROR.getDescription())
                .message("An unexpected error occurred")
                .details("Please contact support if this problem persists")
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .errorId(errorId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maps FieldError to ValidationError DTO.
     */
    private ErrorResponse.ValidationError mapFieldError(FieldError fieldError) {
        return ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    /**
     * Maps HTTP status to appropriate error type.
     */
    private ErrorType mapStatusToErrorType(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> ErrorType.BAD_REQUEST;
            case NOT_FOUND -> ErrorType.RESOURCE_NOT_FOUND;
            case INTERNAL_SERVER_ERROR -> ErrorType.INTERNAL_SERVER_ERROR;
            default -> ErrorType.INTERNAL_SERVER_ERROR;
        };
    }

    /**
     * Extracts the request path from WebRequest.
     */
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Generates a unique error ID for tracking purposes.
     */
    private String generateErrorId() {
        return "ERR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
