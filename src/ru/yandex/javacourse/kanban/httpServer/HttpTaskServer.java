package ru.yandex.javacourse.kanban.httpServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.kanban.manager.Managers;
import ru.yandex.javacourse.kanban.manager.TaskManager;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private HttpServer httpServer;
    private Gson gson;

    HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());

        this.gson = gsonBuilder.create();
    }

    public Gson getGson() {
        return gson;
    }

    public void startServer() {
        httpServer.createContext("/tasks", new TaskHandler(manager,gson));
        httpServer.createContext("/epics", new EpicHandler(manager,gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stopServer() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен!");
    }


    public static void main(String[] args) throws IOException {

        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.startServer();

        httpTaskServer.testTasks();
    }

    private void testTasks() {

        int epicId1 = manager.addNewEpic(new Epic("Первый эпик"));
        int epicId2 = manager.addNewEpic(new Epic("Второй эпик"));

        manager.addNewSubtask(new Subtask("Под первым эпиком1", "Описание 1", epicId1));
        manager.addNewSubtask(new Subtask("Под первым эпиком2", "Описание 2", epicId1));

        manager.addNewSubtask(new Subtask("Под вторым эпиком", "Описание 2", epicId2));

        manager.addNewTask(new Task("Задача 1","Описание 1"));
        manager.addNewTask(new Task("Задача 2","Описание 2"));
/*
        manager.addNewTask(new Task("Со временем", "Описание"
        ,LocalDateTime.of(2024,05,22,17,00)
        ,Duration.ofMinutes(120)));
*/

/*        Task task = new Task("Со временем", "Описание"
                ,LocalDateTime.of(2024,05,22,17,00)
                ,Duration.ofDays(2));

        String jsonTask = gson.toJson(task);

        JsonTestTaskModel jsonTaskModel = gson.fromJson(jsonTask, JsonTestTaskModel.class);

        System.out.println("123");*/
    }

}