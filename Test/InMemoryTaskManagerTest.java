import com.yandex.app.model.Epic;
import com.yandex.app.model.Progress;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefaultTaskManager();
    }

    @Test
    void getAllTasks_ShouldReturnAllAddedTasks() {
        Task newTask1 = new Task("Задача 1", "Описание 1");
        Task newTask2 = new Task("Задача 1_1", "Описание 1_1");
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        List<Task> allTasks = taskManager.getAllTasks();

        Assertions.assertEquals(newTask1, allTasks.get(0));
        Assertions.assertEquals(newTask2, allTasks.get(1));

    }

    @Test
    void deleteAllTasks_ShouldClearMapFromAllTasks() {
        Task newTask1 = new Task("Задача 2", "Описание 2");
        Task newTask2 = new Task("Задача 2_1", "Описание 2_1");
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        taskManager.deleteAllTasks();
        List<Task> allTasks = taskManager.getAllTasks();

        Assertions.assertEquals(0, allTasks.size());

    }

    @Test
    void getTask_ShouldReturnTask() {
        Task expected = new Task("Задача 3", "Описание 3", 0, Progress.NEW);
        Task newTask = new Task("Задача 3", "Описание 3");
        taskManager.addTask(newTask);

        Task actual = taskManager.getTask(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTask_ShouldShouldSaveTaskToHistory() {
        Task newTask = new Task("Задача 4", "Описание 4");
        taskManager.addTask(newTask);

        taskManager.getTask(0);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newTask, history.getFirst());
    }

    @Test
    void addTask_ShouldGenerateIdAndSaveTask() {
        Task expected = new Task("Задача 5", "Описание 5", 0, Progress.NEW);
        Task newTask = new Task("Задача 5", "Описание 5");

        taskManager.addTask(newTask);

        Task actual = taskManager.getTask(0);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateTask_UpdatedTaskShouldHaveSameId() {
        Task expected = new Task("Задача 6_1", "Описание 6_1");
        Task newTask = new Task("Задача 6", "Описание 6");
        taskManager.addTask(newTask);
        newTask.setName("Задача 6_1");
        newTask.setDescription("Описание 6_1");

        taskManager.updateTask(newTask);

        Assertions.assertEquals(0, newTask.getId());
        Assertions.assertEquals(expected, newTask);
    }

    @Test
    void deleteTask_ShouldRemoveTaskById() {
        Task newTask1 = new Task("Задача 7", "Описание 7");
        Task newTask2 = new Task("Задача 7_1", "Описание 7_1");
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        taskManager.deleteTask(0);

        List<Task> allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, allTasks.size());
        Assertions.assertEquals(newTask2, allTasks.getFirst());
    }

    @Test
    void addTask_ShouldRewriteSetIdWhenAdded() {
        Task newTask1 = new Task("Задача 8", "Описание 8");
        Task newTask2 = new Task("Задача 9", "Описание 9", 0, Progress.NEW);

        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        Assertions.assertEquals(0, newTask1.getId());
        Assertions.assertEquals(1, newTask2.getId());
    }


    @Test
    void deleteAllEpics_ShouldDeleteAllSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        SubTask subTask1 = new SubTask("Подзадача 1_1", "Описание 1_1", 0);
        SubTask subTask2 = new SubTask("Подзадача 1_2", "Описание 1_2", 1);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllEpics();

        List<Epic> allEpics = taskManager.getAllEpics();
        List<SubTask> allSubtasks = taskManager.getAllSubTasks();
        Assertions.assertEquals(0, allEpics.size());
        Assertions.assertEquals(0, allSubtasks.size());
    }

    @Test
    void getEpic_ShouldSaveEpicToHistory() {
        Epic newEpic = new Epic("Эпик 3", "Описание 3");
        taskManager.addEpic(newEpic);

        taskManager.getEpic(0);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newEpic, history.getFirst());
    }

    @Test
    void deleteAllSubTasks_ShouldClearSubTasksArrayInAllEpics() {
        Epic epic1 = new Epic("Эпик 4", "Описание 4");
        Epic epic2 = new Epic("Эпик 5", "Описание 5");
        SubTask subTask1 = new SubTask("Подзадача 2_1", "Описание 2_1", 0);
        SubTask subTask2 = new SubTask("Подзадача 2_2", "Описание 2_2", 1);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllSubTasks();

        List<Integer> epic1Array = epic1.getSubTasksArray();
        List<Integer> epic2Array = epic2.getSubTasksArray();

        assertEquals(0, epic1Array.size());
        assertEquals(0, epic2Array.size());
    }

    @Test
    void getSubtask_ShouldSaveSubtaskToHistory() {
        Epic newEpic = new Epic("Эпик 6", "Описание 6");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 3_1", "Описание 3_1", 0);
        taskManager.addSubTask(newSubTask);

        taskManager.getSubtask(1);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(history.size(), 1);
        Assertions.assertEquals(newSubTask, history.getFirst());
    }

    @Test
    void deleteSubtask_epicStatusShouldBeChangedWhenSubtasksAreDeleted() {
        Epic epic1 = new Epic("Эпик 9", "Описание 9");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 7", "Описание 7", 0);
        SubTask subTask2 = new SubTask("Подзадача 8", "Описание 8", 0);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        subTask2.setStatus(Progress.DONE);
        taskManager.updateSubTask(subTask2);

        taskManager.deleteSubtask(subTask1.getId());

        Assertions.assertEquals(Progress.DONE, epic1.getStatus());
    }

    @Test
    void addedToHistoryTasksShouldBePreviousVersions() {
        Task newTask = new Task("Изначальная задача", "Описание 10");
        taskManager.addTask(newTask);
        Epic newEpic = new Epic("Эпик 7", "Изначальное описание");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 4", "Описание 4", 1);
        taskManager.addSubTask(newSubTask);

        taskManager.getTask(0);
        taskManager.getEpic(1);
        taskManager.getSubtask(2);
        newTask.setName("Задача 100");
        newEpic.setDescription("Описание 100");
        newSubTask.setStatus(Progress.DONE);
        taskManager.updateTask(newTask);
        taskManager.updateEpic(newEpic);
        taskManager.updateSubTask(newSubTask);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals("Изначальная задача", history.get(0).getName());
        Assertions.assertEquals("Изначальное описание", history.get(1).getDescription());
        Assertions.assertEquals(Progress.NEW, history.get(2).getStatus());
    }

    @Test
    void epicStatusShouldBeChangedBySubtaskStatuses() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 0);
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 0);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        subTask1.setStatus(Progress.DONE);
        subTask2.setStatus(Progress.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        Assertions.assertEquals(Progress.DONE, epic1.getStatus());
    }



}
