package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Progress;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createTaskManager();

    protected T taskManager = createTaskManager();

    @Test
    void getAllTasks_ShouldReturnAllAddedTasks() {
        Task newTask1 = new Task("Задача 1", "Описание 1",
                LocalDateTime.of(2024, 5, 2, 0, 0), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 1_1", "Описание 1_1",
                LocalDateTime.of(2024, 5, 2, 0, 15), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        List<Task> allTasks = taskManager.getAllTasks();

        Assertions.assertEquals(newTask1, allTasks.get(0));
        Assertions.assertEquals(newTask2, allTasks.get(1));
    }

    @Test
    void deleteAllTasks_ShouldClearMapFromAllTasks() {
        Task newTask1 = new Task("Задача 2", "Описание 2",
                LocalDateTime.of(2024, 5, 2, 0, 30), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 2_1", "Описание 2_1",
                LocalDateTime.of(2024, 5, 2, 0, 45), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        taskManager.deleteAllTasks();
        List<Task> allTasks = taskManager.getAllTasks();

        Assertions.assertEquals(0, allTasks.size());
    }

    @Test
    void getTask_ShouldReturnTask() {
        Task expected = new Task("Задача 3", "Описание 3", 0, Progress.NEW,
                LocalDateTime.of(2024, 5, 2, 1, 0), Duration.ofMinutes(15));
        Task newTask = new Task("Задача 3", "Описание 3",
                LocalDateTime.of(2024, 5, 2, 1, 15), Duration.ofMinutes(15));
        taskManager.addTask(newTask);

        Task actual = taskManager.getTask(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTask_ShouldShouldSaveTaskToHistory() {
        Task newTask = new Task("Задача 4", "Описание 4",
                LocalDateTime.of(2024, 5, 2, 1, 30), Duration.ofMinutes(15));
        taskManager.addTask(newTask);

        taskManager.getTask(0);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newTask, history.getFirst());
    }

    @Test
    void addTask_ShouldGenerateIdAndSaveTask() {
        Task expected = new Task("Задача 5", "Описание 5", 0, Progress.NEW,
                LocalDateTime.of(2024, 5, 2, 1, 45), Duration.ofMinutes(15));
        Task newTask = new Task("Задача 5", "Описание 5",
                LocalDateTime.of(2024, 5, 2, 2, 0), Duration.ofMinutes(15));

        taskManager.addTask(newTask);

        Task actual = taskManager.getTask(0);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateTask_UpdatedTaskShouldHaveSameId() {
        Task expected = new Task("Задача 6_1", "Описание 6_1",
                LocalDateTime.of(2024, 5, 2, 2, 15), Duration.ofMinutes(15));
        Task newTask = new Task("Задача 6", "Описание 6",
                LocalDateTime.of(2024, 5, 2, 2, 30), Duration.ofMinutes(15));
        taskManager.addTask(newTask);
        newTask.setName("Задача 6_1");
        newTask.setDescription("Описание 6_1");

        taskManager.updateTask(newTask);

        Assertions.assertEquals(0, newTask.getId());
        Assertions.assertEquals(expected, newTask);
    }

    @Test
    void deleteTask_ShouldRemoveTaskById() {
        Task newTask1 = new Task("Задача 7", "Описание 7",
                LocalDateTime.of(2024, 5, 2, 2, 45), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 7_1", "Описание 7_1",
                LocalDateTime.of(2024, 5, 2, 3, 0), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        taskManager.deleteTask(0);

        List<Task> allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, allTasks.size());
        Assertions.assertEquals(newTask2, allTasks.getFirst());
    }

    @Test
    void addTask_ShouldRewriteSetIdWhenAdded() {
        Task newTask1 = new Task("Задача 8", "Описание 8",
                LocalDateTime.of(2024, 5, 2, 3, 15), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 9", "Описание 9", 0, Progress.NEW,
                LocalDateTime.of(2024, 5, 2, 3, 30), Duration.ofMinutes(15));

        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        Assertions.assertEquals(0, newTask1.getId());
        Assertions.assertEquals(1, newTask2.getId());
    }

    @Test
    void deleteAllEpics_ShouldDeleteAllSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        SubTask subTask1 = new SubTask("Подзадача 1_1", "Описание 1_1", 0,
                LocalDateTime.of(2024, 5, 2, 3, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 1_2", "Описание 1_2", 1,
                LocalDateTime.of(2024, 5, 2, 4, 0), Duration.ofMinutes(15));
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
    void addSubtask_SubtasksShouldHaveEpicId() {
        Epic newEpic = new Epic("Эпик 6", "Описание 6");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 3_1", "Описание 3_1", 0,
                LocalDateTime.of(2024, 5, 2, 4, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(newSubTask);

        Assertions.assertEquals(newEpic.getId(), newSubTask.getEpicId());
    }

    @Test
    void deleteAllSubTasks_ShouldClearSubTasksArrayInAllEpics() {
        Epic epic1 = new Epic("Эпик 4", "Описание 4");
        Epic epic2 = new Epic("Эпик 5", "Описание 5");
        SubTask subTask1 = new SubTask("Подзадача 2_1", "Описание 2_1", 0,
                LocalDateTime.of(2024, 5, 1, 0, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 2_2", "Описание 2_2", 1,
                LocalDateTime.of(2024, 5, 1, 0, 45), Duration.ofMinutes(15));
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
        SubTask newSubTask = new SubTask("Подзадача 3_1", "Описание 3_1", 0,
                LocalDateTime.of(2024, 5, 2, 4, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(newSubTask);

        taskManager.getSubtask(1);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newSubTask, history.getFirst());
    }

    @Test
    void deleteSubtask_epicStatusShouldBeChangedWhenSubtasksAreDeleted() {
        Epic epic1 = new Epic("Эпик 9", "Описание 9");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 7", "Описание 7", 0,
                LocalDateTime.of(2024, 5, 2, 5, 0), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 8", "Описание 8", 0,
                LocalDateTime.of(2024, 5, 2, 5, 15), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        subTask2.setStatus(Progress.DONE);
        taskManager.updateSubTask(subTask2);

        taskManager.deleteSubtask(subTask1.getId());

        Assertions.assertEquals(Progress.DONE, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeDONEWhenSubtasksStatusesDONE() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 0,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 0,
                LocalDateTime.of(2024, 5, 2, 5, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        subTask1.setStatus(Progress.DONE);
        subTask2.setStatus(Progress.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        Assertions.assertEquals(Progress.DONE, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeNEWWhenSubtasksStatusesNEW() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 0,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 0,
                LocalDateTime.of(2024, 5, 2, 5, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        subTask1.setStatus(Progress.NEW);
        subTask2.setStatus(Progress.NEW);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        Assertions.assertEquals(Progress.NEW, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeIN_PROGRESSWhenSubtasksStatusesNEWandDONE() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 0,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 0,
                LocalDateTime.of(2024, 5, 2, 5, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        subTask1.setStatus(Progress.NEW);
        subTask2.setStatus(Progress.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        Assertions.assertEquals(Progress.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    void epicStatusShouldBeIN_PROGRESSWhenSubtasksStatusesIN_PROGRESS() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 0,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 0,
                LocalDateTime.of(2024, 5, 2, 5, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        subTask1.setStatus(Progress.IN_PROGRESS);
        subTask2.setStatus(Progress.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        Assertions.assertEquals(Progress.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    void deleteSubtask_shouldRemoveSubtaskIdFromEpic() {
        Epic epic1 = new Epic("Эпик 9", "Описание 9");
        SubTask subTask1 = new SubTask("Подзадача 7", "Описание 7", 0,
                LocalDateTime.of(2024, 5, 2, 6, 0), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);

        taskManager.deleteSubtask(1);

        List<Integer> epic1Array = epic1.getSubTasksArray();
        assertEquals(0, epic1Array.size());
    }

    @Test
    void deleteTask_ShouldRemoveTaskFromHistory() {
        Task newTask1 = new Task("Задача 10", "Описание 10",
                LocalDateTime.of(2024, 5, 2, 6, 15), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 11", "Описание 11",
                LocalDateTime.of(2024, 5, 2, 6, 30), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);
        taskManager.getTask(newTask1.getId());
        taskManager.getTask(newTask2.getId());

        taskManager.deleteTask(newTask1.getId());

        List<Task> tasksInHistory = taskManager.getHistory();
        Assertions.assertEquals(1, tasksInHistory.size());
        Assertions.assertEquals(newTask2, tasksInHistory.getFirst());
    }

    @Test
    void deleteEpic_ShouldDeleteEpicAndItsSubTasksFromHistory() {
        Epic epic1 = new Epic("Эпик 10", "Описание 10");
        SubTask subTask1 = new SubTask("Подзадача 8", "Описание 8", 0,
                LocalDateTime.of(2024, 5, 2, 7, 15), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 9", "Описание 9", 0,
                LocalDateTime.of(2024, 5, 2, 7, 30), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subTask1.getId());
        taskManager.getSubtask(subTask2.getId());

        taskManager.deleteEpic(epic1.getId());

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void deleteALLEpics_ShouldDeleteAllEpicsAndItsSubTasksFromHistory() {
        Epic epic1 = new Epic("Эпик 11", "Описание 11");
        Epic epic2 = new Epic("Эпик 12", "Описание 12");
        Epic epic3 = new Epic("Эпик 13", "Описание 13");
        SubTask subTask1 = new SubTask("Подзадача 10", "Описание 10", 0,
                LocalDateTime.of(2024, 5, 2, 7, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 11", "Описание 11", 1,
                LocalDateTime.of(2024, 5, 2, 8, 0), Duration.ofMinutes(15));
        SubTask subTask3 = new SubTask("Подзадача 12", "Описание 12", 1,
                LocalDateTime.of(2024, 5, 2, 8, 15), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic3.getId());
        taskManager.getSubtask(subTask1.getId());
        taskManager.getSubtask(subTask2.getId());
        taskManager.getSubtask(subTask3.getId());

        taskManager.deleteAllEpics();

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void deleteSubtask_ShouldDeleteSubTaskFromHistory() {
        Epic epic1 = new Epic("Эпик 14", "Описание 14");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 13", "Описание 13", 0,
                LocalDateTime.of(2024, 5, 2, 8, 30), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.getSubtask(subTask1.getId());

        taskManager.deleteSubtask(subTask1.getId());

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void deleteAllSubTasks_ShouldDeleteAllSubTasksFromHistory() {
        Epic epic1 = new Epic("Эпик 15", "Описание 15");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 14", "Описание 14", 0,
                LocalDateTime.of(2024, 5, 2, 8, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 15", "Описание 15", 0,
                LocalDateTime.of(2024, 5, 2, 9, 0), Duration.ofMinutes(15));
        SubTask subTask3 = new SubTask("Подзадача 16", "Описание 16", 0,
                LocalDateTime.of(2024, 5, 2, 9, 15), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.getSubtask(subTask1.getId());
        taskManager.getSubtask(subTask2.getId());
        taskManager.getSubtask(subTask3.getId());

        taskManager.deleteAllSubTasks();

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void getSubtasksFromEpic_shouldReturnListOfSubTasksInEpic() {
        Epic epic = new Epic("Эпик 18", "Описание 18");
        SubTask subTask1 = new SubTask("Подзадача 20", "Описание 20", 0,
                LocalDateTime.of(2024, 5, 2, 10, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 21", "Описание 21", 0,
                LocalDateTime.of(2024, 5, 2, 11, 0), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        List<SubTask> subTasksInEpic = taskManager.getSubtasksFromEpic(epic.getId());
        assertEquals(2, subTasksInEpic.size());
        assertEquals(subTask1, subTasksInEpic.getFirst());
        assertEquals(subTask2, subTasksInEpic.getLast());
    }

    @Test
    void getPrioritizedTasks_shouldSortTasksInChronologicalOrder() {
        Task taskPriority1 = new Task("Задача 12", "Описание 12",
                LocalDateTime.of(2024, 5, 2, 9, 30), Duration.ofMinutes(15));
        Epic epic = new Epic("Эпик 16", "Описание 16");
        SubTask subTaskPriority3 = new SubTask("Подзадача 17", "Описание 17", 1,
                LocalDateTime.of(2024, 5, 2, 10, 0), Duration.ofMinutes(15));
        SubTask subTaskPriority2 = new SubTask("Подзадача 18", "Описание 18", 1,
                LocalDateTime.of(2024, 5, 2, 9, 45), Duration.ofMinutes(15));
        taskManager.addTask(taskPriority1);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTaskPriority3);
        taskManager.addSubTask(subTaskPriority2);

        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size());
        assertEquals(taskPriority1, prioritizedTasks.first());
        assertEquals(subTaskPriority3, prioritizedTasks.last());
    }


    @Test
    void getPrioritizedTasks_shouldNotIncludeTaskWithoutStartTimeAndDuration() {
        Task taskPriority1 = new Task("Задача 13", "Описание 13",
                LocalDateTime.of(2024, 5, 2, 10, 15), Duration.ofMinutes(15));
        Task taskWithoutTime = new Task("Задача 14", "Описание 14");
        Epic epic = new Epic("Эпик 17", "Описание 17");
        SubTask subTaskPriority2 = new SubTask("Подзадача 19", "Описание 19", 2,
                LocalDateTime.of(2024, 5, 2, 10, 30), Duration.ofMinutes(15));

        taskManager.addTask(taskPriority1);
        taskManager.addTask(taskWithoutTime);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTaskPriority2);

        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertEquals(taskPriority1, prioritizedTasks.first());
        assertEquals(subTaskPriority2, prioritizedTasks.last());
    }
}

