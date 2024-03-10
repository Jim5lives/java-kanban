package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.List;

public interface TaskManager {
    // TASK
    //a. Получение списка всех задач.
    List<Task> getAllTasks();

    //b. Удаление всех задач.
    void deleteAllTasks();

    //c. Получение по идентификатору.
    Task getTask(int id);

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    Task addTask(Task newTask);

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    Task updateTask(Task updatedTask);

    //f. Удаление по идентификатору.
    Task deleteTask(int id);

    // EPIC
    //a. Получение списка всех задач.
    List<Epic> getAllEpics();

    //b. Удаление всех задач.
    void deleteAllEpics();

    //c. Получение по идентификатору.
    Epic getEpic(int id);

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    Epic addEpic(Epic newEpic);

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    Epic updateEpic(Epic updatedEpic);

    //f. Удаление по идентификатору.
    Epic deleteEpic(int id);

    //----------------------------------------------------------------------------------------------------------------------
    // SUBTASKS
    //a. Получение списка всех задач.
    List<SubTask> getAllSubTasks();

    //b. Удаление всех задач.
    void deleteAllSubTasks();

    //c. Получение по идентификатору.
    SubTask getSubtask(int id);

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    SubTask addSubTask(SubTask newSubTask);

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    SubTask updateSubTask(SubTask updatedSubTask);

    //f. Удаление по идентификатору.
    SubTask deleteSubtask(int id);

    //----------------------------------------------------------------------------------------------------------------------
//    Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.
    List<SubTask> getSubtasksFromEpic(int id);

    // получении истории
     List<Task> getHistory();

}
