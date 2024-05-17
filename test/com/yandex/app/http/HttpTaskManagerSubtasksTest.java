package com.yandex.app.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Progress;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

class HttpTaskManagerSubtasksTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistoryManager());
    HttpTaskServer httpTaskServer;
    Gson gson = HttpTaskServer.getGson();


    @BeforeEach
    public void setUp() throws IOException {
        httpTaskServer = new HttpTaskServer(taskManager);
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        HttpTaskServer.stop(0);
    }

    @Test
    void postSubtasks_shouldAddSubtaskWhenNoIdProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        SubTask subtask = new SubTask("Subtask 1", "Description 1", 0,
                LocalDateTime.of(2024, 5, 15, 0, 0), Duration.ofMinutes(15));
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertEquals(201, response.statusCode());
        assertNotNull(subtasks, "Подадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", subtasks.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    void updateSubtask_shouldUpdateSubtaskWhenIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Description 2");
        SubTask subtask = new SubTask("Subtask 2", "Description 2", 0,
                LocalDateTime.of(2024, 5, 15, 0, 15), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);
        SubTask subtaskUpdated = new SubTask("Subtask Updated", "Description Updated", 1,
                Progress.NEW, 0, LocalDateTime.of(2024, 5, 15, 0, 15),
                Duration.ofMinutes(15));
        String subtaskJson = gson.toJson(subtaskUpdated);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertEquals(201, response.statusCode());
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals("Subtask Updated", subtasks.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    void getSubtasks_shouldReturnAllSubtasksWhenNoIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Description 2");
        SubTask subtask = new SubTask("Subtask 3", "Description 3", 0,
                LocalDateTime.of(2024, 5, 15, 0, 30), Duration.ofMinutes(15));
        SubTask subtask2 = new SubTask("Subtask 4", "Description 4", 0,
                LocalDateTime.of(2024, 5, 15, 0, 45), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasks = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {}.getType());
        assertEquals(200, response.statusCode());
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Некорректное количество подзадач");
    }

    @Test
    void getSubtask_shouldReturnExactSubtaskWhenIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 3", "Description 3");
        SubTask subtask = new SubTask("Subtask 5", "Description 5", 0,
                LocalDateTime.of(2024, 5, 15, 1, 0), Duration.ofMinutes(15));
        SubTask subtask2 = new SubTask("Subtask 6", "Description 6", 0,
                LocalDateTime.of(2024, 5, 15, 1, 15), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask returnedSubtask = gson.fromJson(response.body(), SubTask.class);
        assertEquals(200, response.statusCode());
        assertNotNull(returnedSubtask, "Подадача не возвращаются");
        assertEquals(subtask, returnedSubtask);
    }

    @Test
    void deleteSubtask_shouldDeleteExactSubtaskWhenIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 4", "Description 4");
        SubTask subtask = new SubTask("Subtask 7", "Description 7", 0,
                LocalDateTime.of(2024, 5, 15, 1, 30), Duration.ofMinutes(15));
        SubTask subtask2 = new SubTask("Subtask 8", "Description 8", 0,
                LocalDateTime.of(2024, 5, 15, 1, 45), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertEquals(200, response.statusCode());
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals(subtask, subtasks.getFirst(), "Удалена некорректная подзадача");
    }

    @Test
    void deleteSubtasks_shouldDeleteAllSubtasksWhenNoIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 5", "Description 5");
        SubTask subtask = new SubTask("Subtask 9", "Description 9", 0,
                LocalDateTime.of(2024, 5, 15, 2, 0), Duration.ofMinutes(15));
        SubTask subtask2 = new SubTask("Subtask 10", "Description 10", 0,
                LocalDateTime.of(2024, 5, 15, 2, 15), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, subtasks.size(), "Некорректное количество подзадач");
    }
}
