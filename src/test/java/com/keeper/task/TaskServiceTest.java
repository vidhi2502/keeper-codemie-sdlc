package com.keeper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskServiceTest {
    private TaskRepository repository;
    private TaskService service;

    @BeforeEach
    void setUp() {
        repository = new TaskRepository();
        service = new TaskService(repository);
    }

    @Test
    void trimsUnicodeWhitespaceAndCreatesTask() {
        Task created = service.createTask("\u2003  Buy milk \u00a0");

        assertEquals("Buy milk", created.title());
        assertEquals(1, created.id());
        assertFalse(created.done());
    }

    @Test
    void rejectsMissingOrWhitespaceOnlyTitle() {
        assertThrows(InvalidTaskTitleException.class, () -> service.createTask(null));
        assertThrows(InvalidTaskTitleException.class, () -> service.createTask(" \u2003\u00a0 "));
        assertTrue(service.listTasks().isEmpty());
    }

    @Test
    void acceptsOneHundredCodePointsAndRejectsOneHundredOne() {
        String hundredCodePoints = "\uD83D\uDE00".repeat(100);

        assertEquals(100, service.createTask(hundredCodePoints).title().codePointCount(0, 200));
        assertThrows(InvalidTaskTitleException.class,
                () -> service.createTask(hundredCodePoints + "x"));
    }

    @Test
    void preservesCreationOrderWhenCompletionChanges() {
        Task first = service.createTask("first");
        Task second = service.createTask("second");

        service.setDone(first.id(), true);

        assertEquals(List.of(first.id(), second.id()),
                service.listTasks().stream().map(Task::id).toList());
        assertTrue(service.listTasks().get(0).done());
    }

    @Test
    void storesOneThousandTasks() {
        for (int index = 0; index < 1000; index++) {
            service.createTask("task-" + index);
        }

        assertEquals(1000, service.listTasks().size());
    }

    @Test
    void deletesExistingTaskAndReportsMissingTaskIds() {
        Task task = service.createTask("remove");

        service.deleteTask(task.id());

        assertTrue(service.listTasks().isEmpty());
        assertThrows(TaskNotFoundException.class, () -> service.deleteTask(task.id()));
        assertThrows(TaskNotFoundException.class, () -> service.setDone(999, true));
    }

    @Test
    void concurrentCreatesPreserveEveryTaskAndUniqueIds() throws Exception {
        int taskCount = 200;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<Task>> futures = new ArrayList<>();
            for (int index = 0; index < taskCount; index++) {
                int taskIndex = index;
                futures.add(executor.submit(() -> service.createTask("task-" + taskIndex)));
            }

            Set<Long> identifiers = new HashSet<>();
            for (Future<Task> future : futures) {
                identifiers.add(future.get().id());
            }

            assertEquals(taskCount, identifiers.size());
            assertEquals(taskCount, service.listTasks().size());
        } finally {
            executor.shutdownNow();
        }
    }
}
