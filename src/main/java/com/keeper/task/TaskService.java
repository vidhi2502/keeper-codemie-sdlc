package com.keeper.task;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Validates task input and coordinates task operations.
 */
@Service
public class TaskService {
    private static final int MAX_TITLE_CODE_POINTS = 100;

    private final TaskRepository repository;

    /**
     * Creates the task service.
     *
     * @param repository thread-safe task store
     */
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a task after trimming and validating its title.
     *
     * @param title raw title
     * @return newly created task
     * @throws InvalidTaskTitleException if title is blank or over 100 code points
     */
    public Task createTask(String title) {
        String normalized = trimUnicodeWhitespace(title);
        if (normalized.isEmpty()) {
            throw new InvalidTaskTitleException("Title must not be empty.");
        }
        if (normalized.codePointCount(0, normalized.length()) > MAX_TITLE_CODE_POINTS) {
            throw new InvalidTaskTitleException("Title must be 100 characters or fewer.");
        }
        return repository.add(normalized);
    }

    /**
     * Lists tasks in creation order.
     *
     * @return immutable task snapshot
     */
    public List<Task> listTasks() {
        return repository.findAll();
    }

    /**
     * Sets whether a task is complete.
     *
     * @param id task identifier
     * @param done desired completion state
     * @return updated task
     * @throws TaskNotFoundException if the task does not exist
     */
    public Task setDone(long id, boolean done) {
        return repository.updateDone(id, done).orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Deletes a task.
     *
     * @param id task identifier
     * @throws TaskNotFoundException if the task does not exist
     */
    public void deleteTask(long id) {
        if (!repository.delete(id)) {
            throw new TaskNotFoundException(id);
        }
    }

    private String trimUnicodeWhitespace(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        int start = 0;
        int end = value.length();
        while (start < end) {
            int codePoint = value.codePointAt(start);
            if (!isWhitespace(codePoint)) {
                break;
            }
            start += Character.charCount(codePoint);
        }
        while (start < end) {
            int codePoint = value.codePointBefore(end);
            if (!isWhitespace(codePoint)) {
                break;
            }
            end -= Character.charCount(codePoint);
        }
        return value.substring(start, end);
    }

    private boolean isWhitespace(int codePoint) {
        return Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint);
    }
}
