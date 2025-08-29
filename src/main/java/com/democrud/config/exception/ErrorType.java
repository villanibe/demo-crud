package com.democrud.config.exception;

/**
 * Enumeration of error types used throughout the application.
 * 
 * This provides a consistent categorization of errors that can occur
 * in the API, making it easier for clients to handle different error scenarios.
 */
public enum ErrorType {
    
    /**
     * Validation errors - when request data doesn't meet validation requirements
     */
    VALIDATION_ERROR("Validation Error"),
    
    /**
     * Resource not found - when requested resource doesn't exist
     */
    RESOURCE_NOT_FOUND("Resource Not Found"),
    
    /**
     * Bad request - when the request is malformed or invalid
     */
    BAD_REQUEST("Bad Request"),
    
    /**
     * Internal server errors - unexpected system errors
     */
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    
    /**
     * Database errors - when database operations fail
     */
    DATABASE_ERROR("Database Error"),
    
    /**
     * Business logic errors - when business rules are violated
     */
    BUSINESS_LOGIC_ERROR("Business Logic Error");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
