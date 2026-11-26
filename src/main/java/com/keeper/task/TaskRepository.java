package com.keeper.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Thread-safe insertion-ordered in-memory task store.
 */
@Repository
public class TaskRepository {
    private final LinkedHashMap<Long, Task> tasks = new LinkedHashMap<>();
    private long nextId = 1;

    /**
     * Adds a task at the end of the creation order.
     *
     * @param title validated title to store
     * @return the newly created task
     */
    public synchronized Task add(String title) {
        Task task = new Task(nextId++, title, false);
        tasks.put(task.id(), task);
        return task;
    }

    /**
     * Returns a detached snapshot in creation order.
     *
     * @return immutable ordered task list
     */
    public synchronized List<Task> findAll() {
        return List.copyOf(new ArrayList<>(tasks.values()));
    }

    /**
     * Replaces the completion value without changing task order.
     *
     * @param id task identifier
     * @param done desired completion state
     * @return updated task, or empty if the identifier is unknown
     */
    public synchronized Optional<Task> updateDone(long id, boolean done) {
        Task current = tasks.get(id);
        if (current == null) {
            return Optional.empty();
        }

        Task updated = new Task(current.id(), current.title(), done);
        tasks.put(id, updated);
        return Optional.of(updated);
    }

    /**
     * Removes a task.
     *
     * @param id task identifier
     * @return true if a task was removed
     */
    public synchronized boolean delete(long id) {
        return tasks.remove(id) != null;
    }
}
