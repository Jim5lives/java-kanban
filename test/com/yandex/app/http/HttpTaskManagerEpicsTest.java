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

class HttpTaskManagerEpicsTest {
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
    void POST_EPICS_shouldAddEpicWhenNoIdProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description 1", 0, Progress.NEW);
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(201, response.statusCode());
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epics.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    void UPDATE_EPIC_shouldUpdateEpicWhenIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Description 2");
        Epic epicUpdated = new Epic("Epic Updated", "Description Updated", 0, Progress.NEW);
        taskManager.addEpic(epic);
        String epicJson = gson.toJson(epicUpdated);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(201, response.statusCode());
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals("Epic Updated", epics.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    void GET_EPICS_shouldReturnAllEpicsWhenNoIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 3", "Description 3");
        Epic epic2 = new Epic("Epic 4", "Description 4");
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {}.getType());
        assertEquals(200, response.statusCode());
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(2, epics.size(), "Некорректное количество эпиков");
    }

    @Test
    void GET_EPIC_shouldReturnExactEpicWhenIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 5", "Description 5");
        Epic epic2 = new Epic("Epic 6", "Description 6");
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertNotNull(returnedEpic, "Эпик не возвращаются");
        assertEquals(epic2, returnedEpic);
    }

    @Test
    void DELETE_Epic_shouldDeleteExactEpicWhenIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 7", "Description 7");
        Epic epic2 = new Epic("Epic 8", "Description 8");
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(200, response.statusCode());
        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals(epic2, epics.getFirst(), "Удален неверный эпик");
    }

    @Test
    void DELETE_EPICS_shouldDeleteAllEpicsWhenNoIdIsProvided() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 9", "Description 9");
        Epic epic2 = new Epic("Epic 10", "Description 10");
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(200, response.statusCode());
        assertEquals(0, epics.size(), "Некорректное количество эпиков");
    }

    @Test
    void GET_SUBTASKS_FROM_EPIC() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 11", "Description 11");
        SubTask subtask = new SubTask("Subtask 1", "Description 1", 0,
                LocalDateTime.of(2024, 5, 15, 0, 0), Duration.ofMinutes(15));
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", 0,
                LocalDateTime.of(2024, 5, 15, 0, 15), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask);
        taskManager.addSubTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasksFromEpic = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {}.getType());
        assertEquals(200, response.statusCode());
        assertEquals(2, subtasksFromEpic.size(), "Некорректное количество подзадач в эпике");
    }
}
    

