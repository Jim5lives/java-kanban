package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    // TASK
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTask(int id);

    Task addTask(Task newTask);

    Task updateTask(Task updatedTask);

    Task deleteTask(int id);

    // EPIC
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpic(int id);

    Epic addEpic(Epic newEpic);

    Epic updateEpic(Epic updatedEpic);

    Epic deleteEpic(int id);

    // SUBTASKS
    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

    SubTask getSubtask(int id);

    SubTask addSubTask(SubTask newSubTask);

    SubTask updateSubTask(SubTask updatedSubTask);

    SubTask deleteSubtask(int id);

//    Дополнительные методы:
    List<SubTask> getSubtasksFromEpic(int id);

     List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
