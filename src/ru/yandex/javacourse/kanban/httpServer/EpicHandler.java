package ru.yandex.javacourse.kanban.httpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.kanban.manager.TaskManager;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private Gson gson;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    public EpicHandler(TaskManager manager, Gson gson) {
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
            case "POST":
                handlePostMethod(exchange);
                break;
            case "DELETE":
                handleDeleteMethod(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetMethod(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalQueryId = getQueryTaskId(exchange);

        // GET /epics  -- вывод всех эпиков
        if (optionalQueryId.isEmpty()) {
            List<Epic> epicList = manager.getAllEpics();
            sendSuccess(exchange, gson.toJson(epicList));
        } else if (!getQueryEpicSubtask(exchange)){ // GET /epics/{id}
            Epic epic = manager.getEpicById(optionalQueryId.get());
            if (epic != null) {
                sendSuccess(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange);
            }
        } else { //  GET /epics/{id}/subtasks
            List<Subtask> subtaskList = manager.getAllTasksByEpic(optionalQueryId.get());
            if (!subtaskList.isEmpty()) {
                sendSuccess(exchange,gson.toJson(subtaskList));
            }else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePostMethod(HttpExchange exchange) throws IOException {
        try {
            String body = getHttpBody(exchange);
            JsonTaskModel jsonTaskModel = gson.fromJson(body, JsonTaskModel.class);

            Optional<Integer> optionalEpicId = Optional.ofNullable(jsonTaskModel.getEpicId());

            if (optionalEpicId.isEmpty()) { // Создание эпика
                manager.addNewEpic(new Epic(jsonTaskModel.getTitle()));
                sendSuccessWithOutText(exchange);
            } else {
                manager.updateEpic(new Epic(optionalEpicId.get(), jsonTaskModel.getTitle()));
                sendSuccessWithOutText(exchange);
            }
        } catch (NullPointerException e) { // вызвали POS без тела
            sendNotFound(exchange);
        }

    }

    private void handleDeleteMethod(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalQueryId = getQueryTaskId(exchange);

        if (optionalQueryId.isPresent()) {
            try {
                manager.deleteEpicById(optionalQueryId.get());
                sendSuccessWithOutText(exchange);
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }

    }

    private boolean getQueryEpicSubtask(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath().split("/")[3];
            if (path.equalsIgnoreCase("subtasks")) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }
}
