package ru.yandex.javacourse.kanban.httpServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.kanban.httpServer.adapters.DurationAdapter;
import ru.yandex.javacourse.kanban.httpServer.adapters.LocalDateTimeAdapter;
import ru.yandex.javacourse.kanban.httpServer.handlers.*;
import ru.yandex.javacourse.kanban.manager.Managers;
import ru.yandex.javacourse.kanban.manager.TaskManager;

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
        httpServer.createContext("/subtasks", new SubtaskHandler(manager,gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioretizedHandler(manager, gson));
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

    }

}