package ru.yandex.javacourse.kanban.httpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.kanban.manager.TaskManager;

import java.io.IOException;


public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        printLog(exchange);

        sendSuccess(exchange,"Обработчик Эпика");
    }
}
