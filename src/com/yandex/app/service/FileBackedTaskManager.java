package com.yandex.app.service;

import com.yandex.app.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

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

    private Task fromString(String value) {
        String[] taskSplit = value.split(",");
        return switch (TaskType.valueOf(taskSplit[1])) {
            case TASK -> new Task(taskSplit[2], taskSplit[4],
                    Integer.parseInt(taskSplit[0]), Progress.valueOf(taskSplit[3]));
            case EPIC -> new Epic(taskSplit[2], taskSplit[4],
                    Integer.parseInt(taskSplit[0]), Progress.valueOf(taskSplit[3]));
            case SUBTASK -> new SubTask(taskSplit[2], taskSplit[4],
                    Integer.parseInt(taskSplit[0]), Progress.valueOf(taskSplit[3]), Integer.parseInt(taskSplit[5]));
        };
    }

    private static String historyToString(HistoryManager historyManager) {
        List<Task> currentTasksInHistory = historyManager.getHistory();
        String[] history = new String[currentTasksInHistory.size()];
        for (int i = 0; i < history.length; i++) {
            history[i] = String.valueOf(currentTasksInHistory.get(i).getId());
        }
        return String.join(",", history);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] idFromFile = value.split(",");
        for (String s : idFromFile) {
            int id = Integer.parseInt(String.valueOf(s));
            history.add(id);
        }
        return history;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/resources/memory.csv"))) {
            bw.write("id,type,name,status,description,epic\n");
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
            throw new ManagerSaveException("Ошибка при чтении/записи файла");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        HistoryManager historyManager = new InMemoryHistoryManager();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(historyManager);
        try {
            List<String> strings = Files.readAllLines(file.toPath());
            if (strings.isEmpty()) {
                return fileBackedTaskManager;
            }
            for (int i = 1; i < (strings.size() - 2); i++) {
                String[] parts = strings.get(i).split(",");
                TaskType type = TaskType.valueOf(parts[1]);
                Task task = fileBackedTaskManager.fromString(strings.get(i));
                switch (type) {
                    case TASK:
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        fileBackedTaskManager.id++;
                        break;
                    case EPIC:
                        if (task instanceof Epic epic) {
                            fileBackedTaskManager.epics.put(epic.getId(), epic);
                            fileBackedTaskManager.id++;
                        }
                        break;
                    case SUBTASK:
                        if (task instanceof SubTask subTask) {
                            subTask.setId(Integer.parseInt(parts[0])); // без этой строки почему-то id=0 всегда
                            fileBackedTaskManager.subTasks.put(subTask.getId(), subTask);
                            fileBackedTaskManager.id++;
                        }
                        break;
                }
            }

            String historyFromFile = strings.getLast();
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
            throw new ManagerSaveException("Ошибка при чтении/записи файла");
        }
        return fileBackedTaskManager;
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getFileBackedTaskManager();
        // наполняем менеджер задачами
        Task task1 = new Task("Таск 1", "Собраться");
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Делаем ТЗ-7");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Сабтаск 1", "Долго писать", epic1.getId());
        SubTask subTask2 = new SubTask("Сабтаск 2", "Порадоваться", epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        // наполняем историю
        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subTask1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subTask2.getId());
        //проверяем состояние
        System.out.println("Expected:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getHistory());

        // создаём новый менеджер из файла
        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(Paths.get(
                "src/resources/memory.csv").toFile());
        //Проверяем, что в нём всё совпадает
        System.out.println();
        System.out.println("Actual:");
        System.out.println(taskManager2.getAllTasks());
        System.out.println(taskManager2.getAllEpics());
        System.out.println(taskManager2.getAllSubTasks());
        System.out.println(taskManager2.getHistory());

    }
}
