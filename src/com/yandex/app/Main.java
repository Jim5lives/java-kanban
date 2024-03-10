package com.yandex.app;

import com.yandex.app.model.Progress;
import com.yandex.app.model.*;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();

        Task task1 = new Task("Таск 1","Погладить кота");
        Task task2 = new Task("Таск 2", "Сделать кальян");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Прибираем в квартире");
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("Сабтаск 1-1", "Помыть полы во всей квартире", 2);
        SubTask subTask2 = new SubTask("Сабтаск 2-1", "Протереть пыль на всех поверхностях", 2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        Epic epic2 = new Epic("Эпик 2", "Сходить в зал и бассейн");
        taskManager.addEpic(epic2);
        SubTask subTask3 = new SubTask("Сабтаск 3-2", "Жим лёжа и тренажёры", 5);
        taskManager.addSubTask(subTask3);
        System.out.println("Созданы задачи:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Созданы подзадачи:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Созданы эпики:");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();

        // получаем по id и записываем в историю
        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getSubtask(3);
        taskManager.getSubtask(4);
        taskManager.getSubtask(6);
        taskManager.getEpic(2);
        taskManager.getEpic(5);
        taskManager.getSubtask(3);
        taskManager.getSubtask(4);
        taskManager.getSubtask(6);

        // обновляем все статусы
        task1.setStatus(Progress.DONE);
        task2.setStatus(Progress.IN_PROGRESS);
        subTask1.setStatus(Progress.IN_PROGRESS);
        subTask2.setStatus(Progress.DONE);
        subTask3.setStatus(Progress.DONE);

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        System.out.println("Задачи с новыми статусами:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Подзадачи с новыми статусами:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("Эпики с новыми статусами:");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();

        // проверяем, что всё записалось в историю
        System.out.println("История:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        // удаляем задачу и эпик
        taskManager.deleteTask(0);
        taskManager.deleteEpic(2);

        System.out.println("Осталось после удаления:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
    }
}
