"use strict";

const taskForm = document.querySelector("#task-form");
const taskTitleInput = document.querySelector("#task-title");
const errorMessage = document.querySelector("#error-message");
const taskList = document.querySelector("#task-list");
const emptyMessage = document.querySelector("#empty-message");
const filterButtons = document.querySelectorAll(".filter-button");

let tasks = [];
let selectedFilter = "all";

async function request(path, options) {
    const response = await fetch(path, options);
    if (response.status === 204) {
        return null;
    }

    let body;
    try {
        body = await response.json();
    } catch (error) {
        const message = response.ok
            ? "The server returned an invalid response."
            : `The request failed with status ${response.status}.`;
        throw new Error(message);
    }
    if (!response.ok) {
        const message = body && typeof body === "object" && typeof body.error?.message === "string"
            ? body.error.message
            : "The request could not be completed.";
        throw new Error(message);
    }
    return body;
}

function isTask(value) {
    return value !== null
        && typeof value === "object"
        && Number.isSafeInteger(value.id)
        && value.id > 0
        && typeof value.title === "string"
        && typeof value.done === "boolean";
}

function showError(message) {
    errorMessage.textContent = message;
}

function visibleTasks() {
    if (selectedFilter === "active") {
        return tasks.filter((task) => !task.done);
    }
    if (selectedFilter === "done") {
        return tasks.filter((task) => task.done);
    }
    return tasks;
}

function renderTasks() {
    taskList.replaceChildren();

    const displayedTasks = visibleTasks();
    for (const task of displayedTasks) {
        const item = document.createElement("li");
        item.className = "task-item";

        const toggleLabel = document.createElement("label");
        toggleLabel.className = "task-toggle";

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.checked = task.done;
        checkbox.setAttribute("aria-label", `Toggle completion: ${task.title}`);
        checkbox.addEventListener("change", () => updateTask(task.id, checkbox.checked));

        const title = document.createElement("span");
        title.className = "task-title";
        title.classList.toggle("completed", task.done);
        title.textContent = task.title;

        toggleLabel.append(checkbox, title);

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.className = "delete-button";
        deleteButton.textContent = "Delete";
        deleteButton.setAttribute("aria-label", `Delete task: ${task.title}`);
        deleteButton.addEventListener("click", () => deleteTask(task.id));

        item.append(toggleLabel, deleteButton);
        taskList.append(item);
    }

    emptyMessage.textContent = tasks.length === 0
        ? "No tasks yet."
        : "No tasks match this filter.";
    emptyMessage.hidden = displayedTasks.length > 0;
}

function selectFilter(filter) {
    selectedFilter = filter;
    for (const button of filterButtons) {
        button.setAttribute("aria-pressed", String(button.dataset.filter === filter));
    }
    renderTasks();
}

async function loadTasks() {
    try {
        const responseTasks = await request("/api/tasks");
        if (!Array.isArray(responseTasks) || !responseTasks.every(isTask)) {
            throw new Error("The server returned an invalid task list.");
        }
        tasks = responseTasks;
        renderTasks();
    } catch (error) {
        showError(error.message);
    }
}

async function addTask(title) {
    const task = await request("/api/tasks", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title })
    });
    if (!isTask(task)) {
        throw new Error("The server returned an invalid task.");
    }
    tasks.push(task);
    renderTasks();
}

async function updateTask(id, done) {
    try {
        const updatedTask = await request(`/api/tasks/${id}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ done })
        });
        if (!isTask(updatedTask) || updatedTask.id !== id) {
            throw new Error("The server returned an invalid task.");
        }
        tasks = tasks.map((task) => task.id === id ? updatedTask : task);
        renderTasks();
        showError("");
    } catch (error) {
        showError(error.message);
        renderTasks();
    }
}

async function deleteTask(id) {
    try {
        await request(`/api/tasks/${id}`, { method: "DELETE" });
        tasks = tasks.filter((task) => task.id !== id);
        renderTasks();
        showError("");
    } catch (error) {
        showError(error.message);
    }
}

taskForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    showError("");
    try {
        await addTask(taskTitleInput.value);
        taskTitleInput.value = "";
        taskTitleInput.focus();
    } catch (error) {
        showError(error.message);
    }
});

for (const button of filterButtons) {
    button.addEventListener("click", () => selectFilter(button.dataset.filter));
}

loadTasks();
