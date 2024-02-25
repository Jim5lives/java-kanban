package com.yandex.app;
import com.yandex.app.service.Progress;
import com.yandex.app.service.TaskManager;
import com.yandex.app.model.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Дело 1","Погладить кота");
        Task task2 = new Task("Дело 2", "Сделать кальян");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Уборка", "Прибираем в квартире");
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("Пол", "Помыть полы во всей квартире", 2);
        SubTask subTask2 = new SubTask("Пыль", "Протереть пыль на всех поверхностях", 2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        Epic epic2 = new Epic("Тренировка", "Сходить в зал и бассейн");
        taskManager.addEpic(epic2);
        SubTask subTask3 = new SubTask("Грудь", "Жим лёжа и тренажёры", 5);
        taskManager.addSubTask(subTask3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getEpics());
        System.out.println();
        System.out.println();

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

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getEpics());
        System.out.println();

        taskManager.deleteTask(0);
        taskManager.deleteEpic(2);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getEpics());
        System.out.println();

    }
}
