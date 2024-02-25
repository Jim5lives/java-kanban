package com.yandex.app.service;
import com.yandex.app.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Task> epics = new HashMap<>();
    private int id = 0;

    // TASK
    //a. Получение списка всех задач.
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    //b. Удаление всех задач.
    public void deleteAllTasks() {
        tasks.clear();
    }

    //c. Получение по идентификатору.
    public Task getTask(int id) {
        return tasks.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public Task addTask(Task newTask) {
        newTask.setId(generateId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updateTask(Task updatedTask) {
        Task currentTask = tasks.get(updatedTask.getId());
        tasks.put(currentTask.getId(), updatedTask);
        return updatedTask;
    }

    //f. Удаление по идентификатору.
    public Task deleteTask(int id) {
        return tasks.remove(id);
    }

//----------------------------------------------------------------------------------------------------------------------

    // EPIC
    //a. Получение списка всех задач.
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //b. Удаление всех задач.
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    //c. Получение по идентификатору.
    public Task getEpic(int id) {
        return epics.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public Task addEpic(Task newEpic) {
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Epic updateEpic(Epic updatedEpic) {
        Task currentTask = tasks.get(updatedEpic.getId());
        tasks.put(currentTask.getId(), updatedEpic);
        setEpicStatus(updatedEpic);
        return updatedEpic;
    }

    //f. Удаление по идентификатору.
    public Task deleteEpic(int id) {
        ArrayList<SubTask> subTasksToDelete = new ArrayList<>();
        subTasksToDelete = getSubtasksFromEpic(id);
        for (SubTask subTask : subTasksToDelete) {
            subTasks.remove(subTask.getId());
        }
        return epics.remove(id);
    }

//----------------------------------------------------------------------------------------------------------------------
    // SUBTASKS
    //a. Получение списка всех задач.
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    //b. Удаление всех задач.
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            Epic currentEpic = (Epic) epics.get(id);
            currentEpic.clearAllSubTaskFromEpic();
            setEpicStatus(currentEpic);
        }
    }

    //c. Получение по идентификатору.
    public SubTask getSubtask(int id) {
        return subTasks.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public SubTask addSubTask(SubTask newSubTask) {
        newSubTask.setId(generateId());
        Epic linkedEpic = (Epic) epics.get(newSubTask.getEpicId());
        linkedEpic.linkSubTaskToEpic(newSubTask.getId());
        subTasks.put(newSubTask.getId(), newSubTask);
        setEpicStatus((Epic) epics.get(newSubTask.getEpicId()));
        return newSubTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public SubTask updateSubTask(SubTask updatedSubTask) {
        subTasks.put(updatedSubTask.getId(), updatedSubTask);
        setEpicStatus((Epic) epics.get(updatedSubTask.getEpicId()));
        return updatedSubTask;
    }

    //f. Удаление по идентификатору.
    public SubTask deleteSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic linkedEpic = (Epic) epics.get(subTask.getEpicId());
        linkedEpic.removeSubTaskFromEpic(subTask.getId());
        return subTasks.remove(id);
    }

//----------------------------------------------------------------------------------------------------------------------
//    Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getSubtasksFromEpic(int id) {
        Epic selectedEpic = (Epic) epics.get(id);
        ArrayList<Integer> subTasksIdsArray = selectedEpic.getSubtasksList(selectedEpic.getId());
        ArrayList<SubTask> subTasksInSelectedEpic = new ArrayList<>();
        for (Integer subTaskNumber : subTasksIdsArray) {
            subTasksInSelectedEpic.add(subTasks.get(subTaskNumber));
        }
        return subTasksInSelectedEpic;
    }

    // getters and setters
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public HashMap<Integer, Task> getEpics() {
        return epics;
    }

    public void setEpics(HashMap<Integer, Task> epics) {
        this.epics = epics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

// изменение статуса эпика
    private Epic setEpicStatus(Epic epic) {
        int subTasksDone = 0;
        int subTasksInProgress = 0;
        int subtasksNew = 0;
        for (SubTask subTask : getSubtasksFromEpic(epic.getId())) {
            if (subTask.getStatus() == Progress.DONE) {
                subTasksDone++;
            } else if (subTask.getStatus() == Progress.IN_PROGRESS) {
                subTasksInProgress++;
            } else {
                subtasksNew++;
            }
            if (epic.getSubTasksArray() == null || subtasksNew == epic.getSubTasksArray().size()) {
                epic.setStatus(Progress.NEW);
            } else if (subTasksDone == epic.getSubTasksArray().size()) {
                epic.setStatus(Progress.DONE);
            } else {
                epic.setStatus(Progress.IN_PROGRESS);
            }
        }
        return epic;
    }
    private int generateId() { // генератор id
        return id++;
    }
}