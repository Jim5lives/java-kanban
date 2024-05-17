package com.yandex.app.service;

import com.yandex.app.model.*;

import java.io.*;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final File MEMORY_FILE = new File("src/resources/memory.csv");

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();

    }

    @Override
    public Task getTask(int id) {
        super.getTask(id);
        save();
        return tasks.get(id);
    }

    @Override
    public Task addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return tasks.get(newTask.getId());
    }

    @Override
    public Task updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
        return updatedTask;
    }

    @Override
    public Task deleteTask(int id) {
        Task copyToReturn = tasks.get(id);
        super.deleteTask(id);
        save();
        return copyToReturn;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        super.getEpic(id);
        save();
        return epics.get(id);
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
        return updatedEpic;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic copyToReturn = epics.get(id);
        super.deleteEpic(id);
        save();
        return copyToReturn;
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public SubTask getSubtask(int id) {
        super.getSubtask(id);
        save();
        return subTasks.get(id);
    }

    @Override
    public SubTask addSubTask(SubTask newSubTask) {
        super.addSubTask(newSubTask);
        save();
        return subTasks.get(newSubTask.getId());
    }

    @Override
    public SubTask updateSubTask(SubTask updatedSubTask) {
        super.updateSubTask(updatedSubTask);
        save();
        return updatedSubTask;
    }

    @Override
    public SubTask deleteSubtask(int id) {
        SubTask copyToReturn = subTasks.get(id);
        super.deleteSubtask(id);
        save();
        return copyToReturn;
    }

    // создаём задачу из строки
    private Task fromString(String value) {
        String[] taskSplit = value.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.HH:mm");

        int id = Integer.parseInt(taskSplit[0]);
        TaskType type = TaskType.valueOf(taskSplit[1]);
        String name = taskSplit[2];
        Progress status = Progress.valueOf(taskSplit[3]);
        String description =  taskSplit[4];
        // проверяем, заданы ли у задачи время начала и длительность
        if (taskSplit.length > 6) {
            LocalDateTime startTime = LocalDateTime.parse(taskSplit[5], formatter);
            Duration duration = Duration.parse("PT" + taskSplit[6] + "M");

            return switch (type) {
                case TASK -> new Task(name, description, id, status, startTime, duration);
                case EPIC -> new Epic(name, description, id, status);
                case SUBTASK ->
                        new SubTask(name, description, id, status, Integer.parseInt(taskSplit[7]), startTime, duration);
            };
        } else {
            return switch (type) {
                case TASK -> new Task(name, description, id, status);
                case EPIC -> new Epic(name, description, id, status);
                case SUBTASK -> new SubTask(name, description, id, status, Integer.parseInt(taskSplit[5]));
            };
        }
    }

    // записываем историю в  строку
    private static String historyToString(HistoryManager historyManager) {
        List<Task> currentTasksInHistory = historyManager.getHistory();
        String[] history = new String[currentTasksInHistory.size()];
        for (int i = 0; i < history.length; i++) {
            history[i] = String.valueOf(currentTasksInHistory.get(i).getId());
        }
        return String.join(",", history);
    }

    // извлекаем историю из строки
    private static List<Integer> historyFromString(String value) {
        String[] idFromFile = value.split(",");
        return Arrays.stream(idFromFile).map(id -> Integer.parseInt(String.valueOf(id))).toList();
    }

    // сохраняем всё текущее состояние taskManager
    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMORY_FILE))) {
            bw.write("id,type,name,status,description,startTime,duration,epic\n");
            for (Integer id : tasks.keySet()) {
                bw.write(tasks.get(id).toString() + "\n");
            }
            for (Integer id : epics.keySet()) {
                bw.write(epics.get(id).toString() + "\n");
            }
            for (Integer id : subTasks.keySet()) {
                bw.write(subTasks.get(id).toString() + "\n");
            }

            bw.write("\n" + historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи задач в файл");
        }
    }

    // загружаем состояние taskManager из файла
    static FileBackedTaskManager loadFromFile(File file) {
        HistoryManager historyManager = Managers.getDefaultHistoryManager();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(historyManager);
        try {
            List<String> strings = Files.readAllLines(file.toPath());
            if (strings.isEmpty()) {
                return fileBackedTaskManager;
            }
            int maxId = 0;
            for (int i = 1; i < (strings.size() - 2); i++) {
                String[] parts = strings.get(i).split(",");
                TaskType type = TaskType.valueOf(parts[1]);
                Task task = fileBackedTaskManager.fromString(strings.get(i));
                maxId = Math.max(maxId, task.getId());

                switch (type) {
                    case TASK:
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        fileBackedTaskManager.id++;
                        break;
                    case EPIC:
                            fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                            fileBackedTaskManager.id++;

                        break;
                    case SUBTASK:
                            SubTask subTask = (SubTask) task;
                            fileBackedTaskManager.subTasks.put(subTask.getId(), subTask);
                            fileBackedTaskManager.id++;
                            // привязываем subTask к epic
                            Epic epic = fileBackedTaskManager.epics.get(subTask.getEpicId());
                            epic.linkSubTaskToEpic(subTask.getId());
                            // если у сабтаска заданы время начала и длительность, обновляем данные эпика
                            if (subTask.getDuration() == null || subTask.getStartTime() == null) {
                                fileBackedTaskManager.setEpicEndTimeAndStartTime(epic);
                            }
                        break;
                }
            }
            // устанавливаем поле id в taskManager на основе созданных задач
            fileBackedTaskManager.id = maxId + 1;

            // считываем историю и добавляем в historyManager
            String historyFromFile = strings.getLast();
            if (historyFromFile.isEmpty()) {
                return fileBackedTaskManager;
            }
            List<Integer> history = FileBackedTaskManager.historyFromString(historyFromFile);
            for (Integer id : history) {
                if (fileBackedTaskManager.tasks.containsKey(id)) {
                    historyManager.add(fileBackedTaskManager.tasks.get(id));
                } else if (fileBackedTaskManager.subTasks.containsKey(id)) {
                    historyManager.add(fileBackedTaskManager.subTasks.get(id));
                } else if (fileBackedTaskManager.epics.containsKey(id)) {
                    historyManager.add(fileBackedTaskManager.epics.get(id));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
        return fileBackedTaskManager;
    }
}
