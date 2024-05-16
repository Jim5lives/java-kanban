package com.yandex.app.http;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TimeCollisionException;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod(), exchange.getRequestURI().getPath());

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK:
                handleGetTask(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case UPDATE_TASK:
                handleUpdateTask(exchange);
                break;
            case DELETE_TASKS:
                handleDeleteTasks(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(super.taskManager.getAllTasks());
        sendText(exchange, response);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(split[2]);
        try {
            String taskJson = gson.toJson(super.taskManager.getTask(id));
            sendText(exchange, taskJson);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            String jsonTask = getJsonString(exchange);
            Task task = gson.fromJson(jsonTask, Task.class);
            taskManager.addTask(task);
            sendCreated(exchange, "Задача %s успешно добавлена.".formatted(task.getName()));

        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            int taskId = Integer.parseInt(pathSplit[2]);
            boolean isIdValid = taskManager.getAllTasks().stream().map(Task::getId).anyMatch(id -> id == taskId);
            if (!isIdValid) {
                throw new NotFoundException("Некорректный id в эндпоинте.");
            }
            String jsonTask = getJsonString(exchange);
            Task task = gson.fromJson(jsonTask, Task.class);
            taskManager.updateTask(task);
            sendCreated(exchange, "Задача %s успешно обновлена.".formatted(task.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendText(exchange, "Все задачи успешно удалены.");
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            String[] split = exchange.getRequestURI().getPath().split("/");
            int id = Integer.parseInt(split[2]);
            Task taskToDelete = taskManager.deleteTask(id);
            sendText(exchange, "Задача %s успешно удалена.".formatted(taskToDelete.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

}
