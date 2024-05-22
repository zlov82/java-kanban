package ru.yandex.javacourse.kanban.httpServer;

import ru.yandex.javacourse.kanban.exceptions.*;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.kanban.manager.TaskManager;
import ru.yandex.javacourse.kanban.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private Gson gson;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    public TaskHandler(TaskManager manager, Gson gson) {
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

        // GET /posts/ - вывод всех записей
        if (optionalQueryId.isEmpty()) {
            List<Task> listTask = manager.getAllTasks();
            sendSuccess(exchange, gson.toJson(listTask));
        } else { // GET /posts/{id}
            Task task = manager.getTaskById(optionalQueryId.get());
            if (task != null) {
                sendSuccess(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePostMethod(HttpExchange exchange) throws IOException {

        String body = getHttpBody(exchange);
        JsonTaskModel jsonTaskModel = gson.fromJson(body, JsonTaskModel.class);
        Optional<Integer> optionalPostId = Optional.ofNullable(jsonTaskModel.getId());

        // добавление новой задачи
        if (optionalPostId.isEmpty()) { //создание нового поста
            // Создание таска без даты и времени
            if (jsonTaskModel.getStartTime() == null || jsonTaskModel.getStatus() == null) {
                manager.addNewTask(new Task(jsonTaskModel.getTitle()
                        , jsonTaskModel.getDescription()));
                sendSuccessWithOutText(exchange);
                return;
            } else { //создание таска с датой и временем
                try {
                    manager.addNewTask(new Task(jsonTaskModel.getTitle()
                            , jsonTaskModel.getDescription()
                            , jsonTaskModel.getStartTime()
                            , jsonTaskModel.getDuration()));
                    sendSuccessWithOutText(exchange);
                    return;
                } catch (ManagerSaveException | TaskCrossTimeException e) {
                    sendHasInteractions(exchange);
                }
            }
        }

        //Путь POST /tasks/{id} - обновление задачи
        if (optionalPostId.isPresent()) {
            Integer taskId = optionalPostId.get();
            // Создание таска без даты и времени
            if (jsonTaskModel.getStartTime() == null || jsonTaskModel.getStatus() == null) {
                manager.updateTask(new Task(taskId
                        , jsonTaskModel.getTitle()
                        , jsonTaskModel.getDescription()
                        , jsonTaskModel.getStatus()));
                sendSuccessWithOutText(exchange);
                return;
            } else { //обновление таска с датой и временем
                try {
                    manager.updateTask(new Task(taskId
                            , jsonTaskModel.getTitle()
                            , jsonTaskModel.getDescription()
                            , jsonTaskModel.getStatus()
                            , jsonTaskModel.getStartTime()
                            , jsonTaskModel.getDuration()));
                    sendSuccessWithOutText(exchange);
                } catch (ManagerSaveException | TaskCrossTimeException e) {
                    sendHasInteractions(exchange);
                }
            }
        }
    }


    private void handleDeleteMethod(HttpExchange exchange) throws IOException {

        Optional<Integer> optionalQueryId = getQueryTaskId(exchange);

        if (optionalQueryId.isPresent()) {
            try {
                manager.deleteTaskById(optionalQueryId.get());
                sendSuccessWithOutText(exchange);
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }

    }
}