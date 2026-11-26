package com.keeper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class TaskApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    @Test
    @Order(1)
    void createsTrimmedTaskAndListsItLast() throws Exception {
        int previousTaskCount = getTasks().size();

        Task created = createTask("\u2003  integration-create \u00a0");
        JsonNode tasks = getTasks();

        assertEquals("integration-create", created.title());
        assertEquals(created.id(), tasks.get(tasks.size() - 1).get("id").asLong());
        assertEquals(previousTaskCount + 1, tasks.size());
    }

    @Test
    @Order(2)
    void completesAndUncompletesTaskWithoutChangingItsOrder() throws Exception {
        Task first = createTask("integration-complete-first");
        Task second = createTask("integration-complete-second");

        mockMvc.perform(patch("/api/tasks/{id}", first.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));

        mockMvc.perform(patch("/api/tasks/{id}", first.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(false));

        JsonNode tasks = getTasks();
        assertTrue(indexOf(tasks, first.id()) < indexOf(tasks, second.id()));
    }

    @Test
    @Order(3)
    void rejectsEmptyWhitespaceAndOverlongTitles() throws Exception {
        assertInvalidTitle("\t \u2003");
        assertInvalidTitle("x".repeat(101));
    }

    @Test
    @Order(4)
    void deletesTaskAndReturnsNotFoundWhenDeletedAgain() throws Exception {
        Task task = createTask("integration-delete");

        mockMvc.perform(delete("/api/tasks/{id}", task.id()))
                .andExpect(status().isNoContent());
        assertFalse(containsTask(getTasks(), task.id()));

        mockMvc.perform(delete("/api/tasks/{id}", task.id()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TASK_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("Task " + task.id() + " was not found."));
    }

    @Test
    @Order(5)
    void returnsNotFoundWhenCompletingUnknownTask() throws Exception {
        mockMvc.perform(patch("/api/tasks/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TASK_NOT_FOUND"));
    }

    @Test
    @Order(6)
    void servesFrontendWithFilterControlsAndMatchingApiRoutes() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        String page = mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String script = mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(page.contains("id=\"task-form\""));
        assertTrue(page.contains("id=\"task-list\""));
        assertTrue(page.contains("data-filter=\"all\""));
        assertTrue(page.contains("data-filter=\"active\""));
        assertTrue(page.contains("data-filter=\"done\""));
        assertTrue(script.contains("fetch(path, options)"));
        assertTrue(script.contains("\"/api/tasks\""));
        assertTrue(script.contains("`/api/tasks/${id}`"));
        assertFalse(script.toLowerCase().contains("todo"));
        assertFalse(script.toLowerCase().contains("placeholder"));
    }

    @Test
    @Order(7)
    void returnsOneThousandStoredTasksWithinHalfSecond() throws Exception {
        int existingTasks = taskService.listTasks().size();
        for (int index = existingTasks; index < 1000; index++) {
            taskService.createTask("timing-task-" + index);
        }

        long start = System.nanoTime();
        MvcResult result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        JsonNode tasks = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1000, tasks.size());
        assertTrue(elapsedMillis < 500,
                "Expected 1,000-task API response under 500 ms, actual: " + elapsedMillis + " ms");
    }

    private Task createTask(String title) throws Exception {
        String request = objectMapper.writeValueAsString(new TaskController.CreateTaskRequest(title));
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);
    }

    private void assertInvalidTitle(String title) throws Exception {
        String request = objectMapper.writeValueAsString(new TaskController.CreateTaskRequest(title));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_TITLE"))
                .andExpect(jsonPath("$.error.message").isNotEmpty());
    }

    private JsonNode getTasks() throws Exception {
        String body = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body);
    }

    private int indexOf(JsonNode tasks, long id) {
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).get("id").asLong() == id) {
                return index;
            }
        }
        return -1;
    }

    private boolean containsTask(JsonNode tasks, long id) {
        return indexOf(tasks, id) >= 0;
    }
}
