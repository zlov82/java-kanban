package ru.yandex.javacourse.kanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void shouldSuccessLoadFile() {

        try {
            File file = File.createTempFile("tmp1JavaKanban",".tmp");

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            //Создаем задачи и, следовательно, происходит сохранение в файл БД
            fileBackedTaskManager.addNewTask(new Task("TaskTitle","Description"));
            fileBackedTaskManager.addNewTask(new Task("TaskTitle2","Description2"));
            int epicId = fileBackedTaskManager.addNewEpic(new Epic("EpicTitle"));
            fileBackedTaskManager.addNewSubtask(new Subtask("SubtaskTitle","Description",epicId));

            FileBackedTaskManager loadedFileManager = FileBackedTaskManager.loadFromFile(file);

            // Уникальный идентификатор менеджере загрузки в том же значении, что и в менеджере где было сохранение
            assertEquals(fileBackedTaskManager.uniqueId, loadedFileManager.uniqueId, "Уникальный идентификатор" +
                    "не уникален в менеджерах");

            //Проверяем количество задач в разных менеджерах
            assertEquals(fileBackedTaskManager.tasksDb.size(), loadedFileManager.tasksDb.size());
            assertEquals(fileBackedTaskManager.epicsDb.size(), loadedFileManager.epicsDb.size());
            assertEquals(fileBackedTaskManager.subtaskDb.size(), loadedFileManager.subtaskDb.size());

            //Дополнительная проверка эпика
            Epic savedEpic = fileBackedTaskManager.getEpicById(epicId);
            Epic loaderEpic = loadedFileManager.getEpicById(epicId);

            assertEquals(savedEpic.getSubtaskIdList().size(), loaderEpic.getSubtaskIdList().size());
            assertEquals(savedEpic.getSubtaskIdList().get(0), loaderEpic.getSubtaskIdList().get(0));

            file.delete();

        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }


    }

}