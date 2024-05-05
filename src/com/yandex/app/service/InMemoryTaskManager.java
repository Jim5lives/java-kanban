package com.yandex.app.service;

import com.yandex.app.model.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

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
        tasks.keySet().forEach(taskId -> historyManager.remove(taskId));
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
        if (isTaskTimeValid(newTask)) {
            checkCollisions(newTask);
        }
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public Task updateTask(Task updatedTask) {
        if (isTaskTimeValid(updatedTask)) {
            checkCollisions(updatedTask);
        }
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
        epics.keySet().forEach(epicId -> historyManager.remove(epicId));
        subTasks.keySet().forEach(subTaskId -> historyManager.remove(subTaskId));
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
        getSubtasksFromEpic(id).stream().map(SubTask::getId).forEach(subTaskId -> {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        });
        historyManager.remove(id);
        return epics.remove(id);
    }

    //------------------------------------------------------------------------------------------------------------------
    // SUBTASKS
    //a. Получение списка всех подзадач.
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    //b. Удаление всех подзадач.
    @Override
    public void deleteAllSubTasks() {
        subTasks.keySet().forEach(subTaskId -> historyManager.remove(subTaskId));
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearAllSubTaskFromEpic();
            setEpicStatus(epic);
            setEpicEndTimeAndStartTime(epic);
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
        if (isTaskTimeValid(newSubTask)) {
            checkCollisions(newSubTask);
        }
        subTasks.put(newSubTask.getId(), newSubTask);
        setEpicStatus(linkedEpic);
        setEpicEndTimeAndStartTime(linkedEpic);
        return newSubTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public SubTask updateSubTask(SubTask updatedSubTask) {
        if (isTaskTimeValid(updatedSubTask)) {
            checkCollisions(updatedSubTask);
        }
        subTasks.put(updatedSubTask.getId(), updatedSubTask);
        setEpicStatus(epics.get(updatedSubTask.getEpicId()));
        setEpicEndTimeAndStartTime(epics.get(updatedSubTask.getEpicId()));
        return updatedSubTask;
    }

    //f. Удаление по идентификатору.
    @Override
    public SubTask deleteSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic linkedEpic = epics.get(subTask.getEpicId());
        linkedEpic.removeSubTaskFromEpic(subTask.getId());
        setEpicStatus(linkedEpic);
        setEpicEndTimeAndStartTime(linkedEpic);
        historyManager.remove(id);
        return subTasks.remove(id);
    }

    //----------------------------------------------------------------------------------------------------------------------
//    Дополнительные методы:
//    получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubtasksFromEpic(int id) {
        Epic selectedEpic = epics.get(id);
        List<Integer> subTasksIds = selectedEpic.getSubTasksArray();
        List<SubTask> subTasksInSelectedEpic = new ArrayList<>();
        subTasksIds.forEach(subTaskId -> subTasksInSelectedEpic.add(subTasks.get(subTaskId)));
        return subTasksInSelectedEpic;
    }

    // получении истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Получаем список задач по приоритету
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        tasks.values().stream().filter(task -> task.getStartTime() != null).forEach(prioritizedTasks::add);
        subTasks.values().stream().filter(subTask -> subTask.getStartTime() != null).forEach(prioritizedTasks::add);
        return prioritizedTasks;
    }

    //установлене duration и endTime эпика
    protected void setEpicEndTimeAndStartTime(Epic epic) {
        List<SubTask> subTasksInEpic = getSubtasksFromEpic(epic.getId());
        if (!subTasksInEpic.isEmpty()) {

            //находим duration эпика из его сабтасков
            Duration epicDuration = subTasksInEpic.stream()
                    .map(Task::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);

            epic.setDuration(epicDuration);

            //находим и устанавливаем время начала эпика по самому раннему SubTask
            Optional<SubTask> earliestSubTask = subTasksInEpic.stream()
                    .min(Comparator.comparing(SubTask::getStartTime));

            if (earliestSubTask.isPresent()) {
                Instant earliestSubTaskStartTime = earliestSubTask.get().getStartTime();
                epic.setStartTime(earliestSubTaskStartTime);
            }

            //находим и устанавливаем время конца эпика по самому позднему SubTask
            Optional<SubTask> latestSubTask = subTasksInEpic.stream()
                    .max(Comparator.comparing(SubTask::getEndTime));

            if (latestSubTask.isPresent()) {
                Instant latestSubTaskEndTime = latestSubTask.get().getEndTime();
                epic.setEndTime(latestSubTaskEndTime);
            }

        } else {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
        }
    }

    // изменение статуса эпика
    private void setEpicStatus(Epic epic) {
        int subTasksDone = 0;
        int subtasksNew = 0;
        for (SubTask subTask : getSubtasksFromEpic(epic.getId())) {
            if (subTask.getStatus() == Progress.DONE) {
                subTasksDone++;
            } else if (subTask.getStatus() == Progress.IN_PROGRESS) {
                epic.setStatus(Progress.IN_PROGRESS);
                return;
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
    }

    // проверка на пересечение времени задач
    private void checkCollisions(Task newTask) {
        Instant newStartInstant = newTask.getStartTime();
        Instant newEndInstant = newTask.getEndTime();
        boolean tasksCollide =  getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(task -> newStartInstant.isBefore(task.getEndTime())
                        && newEndInstant.isAfter(task.getStartTime()));
        if (tasksCollide) throw new TimeCollisionException("Некорректное время задачи!");
    }

    //метод для валидации времени в таске
    private boolean isTaskTimeValid(Task task) {
       return task.getDuration() != null && task.getStartTime() != null && task.getDuration().toMinutes() != 0;
    }


    // генератор id
    private int generateId() {
        return id++;
    }
}