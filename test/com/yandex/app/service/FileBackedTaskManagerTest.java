package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    private TaskManager taskManager;
    private static final File MEMORY_TEST_EMPTY_FILE = new File("test/resources/memoryTestEmpty.csv");
    private static final File MEMORY_TEST_LOAD_FILE = new File("test/resources/memoryTestLoad.csv");

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getFileBackedTaskManager();
    }

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getFileBackedTaskManager();
    }

    @Test
    void loadFromFile_shouldReturnEmptyTaskManagerIfFileIsEmpty() {
        taskManager = FileBackedTaskManager.loadFromFile(MEMORY_TEST_EMPTY_FILE);

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void loadFromFile_shouldLoadTasksAndHistoryFromFile() {
        taskManager = FileBackedTaskManager.loadFromFile(MEMORY_TEST_LOAD_FILE);

        List<Task> allTasks = taskManager.getAllTasks();
        List<Epic> allEpics = taskManager.getAllEpics();
        List<SubTask> allSubtasks = taskManager.getAllSubTasks();
        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, allTasks.size());
        Assertions.assertEquals(1, allEpics.size());
        Assertions.assertEquals(1, allSubtasks.size());
        Assertions.assertEquals(2, history.size());
    }

    @Test
    void tasksAndHistoryShouldBeSavedToFileWhenSomethingIsChanged() {
        Task task = new Task("Таск", "Описание таск",
                LocalDateTime.of(2024, 5, 2, 9, 30), Duration.ofMinutes(15));
        Epic epic = new Epic("Эпик", "Описание эпик");
        SubTask subTask = new SubTask("Сабтаск1", "Описание Сабтаск1", 1,
                LocalDateTime.of(2024, 5, 2, 9, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Сабтаск2", "Описание Сабтаск2", 1,
                LocalDateTime.of(2024, 5, 2, 10, 0), Duration.ofMinutes(15));

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);
        taskManager.deleteSubtask(subTask.getId());

        try {
            Path path = Paths.get("src/resources/memory.csv");
            List<String> strings = Files.readAllLines(path);
            String expectedString1 = "0,TASK,Таск,NEW,Описание таск,02.05.2024.09:30,15";
            String expectedString2 = "1,EPIC,Эпик,NEW,Описание эпик,02.05.2024.10:00,15";
            String expectedString3 = "3,SUBTASK,Сабтаск2,NEW,Описание Сабтаск2,02.05.2024.10:00,15," + epic.getId();

            assertEquals(expectedString1, strings.get(1));
            assertEquals(expectedString2, strings.get(2));
            assertEquals(expectedString3, strings.get(3));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении/записи файла в тесте");
        }

    }

    @Test
    void loadFromFile_throwsManagerSaveException() {
        Path nonExistentPath = Paths.get("test/resourcesblablabla/memory.csv");

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentPath.toFile()));
    }

}
