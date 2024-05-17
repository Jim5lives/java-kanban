package com.yandex.app.http;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod(), exchange.getRequestURI().getPath());

        if (endpoint.equals(Endpoint.GET_PRIORITIZED)) {
            handleGetPrioritized(exchange);
        } else {
            sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String response = gson.toJson(super.taskManager.getPrioritizedTasks());
        sendText(exchange, response);
    }
}
