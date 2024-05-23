package ru.yandex.javacourse.kanban.httpServer.handlers;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.httpServer.HttpTaskServer;
import ru.yandex.javacourse.kanban.manager.InMemoryTaskManager;
import ru.yandex.javacourse.kanban.manager.TaskManager;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = server.getGson();

    EpicHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        List<Epic> ls = manager.getAllEpics();
        if (!ls.isEmpty()) {
            for (Epic epic : ls) {
                manager.deleteEpicById(epic.getId());
            }
        }
        server.startServer();
    }

    @AfterEach
    public void shutDown() {
        server.stopServer();
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicFromManager = manager.getAllEpics();

        assertNotNull(epicFromManager, "Эпик не создался");
        assertEquals(1, epicFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic1", epicFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicFromManager = manager.getAllEpics();
        int epicId = epicFromManager.get(0).getId();

        url = URI.create("http://localhost:8080/epics/"+epicId);
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        epicFromManager = manager.getAllEpics();
        assertEquals(0,epicFromManager.size());
    }

}