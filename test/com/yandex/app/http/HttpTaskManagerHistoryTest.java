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

class HttpTaskManagerHistoryTest {
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
    void GET_HISTORY_shouldReturnHistory() throws IOException, InterruptedException {
        Task taskLastInHistory = new Task("Task 1", "Description 1",
                LocalDateTime.of(2024, 5, 15, 0, 0), Duration.ofMinutes(15));
        Epic epicFirstInHistory = new Epic("Epic 1", "Description 1");
        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", 1,
                LocalDateTime.of(2024, 5, 15, 0, 30), Duration.ofMinutes(15));
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", 1,
                LocalDateTime.of(2024, 5, 15, 0, 15), Duration.ofMinutes(15));
        taskManager.addTask(taskLastInHistory);
        taskManager.addEpic(epicFirstInHistory);
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.getEpic(epicFirstInHistory.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(taskLastInHistory.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(200, response.statusCode());
        assertNotNull(history, "История не возвращается");
        assertEquals(4, history.size(), "Некорректное количество задач в истории");
        assertEquals(epicFirstInHistory, history.getFirst(), "Неверная первая задача в истории");
        assertEquals(taskLastInHistory, history.getLast(), "Неверная последняя задача в истории");
    }
}
