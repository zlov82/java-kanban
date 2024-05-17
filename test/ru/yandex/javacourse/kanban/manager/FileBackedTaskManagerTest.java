package ru.yandex.javacourse.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.tasks.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void setUp() {
        try {
            File file = File.createTempFile("tmp1JavaKanban", ".tmp");
            taskManager = new FileBackedTaskManager(file);
        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }
    }

    @Test
    void shouldSuccessLoadFile() {
        //Создаем задачи и, следовательно, происходит сохранение в файл БД
        taskManager.addNewTask(new Task("TaskTitle", "Description"));
        taskManager.addNewTask(new Task("TaskTitle2", "Description2"));
        int epicId = taskManager.addNewEpic(new Epic("EpicTitle"));
        taskManager.addNewSubtask(new Subtask("SubtaskTitle", "Description", epicId));
        int subtaskId = taskManager.addNewSubtask(new Subtask("SubtaskTitle2", "Description2", epicId));

        //Меняю статус подзадачи
        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        taskManager.updateSubtask(new Subtask(
                subtaskId,
                savedSubtask.getTitle(),
                savedSubtask.getDescription(),
                epicId,
                TaskStatus.IN_PROGRESS.toString()));

        FileBackedTaskManager loadedFileManager = FileBackedTaskManager.loadFromFile(taskManager.pathToFileDb);

        // Уникальный идентификатор менеджере загрузки в том же значении, что и в менеджере где было сохранение
        assertEquals(taskManager.uniqueId, loadedFileManager.uniqueId, "Уникальный идентификатор" +
                "не уникален в менеджерах");

        //Проверяем количество задач в разных менеджерах
        assertEquals(taskManager.tasksDb.size(), loadedFileManager.tasksDb.size());
        assertEquals(taskManager.epicsDb.size(), loadedFileManager.epicsDb.size());
        assertEquals(taskManager.subtaskDb.size(), loadedFileManager.subtaskDb.size());

        //Дополнительная проверка эпика
        Epic savedEpic = taskManager.getEpicById(epicId);
        Epic loaderEpic = loadedFileManager.getEpicById(epicId);

        assertEquals(savedEpic.getSubtaskIdList().size(), loaderEpic.getSubtaskIdList().size(),
                "Количество подзадач в самом эпике не совпадает с загруженным");
        assertEquals(savedEpic.getSubtaskIdList().get(0), loaderEpic.getSubtaskIdList().get(0));

        assertEquals(savedEpic.getStatus(), loaderEpic.getStatus(),
                "Не совпадают статусы у эпика");
    }


    @Test
    void shouldSuccessLoadFileWithDateAndDuration() {
        //Таск без времени
        taskManager.addNewTask(new Task("TaskTitle", "Description"));

        //Такс со датой и временем
        taskManager.addNewTask(new Task(
                "TaskTitle2",
                "Description2",
                LocalDateTime.now().minusDays(3),
                Duration.ofDays(2)
        ));

        int epicId = taskManager.addNewEpic(new Epic("EpicTitle"));

        //Подзадача с текущим временем
        taskManager.addNewSubtask(new Subtask(
                "SubtaskTitle",
                "Description",
                epicId,
                LocalDateTime.now().minusDays(10),
                Duration.ofDays(1)
        ));

        //Подзадача с вчерашним временем старта
        taskManager.addNewSubtask(new Subtask(
                "SubtaskTitle_2",
                "Description_2",
                epicId,
                LocalDateTime.now().minusDays(20),
                Duration.ofDays(1)
        ));

        //Подзадача без времени
        taskManager.addNewSubtask(new Subtask(
                "SubtaskTitle_2",
                "Description_2",
                epicId
        ));

        FileBackedTaskManager loadedFileManager = FileBackedTaskManager.loadFromFile(taskManager.pathToFileDb);

        //Проверяем количество задач в разных менеджерах
        assertEquals(taskManager.tasksDb.size(), loadedFileManager.tasksDb.size());
        assertEquals(taskManager.epicsDb.size(), loadedFileManager.epicsDb.size());
        assertEquals(taskManager.subtaskDb.size(), loadedFileManager.subtaskDb.size());

        //Дополнительная проверка эпика
        Epic savedEpic = taskManager.getEpicById(epicId);
        Epic loaderEpic = loadedFileManager.getEpicById(epicId);

        assertEquals(0, savedEpic.getDuration().compareTo(loaderEpic.getDuration()),
                "Текущая продолжительность у созданного и загруженного эпика НЕ совпадает");
        assertEquals(0, savedEpic.getStartTime().compareTo(loaderEpic.getStartTime()),
                "Дата начала сохраненного и загруженного эпиков НЕ совпадает");
    }
}