package com.keeper.task;

/**
 * Indicates that a task identifier does not exist in the store.
 */
public class TaskNotFoundException extends RuntimeException {
    /**
     * Creates a task-not-found exception.
     *
     * @param id unknown task identifier
     */
    public TaskNotFoundException(long id) {
        super("Task " + id + " was not found.");
    }
}
