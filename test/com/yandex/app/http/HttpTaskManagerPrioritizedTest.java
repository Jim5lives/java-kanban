package com.yandex.app.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
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

class HttpTaskManagerPrioritizedTest {
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
    void getPrioritized_shouldReturnTasksInCorrectOrder() throws IOException, InterruptedException {
        Task taskFirstPriority = new Task("Task 1", "Description 1",
                LocalDateTime.of(2024, 5, 15, 0, 0), Duration.ofMinutes(15));
        Epic epic = new Epic("Epic 1", "Description 1");
        SubTask subtaskLastPriority = new SubTask("Subtask 1", "Description 1", 1,
                LocalDateTime.of(2024, 5, 15, 0, 30), Duration.ofMinutes(15));
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", 1,
                LocalDateTime.of(2024, 5, 15, 0, 15), Duration.ofMinutes(15));
        taskManager.addTask(taskFirstPriority);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtaskLastPriority);
        taskManager.addSubTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> prioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(200, response.statusCode());
        assertNotNull(prioritized, "Задачи по приоритету не возвращаются");
        assertEquals(3, prioritized.size(), "Некорректное количество задач в списке приоритетности");
        assertEquals(taskFirstPriority, prioritized.getFirst(), "Неверная задача с высшим приоритетом");
        assertEquals(subtaskLastPriority, prioritized.getLast(), "Неверная задача с наименьшим приоритетом");
    }
}
