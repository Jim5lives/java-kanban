package com.yandex.app.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTasksTest {

    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistoryManager());
    HttpTaskServer httpTaskServer;
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        httpTaskServer = new HttpTaskServer(taskManager);
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop(0);
    }

    @Test
    void POST_TASKS_shouldAddTaskWhenNoIdProvided() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1",
                LocalDateTime.of(2024, 5, 15, 0, 0), Duration.ofMinutes(15));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(201, response.statusCode());
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Task 1", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void UPDATE_TASK_shouldUpdateTaskWhenIdIsProvided() throws IOException, InterruptedException {
        Task task = new Task("Task 2", "Description 2",
                LocalDateTime.of(2024, 5, 15, 0, 15), Duration.ofMinutes(15));
        Task task2 = new Task("Task updated", "Description updated",
                LocalDateTime.of(2024, 5, 15, 0, 30), Duration.ofMinutes(15));
        taskManager.addTask(task);
        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(201, response.statusCode());
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Task updated", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void GET_TASKS_shouldReturnAllTasksWhenNoIdIsProvided() throws IOException, InterruptedException {
        Task task = new Task("Task 3", "Description 3",
                LocalDateTime.of(2024, 5, 15, 0, 45), Duration.ofMinutes(15));
        Task task2 = new Task("Task 4", "Description 4",
                LocalDateTime.of(2024, 5, 15, 1, 0), Duration.ofMinutes(15));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(200, response.statusCode());
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Некорректное количество задач");
    }

    @Test
    void GET_TASK_shouldReturnExactTaskWhenIdIsProvided() throws IOException, InterruptedException {
        Task task = new Task("Task 5", "Description 5",
                LocalDateTime.of(2024, 5, 15, 1, 15), Duration.ofMinutes(15));
        Task task2 = new Task("Task 6", "Description 6",
                LocalDateTime.of(2024, 5, 15, 1, 30), Duration.ofMinutes(15));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertNotNull(returnedTask, "Задача не возвращаются");
        assertEquals(task2, returnedTask);
    }

    @Test
    void DELETE_TASK_shouldDeleteExactTaskWhenIdIsProvided() throws IOException, InterruptedException {
        Task task = new Task("Task 7", "Description 7",
                LocalDateTime.of(2024, 5, 15, 1, 45), Duration.ofMinutes(15));
        Task task2 = new Task("Task 8", "Description 8",
                LocalDateTime.of(2024, 5, 15, 2, 0), Duration.ofMinutes(15));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(200, response.statusCode());
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals(task2, tasks.getFirst(), "Удалена некорректная задача");
    }

    @Test
    void DELETE_TASKS_shouldDeleteAllTasksWhenNoIdIsProvided() throws IOException, InterruptedException {
        Task task = new Task("Task 9", "Description 9",
                LocalDateTime.of(2024, 5, 15, 2, 15), Duration.ofMinutes(15));
        Task task2 = new Task("Task 10", "Description 10",
                LocalDateTime.of(2024, 5, 15, 2, 30), Duration.ofMinutes(15));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasks.size(), "Некорректное количество задач");
    }

}