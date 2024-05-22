package ru.yandex.javacourse.kanban.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.kanban.exceptions.ManagerSaveException;
import ru.yandex.javacourse.kanban.exceptions.TaskCrossTimeException;
import ru.yandex.javacourse.kanban.httpServer.JsonTaskModel;
import ru.yandex.javacourse.kanban.manager.TaskManager;
import ru.yandex.javacourse.kanban.tasks.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
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

        if (optionalQueryId.isEmpty()) { //вывод всех подзадач
            List<Subtask> subtaskList = manager.getAllSubTasks();
            sendSuccess(exchange, gson.toJson(subtaskList));
        } else { //вывод конкретной подзадачи
            Subtask subtask = manager.getSubtaskById(optionalQueryId.get());
            if (subtask != null) {
                sendSuccess(exchange, gson.toJson(subtask));
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePostMethod(HttpExchange exchange) throws IOException {
        String body = getHttpBody(exchange);
        JsonTaskModel jsonTaskModel = gson.fromJson(body, JsonTaskModel.class);
        if (jsonTaskModel == null) {
            sendNotFound(exchange);
            return;
        }
        Optional<Integer> optionalSubtaskId = Optional.ofNullable(jsonTaskModel.getId());

        if (optionalSubtaskId.isEmpty()) { // добавление задачи
            if (jsonTaskModel.getStartTime() == null || jsonTaskModel.getDuration() == null) { // подзадача без времени
                manager.addNewSubtask(new Subtask(jsonTaskModel.getTitle()
                        , jsonTaskModel.getDescription()
                        , jsonTaskModel.getEpicId()));
                sendSuccessWithOutText(exchange);
            } else { // подзадача со временем
                try {
                    manager.addNewSubtask(new Subtask(jsonTaskModel.getTitle()
                            , jsonTaskModel.getDescription()
                            , jsonTaskModel.getEpicId()
                            , jsonTaskModel.getStartTime()
                            , jsonTaskModel.getDuration()));
                    sendSuccessWithOutText(exchange);
                } catch (ManagerSaveException | TaskCrossTimeException e) {
                    sendHasInteractions(exchange);
                }
            }
        }


        if (optionalSubtaskId.isPresent()) { // обновление подзадачи
            Integer subtaskId = optionalSubtaskId.get();
            //обновление подзадачи без времени
            if (jsonTaskModel.getStartTime() == null || jsonTaskModel.getStatus() == null) {
                manager.updateSubtask(new Subtask(subtaskId
                        , jsonTaskModel.getTitle()
                        , jsonTaskModel.getDescription()
                        , jsonTaskModel.getEpicId()
                        , jsonTaskModel.getStatus()));
                sendSuccessWithOutText(exchange);
            } else { //обновление подзадачи со временем
                try {
                    manager.updateSubtask(new Subtask(subtaskId
                            , jsonTaskModel.getTitle()
                            , jsonTaskModel.getDescription()
                            , jsonTaskModel.getEpicId()
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
        Optional<Integer> optionalSubtaskId = getQueryTaskId(exchange);

        if (optionalSubtaskId.isPresent()) {
            manager.deleteSubtaskById(optionalSubtaskId.get());
            sendSuccessWithOutText(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}
