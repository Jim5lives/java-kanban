package com.yandex.app.http;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TimeCollisionException;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod(), exchange.getRequestURI().getPath());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK:
                handleGetSubtask(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case UPDATE_SUBTASK:
                handleUpdateSubtask(exchange);
                break;
            case DELETE_SUBTASKS:
                handleDeleteSubtasks(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(super.taskManager.getAllSubTasks());
        sendText(exchange, response);
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(split[2]);
        try {
            String jsonSubtask = gson.toJson(super.taskManager.getSubtask(id));
            sendText(exchange, jsonSubtask);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try {
            String jsonSubtask = getJsonString(exchange);
            SubTask subtask = gson.fromJson(jsonSubtask, SubTask.class);
            taskManager.addSubTask(subtask);
            sendCreated(exchange, "Подзадача %s успешно добавлена.".formatted(subtask.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            int subtaskId = Integer.parseInt(pathSplit[2]);
            boolean isIdValid = taskManager.getAllSubTasks().stream()
                    .map(SubTask::getId)
                    .anyMatch(id -> id == subtaskId);
            if (!isIdValid) {
                throw new NotFoundException("Некорректный id в эндпоинте.");
            }
            String jsonTask = getJsonString(exchange);
            SubTask subtask = gson.fromJson(jsonTask, SubTask.class);
            taskManager.updateSubTask(subtask);
            sendCreated(exchange, "Подзадача %s успешно обновлена.".formatted(subtask.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubTasks();
        sendText(exchange, "Все подзадачи успешно удалены.");
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        try {
            String[] split = exchange.getRequestURI().getPath().split("/");
            int id = Integer.parseInt(split[2]);
            SubTask deletadSubtask = taskManager.deleteSubtask(id);
            sendText(exchange, "Подзадача %s успешно удалена.".formatted(deletadSubtask.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
