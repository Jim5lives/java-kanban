package com.yandex.app.service;

import com.yandex.app.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager;
    protected int id = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // TASK
    //a. Получение списка всех задач.
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    //b. Удаление всех задач.
    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    //c. Получение по идентификатору.
    @Override
    public Task getTask(int id) {
            Task task = tasks.get(id);
            if (task != null) {
                historyManager.add(task);
            }
        return task;
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public Task addTask(Task newTask) {
        newTask.setId(generateId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public Task updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;
    }

    //f. Удаление по идентификатору.
    @Override
    public Task deleteTask(int id) {
        historyManager.remove(id);
        return tasks.remove(id);
    }

    //------------------------------------------------------------------------------------------------------------------
    // EPIC
    //a. Получение списка всех задач.
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //b. Удаление всех задач.
    @Override
    public void deleteAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subTasks.clear();
    }

    //c. Получение по идентификатору.
    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic oldEpic = epics.get(updatedEpic.getId());
        oldEpic.setName(updatedEpic.getName());
        oldEpic.setDescription(updatedEpic.getDescription());
        return oldEpic;
    }

    //f. Удаление по идентификатору.
    @Override
    public Epic deleteEpic(int id) {
        List<SubTask> subTasksToDelete = getSubtasksFromEpic(id);
        for (SubTask subTask : subTasksToDelete) {
            subTasks.remove(subTask.getId());
            historyManager.remove(subTask.getId());
        }
        historyManager.remove(id);
        return epics.remove(id);
    }

    //------------------------------------------------------------------------------------------------------------------
    // SUBTASKS
    //a. Получение списка всех задач.
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    //b. Удаление всех задач.
    @Override
    public void deleteAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearAllSubTaskFromEpic();
            setEpicStatus(epic);
        }
    }

    //c. Получение по идентификатору.
    @Override
    public SubTask getSubtask(int id) {
            SubTask subTask = subTasks.get(id);
            if (subTask != null) {
                historyManager.add(subTask);
            }
            return subTask;
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public SubTask addSubTask(SubTask newSubTask) {
        newSubTask.setId(generateId());
        Epic linkedEpic = epics.get(newSubTask.getEpicId());
        linkedEpic.linkSubTaskToEpic(newSubTask.getId());
        subTasks.put(newSubTask.getId(), newSubTask);
        setEpicStatus(linkedEpic);
        return newSubTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public SubTask updateSubTask(SubTask updatedSubTask) {
        subTasks.put(updatedSubTask.getId(), updatedSubTask);
        setEpicStatus(epics.get(updatedSubTask.getEpicId()));
        return updatedSubTask;
    }

    //f. Удаление по идентификатору.
    @Override
    public SubTask deleteSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic linkedEpic = epics.get(subTask.getEpicId());
        linkedEpic.removeSubTaskFromEpic(subTask.getId());
        setEpicStatus(linkedEpic);
        historyManager.remove(id);
        return subTasks.remove(id);
    }

//----------------------------------------------------------------------------------------------------------------------
//    Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubtasksFromEpic(int id) {
        Epic selectedEpic = epics.get(id);
        List<Integer> subTasksIdsArray = selectedEpic.getSubTasksArray();
        List<SubTask> subTasksInSelectedEpic = new ArrayList<>();
        for (Integer subTaskNumber : subTasksIdsArray) {
            subTasksInSelectedEpic.add(subTasks.get(subTaskNumber));
        }
        return subTasksInSelectedEpic;
    }

    // получении истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // изменение статуса эпика
    private Epic setEpicStatus(Epic epic) {
        int subTasksDone = 0;
        int subtasksNew = 0;
        for (SubTask subTask : getSubtasksFromEpic(epic.getId())) {
            if (subTask.getStatus() == Progress.DONE) {
                subTasksDone++;
            } else if (subTask.getStatus() == Progress.IN_PROGRESS) {
                epic.setStatus(Progress.IN_PROGRESS);
                return epic;
            } else {
                subtasksNew++;
            }
        }
        if (subtasksNew == epic.getSubTasksArray().size()) {
            epic.setStatus(Progress.NEW);
        } else if (subTasksDone == epic.getSubTasksArray().size()) {
            epic.setStatus(Progress.DONE);
        } else {
            epic.setStatus(Progress.IN_PROGRESS);
        }
        return epic;
    }

    // генератор id
    private int generateId() {
        return id++;
    }
}