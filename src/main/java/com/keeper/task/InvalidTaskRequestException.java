package com.keeper.task;

/**
 * Indicates a missing or invalid field in a task request.
 */
public class InvalidTaskRequestException extends RuntimeException {
    /**
     * Creates an invalid-request exception.
     *
     * @param message explanation suitable for the API error response
     */
    public InvalidTaskRequestException(String message) {
        super(message);
    }
}
