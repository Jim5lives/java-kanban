package com.yandex.app.service;
import com.yandex.app.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
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
        tasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;
    }

    //f. Удаление по идентификатору.
    public Task deleteTask(int id) {
        return tasks.remove(id);
    }

//----------------------------------------------------------------------------------------------------------------------

    // EPIC
    //a. Получение списка всех задач.
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //b. Удаление всех задач.
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    //c. Получение по идентификатору.
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Epic updateEpic(Epic updatedEpic) {
        Epic oldEpic = epics.get(updatedEpic.getId());
        oldEpic.setName(updatedEpic.getName());
        oldEpic.setDescription(updatedEpic.getDescription());
        return oldEpic;
    }

    //f. Удаление по идентификатору.
    public Epic deleteEpic(int id) {
        ArrayList<SubTask> subTasksToDelete = getSubtasksFromEpic(id);
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
        for (Epic epic : epics.values()) {
            epic.clearAllSubTaskFromEpic();
            setEpicStatus(epic);
        }
    }

    //c. Получение по идентификатору.
    public SubTask getSubtask(int id) {
        return subTasks.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public SubTask addSubTask(SubTask newSubTask) {
        newSubTask.setId(generateId());
        Epic linkedEpic = epics.get(newSubTask.getEpicId());
        linkedEpic.linkSubTaskToEpic(newSubTask.getId());
        subTasks.put(newSubTask.getId(), newSubTask);
        setEpicStatus(linkedEpic);
        return newSubTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public SubTask updateSubTask(SubTask updatedSubTask) {
        subTasks.put(updatedSubTask.getId(), updatedSubTask);
        setEpicStatus(epics.get(updatedSubTask.getEpicId()));
        return updatedSubTask;
    }

    //f. Удаление по идентификатору.
    public SubTask deleteSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic linkedEpic = epics.get(subTask.getEpicId());
        linkedEpic.removeSubTaskFromEpic(subTask.getId());
        setEpicStatus(linkedEpic);
        return subTasks.remove(id);
    }

    //----------------------------------------------------------------------------------------------------------------------
//    Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getSubtasksFromEpic(int id) {
        Epic selectedEpic = epics.get(id);
        ArrayList<Integer> subTasksIdsArray = selectedEpic.getSubtasksList();
        ArrayList<SubTask> subTasksInSelectedEpic = new ArrayList<>();
        for (Integer subTaskNumber : subTasksIdsArray) {
            subTasksInSelectedEpic.add(subTasks.get(subTaskNumber));
        }
        return subTasksInSelectedEpic;
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
            if (subTasksDone == epic.getSubTasksArray().size()) {
                epic.setStatus(Progress.DONE);
            } else {
                epic.setStatus(Progress.IN_PROGRESS);
            }
        }
        if (subtasksNew == epic.getSubTasksArray().size()) {
            epic.setStatus(Progress.NEW);
        }
        return epic;
    }
    private int generateId () { // генератор id
        return id++;
    }

}