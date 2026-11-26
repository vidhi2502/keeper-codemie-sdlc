package com.keeper.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Maps API failures to the documented JSON error envelope.
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Maps invalid task titles to HTTP 400.
     *
     * @param exception validation failure
     * @return structured error response
     */
    @ExceptionHandler(InvalidTaskTitleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTitle(InvalidTaskTitleException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_TITLE", exception.getMessage());
    }

    /**
     * Maps unknown task identifiers to HTTP 404.
     *
     * @param exception missing task failure
     * @return structured error response
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND", exception.getMessage());
    }

    /**
     * Maps missing or invalid task fields to HTTP 400.
     *
     * @param exception invalid request failure
     * @return structured error response
     */
    @ExceptionHandler(InvalidTaskRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTaskRequest(
            InvalidTaskRequestException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage());
    }

    /**
     * Maps malformed request bodies and path IDs to HTTP 400.
     *
     * @param exception request parsing failure
     * @return structured error response
     */
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "The request is invalid.");
    }

    /**
     * Maps unsupported methods to HTTP 405.
     *
     * @param exception unsupported method failure
     * @return structured error response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "The HTTP method is not supported for this route.");
    }

    /**
     * Maps unsupported request content types to HTTP 415.
     *
     * @param exception unsupported content type failure
     * @return structured error response
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception) {
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE",
                "The request content type is not supported.");
    }

    /**
     * Maps unexpected failures without exposing internal details.
     *
     * @param exception unexpected failure
     * @return generic server error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        LOGGER.error("Unexpected task API failure of type {}",
                exception.getClass().getSimpleName());
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected server error occurred.");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(new ApiError(code, message)));
    }

    /**
     * JSON error envelope.
     *
     * @param error error details
     */
    public record ErrorResponse(ApiError error) {
    }

    /**
     * Error code and plain-text message.
     *
     * @param code stable error code
     * @param message human-readable error
     */
    public record ApiError(String code, String message) {
    }
}
