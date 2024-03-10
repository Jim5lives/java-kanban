import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefaultTaskManager();
    }

    @Test
    void add_shouldAddElementToHistory() {
        Task newTask = new Task("Задача 1", "Описание 1");
        taskManager.addTask(newTask);
        List<Task> history = taskManager.getHistory();

        history.add(newTask);

        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newTask, history.getFirst());
    }

    @Test
    void getHistory_ShouldReturnHistoryList() {
        Task newTask1 = new Task("Задача 2", "Описание 2");
        Task newTask2 = new Task("Задача 3", "Описание 3");
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);
        taskManager.getTask(newTask1.getId());
        taskManager.getTask(newTask2.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(history.size(), 2);
    }
}