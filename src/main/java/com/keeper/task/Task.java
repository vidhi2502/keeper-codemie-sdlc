package com.keeper.task;

/**
 * An immutable task value.
 *
 * @param id unique in-memory task identifier
 * @param title validated task title
 * @param done whether the task is complete
 */
public record Task(long id, String title, boolean done) {
}
