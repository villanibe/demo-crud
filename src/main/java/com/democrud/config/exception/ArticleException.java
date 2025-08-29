package com.democrud.config.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for article-related business logic errors.
 * 
 * This exception is thrown when article operations violate business rules
 * or when article-specific errors occur that are not covered by generic exceptions.
 */
public class ArticleException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorType errorType;

    /**
     * Constructs an ArticleException with a message and HTTP status.
     */
    public ArticleException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorType = ErrorType.BUSINESS_LOGIC_ERROR;
    }

    /**
     * Constructs an ArticleException with a message, status, and error type.
     */
    public ArticleException(String message, HttpStatus status, ErrorType errorType) {
        super(message);
        this.status = status;
        this.errorType = errorType;
    }

    /**
     * Constructs an ArticleException with a message, cause, and status.
     */
    public ArticleException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
        this.errorType = ErrorType.BUSINESS_LOGIC_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Factory method for creating article not found exceptions.
     */
    public static ArticleException notFound(String articleId) {
        return new ArticleException(
                String.format("Article not found with id: %s", articleId),
                HttpStatus.NOT_FOUND,
                ErrorType.RESOURCE_NOT_FOUND
        );
    }

    /**
     * Factory method for creating article validation exceptions.
     */
    public static ArticleException invalidData(String message) {
        return new ArticleException(
                message,
                HttpStatus.BAD_REQUEST,
                ErrorType.VALIDATION_ERROR
        );
    }

    /**
     * Factory method for creating article already exists exceptions.
     */
    public static ArticleException alreadyExists(String identifier) {
        return new ArticleException(
                String.format("Article already exists with identifier: %s", identifier),
                HttpStatus.CONFLICT,
                ErrorType.BUSINESS_LOGIC_ERROR
        );
    }
}
