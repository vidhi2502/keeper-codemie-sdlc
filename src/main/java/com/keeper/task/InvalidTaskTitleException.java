package com.keeper.task;

/**
 * Indicates a missing, blank, or overlong task title.
 */
public class InvalidTaskTitleException extends RuntimeException {
    /**
     * Creates an invalid-title exception.
     *
     * @param message explanation suitable for the API error response
     */
    public InvalidTaskTitleException(String message) {
        super(message);
    }
}
