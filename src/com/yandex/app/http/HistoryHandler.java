package com.yandex.app.http;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.service.TaskManager;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod(), exchange.getRequestURI().getPath());

        if (endpoint.equals(Endpoint.GET_HISTORY)) {
            handleGetHistory(exchange);
        } else {
            sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = gson.toJson(super.taskManager.getHistory());
        sendText(exchange, response);
    }
}
