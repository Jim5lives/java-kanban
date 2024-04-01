package com.yandex.app.service;

import com.yandex.app.model.Progress;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistoryManager();
    }

    @Test
    void add_shouldAddElementToHistory() {
        Task newTask = new Task("Задача 1", "Описание 1");

        historyManager.add(newTask);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newTask, history.getFirst());
    }

    @Test
    void add_shouldLinkInCorrectOrder() {
        Task task1 = new Task("Задача 2", "Описание 2", 0, Progress.NEW);
        Task task2 = new Task("Задача 3", "Описание 3", 1, Progress.NEW);
        Task task3 = new Task("Задача 4", "Описание 4", 2, Progress.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size());
        Assertions.assertEquals(task1, history.getFirst());
        Assertions.assertEquals(task2, history.get(1));
        Assertions.assertEquals(task3, history.get(2));
    }

    @Test
    void remove_shouldRemoveFromHistory() {
        Task task1 = new Task("Задача 5", "Описание 5", 0, Progress.NEW);
        Task task2 = new Task("Задача 6", "Описание 6", 1, Progress.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(0);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task2, history.getFirst());
    }

    @Test
    void add_shouldDeleteExistingTaskFromHistoryAndPutItInTheEnd() {
        Task task1 = new Task("Задача 7", "Описание 7", 0, Progress.NEW);
        Task task2 = new Task("Задача 8", "Описание 8", 1, Progress.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.getLast());
        Assertions.assertEquals(task2, history.getFirst());
    }

    @Test
    void add_shouldRewriteSameAddedTask() {
        Task task1 = new Task("Задача 9", "Описание 9", 0, Progress.NEW);

        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task1, history.getFirst());
    }
}