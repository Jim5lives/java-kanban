package com.yandex.app.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.service.TaskManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected final Gson gson = HttpTaskServer.getGson();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "Такого эндпоинта не существует");
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, 200, text);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, 201, text);
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, 404, text);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 406, "Время задачи пересекается с существующими!");
    }

    protected String getJsonString(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }


    protected Endpoint getEndpoint(String requestMethod, String requestPath) {
        String[] pathSplit = requestPath.split("/");
        String path = pathSplit[1];
        boolean isIdProvided = pathSplit.length == 3;

        if (path.equals("tasks")) {
            //если в пути передан id
            if (isIdProvided) {
                return switch (requestMethod) {
                    case "GET" -> Endpoint.GET_TASK;
                    case "DELETE" -> Endpoint.DELETE_TASK;
                    case "POST" -> Endpoint.UPDATE_TASK;
                    default -> Endpoint.UNKNOWN;
                };
            } else {
                return switch (requestMethod) {
                    case "GET" -> Endpoint.GET_TASKS;
                    case "POST" -> Endpoint.POST_TASK;
                    case "DELETE" -> Endpoint.DELETE_TASKS;
                    default -> Endpoint.UNKNOWN;
                };
            }

        } else if (path.equals("epics")) {

            if (isIdProvided) {
                return switch (requestMethod) {
                    case "GET" -> Endpoint.GET_EPIC;
                    case "DELETE" -> Endpoint.DELETE_EPIC;
                    case "POST" -> Endpoint.UPDATE_EPIC;
                    default -> Endpoint.UNKNOWN;
                };
            } else if (pathSplit.length == 4 && pathSplit[3].equals("subtasks")) {
                return Endpoint.GET_SUBTASKS_FROM_EPIC;
            } else {
                return switch (requestMethod) {
                    case "GET" -> Endpoint.GET_EPICS;
                    case "POST" -> Endpoint.POST_EPIC;
                    case "DELETE" -> Endpoint.DELETE_EPICS;
                    default -> Endpoint.UNKNOWN;
                };
            }

        } else if (path.equals("subtasks")) {

            if (isIdProvided) {
                return switch (requestMethod) {
                    case "GET" -> Endpoint.GET_SUBTASK;
                    case "DELETE" -> Endpoint.DELETE_SUBTASK;
                    case "POST" -> Endpoint.UPDATE_SUBTASK;
                    default -> Endpoint.UNKNOWN;
                };
            } else {
                return switch (requestMethod) {
                    case "GET" -> Endpoint.GET_SUBTASKS;
                    case "POST" -> Endpoint.POST_SUBTASK;
                    case "DELETE" -> Endpoint.DELETE_SUBTASKS;
                    default -> Endpoint.UNKNOWN;
                };
            }

        } else if (path.equals("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED;

        } else if (path.equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }
        return Endpoint.UNKNOWN;
    }
}
