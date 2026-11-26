package com.keeper.task;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for task operations.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    /**
     * Creates the controller.
     *
     * @param taskService task operation service
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Lists all tasks in creation order.
     *
     * @return ordered task list
     */
    @GetMapping
    public List<Task> listTasks() {
        return taskService.listTasks();
    }

    /**
     * Creates a task.
     *
     * @param request task title request
     * @return newly created task
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        if (request == null) {
            throw new InvalidTaskRequestException("A task title is required.");
        }
        Task created = taskService.createTask(request.title());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Changes a task's completion state.
     *
     * @param id task identifier
     * @param request completion-state request
     * @return updated task
     */
    @PatchMapping("/{id}")
    public Task updateTask(@PathVariable long id, @RequestBody UpdateTaskRequest request) {
        if (request == null || request.done() == null || !request.done().isBoolean()) {
            throw new InvalidTaskRequestException("The done field must be a boolean.");
        }
        return taskService.setDone(id, request.done().booleanValue());
    }

    /**
     * Deletes a task.
     *
     * @param id task identifier
     * @return empty response with status 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Request body for task creation.
     *
     * @param title submitted task title
     */
    public record CreateTaskRequest(String title) {
    }

    /**
     * Request body for task completion updates.
     *
     * @param done desired completion state
     */
    public record UpdateTaskRequest(JsonNode done) {
    }
}
