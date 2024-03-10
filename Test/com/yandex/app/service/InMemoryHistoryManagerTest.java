package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private static final int HISTORY_MAX_SIZE = 10;

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
    void maxSizeOfHistoryShouldBe10 () {
        Task newTask = new Task("Задача 2", "Описание 2");

        for (int i = 0; i < 15; i++) {
            historyManager.add(newTask);
        }

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(HISTORY_MAX_SIZE, history.size());
    }
}