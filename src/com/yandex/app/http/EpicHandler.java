package com.yandex.app.http;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Epic;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.TimeCollisionException;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod(), exchange.getRequestURI().getPath());

        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC:
                handleGetEpic(exchange);
                break;
            case POST_EPIC:
                handlePostEpic(exchange);
                break;
            case UPDATE_EPIC:
                handleUpdateEpic(exchange);
                break;
            case DELETE_EPICS:
                handleDeleteEpics(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            case GET_SUBTASKS_FROM_EPIC:
                handleGetSubtasksFromEpic(exchange);
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(super.taskManager.getAllEpics());
        sendText(exchange, response);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(split[2]);
        try {
            String epicJson = gson.toJson(super.taskManager.getEpic(id));
            sendText(exchange, epicJson);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        try {
            String jsonEpic = getJsonString(exchange);
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            taskManager.addEpic(epic);
            sendCreated(exchange, "Эпик %s успешно добавлен.".formatted(epic.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            int epicId = Integer.parseInt(pathSplit[2]);
            boolean isIdValid = taskManager.getAllEpics().stream().map(Epic::getId).anyMatch(id -> id == epicId);
            if (!isIdValid) {
                throw new NotFoundException("Некорректный id в эндпоинте.");
            }
            String jsonEpic = getJsonString(exchange);
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            taskManager.updateEpic(epic);
            sendCreated(exchange, "Эпик %s успешно обновлен.".formatted(epic.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        sendText(exchange, "Все эпики успешно удалены.");
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        try {
            String[] split = exchange.getRequestURI().getPath().split("/");
            int id = Integer.parseInt(split[2]);
            Epic deletedEpic = taskManager.deleteEpic(id);
            sendText(exchange, "Эпик %s успешно удален.".formatted(deletedEpic.getName()));
        } catch (TimeCollisionException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleGetSubtasksFromEpic(HttpExchange exchange) throws IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(split[2]);
        try {
            String subtasksFromEpicJson = gson.toJson(super.taskManager.getSubtasksFromEpic(id));
            sendText(exchange, subtasksFromEpicJson);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
