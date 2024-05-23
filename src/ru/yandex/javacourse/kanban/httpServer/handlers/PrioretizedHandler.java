package ru.yandex.javacourse.kanban.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.kanban.manager.TaskManager;
import ru.yandex.javacourse.kanban.tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioretizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private Gson gson;

    public PrioretizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetMethod(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetMethod(HttpExchange exchange) throws IOException {
        List<Task> prioretizedTasks = manager.getPrioritizedTasks();
        sendSuccess(exchange, gson.toJson(prioretizedTasks));
    }
}
