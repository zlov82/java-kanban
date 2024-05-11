package ru.yandex.javacourse.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.exceptions.TaskCrossTimeException;
import ru.yandex.javacourse.kanban.tasks.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
            int subtaskId = fileBackedTaskManager.addNewSubtask(new Subtask("SubtaskTitle2","Description2",epicId));

            //Меняю статус подзадачи
            Subtask savedSubtask = fileBackedTaskManager.getSubtaskById(subtaskId);
            fileBackedTaskManager.updateSubtask(new Subtask(
                    subtaskId,
                    savedSubtask.getTitle(),
                    savedSubtask.getDescription(),
                    epicId,
                    TaskStatus.IN_PROGRESS.toString()));

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

            assertEquals(savedEpic.getSubtaskIdList().size(), loaderEpic.getSubtaskIdList().size(),
                    "Количество подзадач в самом эпике не совпадает с загруженным");
            assertEquals(savedEpic.getSubtaskIdList().get(0), loaderEpic.getSubtaskIdList().get(0));

            assertEquals(savedEpic.getStatus(), loaderEpic.getStatus(),
                    "Не совпадают статусы у эпика");

            file.delete();

        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }

    }


    @Test
    void shouldSuccessLoadFileWithDateAndDuration() {
        try {
            File file = File.createTempFile("tmp2JavaKanban",".tmp");

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            //Таск без времени
            fileBackedTaskManager.addNewTask(new Task("TaskTitle","Description"));

            //Такс со датой и временем
            fileBackedTaskManager.addNewTask(new Task(
                    "TaskTitle2",
                    "Description2",
                    LocalDateTime.now().minusDays(3),
                    Duration.ofDays(2)
                    ));

            int epicId = fileBackedTaskManager.addNewEpic(new Epic("EpicTitle"));

            //Подзадача с текущим временем
            fileBackedTaskManager.addNewSubtask(new Subtask(
                    "SubtaskTitle",
                    "Description",
                    epicId,
                    LocalDateTime.now().minusDays(10),
                    Duration.ofDays(1)
                ));

            //Подзадача с вчерашним временем старта
            fileBackedTaskManager.addNewSubtask(new Subtask(
                    "SubtaskTitle_2",
                    "Description_2",
                    epicId,
                    LocalDateTime.now().minusDays(20),
                    Duration.ofDays(1)
            ));

            //Подзадача без времени
            fileBackedTaskManager.addNewSubtask(new Subtask(
                    "SubtaskTitle_2",
                    "Description_2",
                    epicId
            ));

            FileBackedTaskManager loadedFileManager = FileBackedTaskManager.loadFromFile(file);

            //Проверяем количество задач в разных менеджерах
            assertEquals(fileBackedTaskManager.tasksDb.size(), loadedFileManager.tasksDb.size());
            assertEquals(fileBackedTaskManager.epicsDb.size(), loadedFileManager.epicsDb.size());
            assertEquals(fileBackedTaskManager.subtaskDb.size(), loadedFileManager.subtaskDb.size());

            //Дополнительная проверка эпика
            Epic savedEpic = fileBackedTaskManager.getEpicById(epicId);
            Epic loaderEpic = loadedFileManager.getEpicById(epicId);

            assertEquals(0,savedEpic.getDuration().compareTo(loaderEpic.getDuration()),
                    "Текущая продолжительность у созданного и загруженного эпика НЕ совпадает");
            assertEquals(0, savedEpic.getStartTime().compareTo(loaderEpic.getStartTime()),
                    "Дата начала сохраненного и загруженного эпиков НЕ совпадает");

            file.delete();

        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }

    }


    @Test
    void sortedTaskTest() {
        TaskManager fileBackedTaskManager = Managers.getDefault();

        //Таск без времени
        fileBackedTaskManager.addNewTask(new Task("TaskTitle","Description"));

        //Такс со датой и временем (по времени будет самым последним в списке)
        int taskIdLastInSort = fileBackedTaskManager.addNewTask(new Task(
                "TaskTitle2",
                "Description2",
                LocalDateTime.now(),
                Duration.ofDays(1)
        ));

        //Обновляем таску (только статус)
        Task savedTask = fileBackedTaskManager.getTaskById(taskIdLastInSort);
        fileBackedTaskManager.updateTask(new Task(
                taskIdLastInSort,
                savedTask.getTitle(),
                savedTask.getDescription(),
                TaskStatus.IN_PROGRESS.toString(),
                savedTask.getStartTime(),
                savedTask.getDuration()
        ));


        int epicId = fileBackedTaskManager.addNewEpic(new Epic("EpicTitle"));

        //Подзадача с текущим временем (будет вторая в списке сортировки по времени)
        int taskIdMediumInSort = fileBackedTaskManager.addNewSubtask(new Subtask(
                "SubtaskTitle",
                "Description",
                epicId,
                LocalDateTime.now().minusDays(3),
                Duration.ofHours(1)
        ));

        //Подзадача, самая перая в списке
        int taskIdFirstInSearch = fileBackedTaskManager.addNewSubtask(new Subtask(
                "SubtaskTitle_2",
                "Description_2",
                epicId,
                LocalDateTime.now().minusDays(10),
                Duration.ofDays(3)
        ));

        //Подзадача без времени (не должна войти в список)
        fileBackedTaskManager.addNewSubtask(new Subtask(
                "SubtaskTitle_2",
                "Description_2",
                epicId
        ));

        List<Task> sortedTasks = fileBackedTaskManager.getPrioritizedTasks();

        //В sortedTasks должно быть 3 элемента
        assertEquals(3, sortedTasks.size());

        //Проверка, что в sortedTasks обновленная таска, а не старая
        assertTrue(sortedTasks.get(2).getStatus().equals(TaskStatus.IN_PROGRESS));

        //Проверка оставшегося порядка
        assertTrue((sortedTasks.get(0).getId() == taskIdFirstInSearch && sortedTasks.get(1).getId() == taskIdMediumInSort),
                "В сортированном списке нарушена последовательность");

    }

    @Test
    void crossStartTimeTest() {

        TaskManager manager = Managers.getDefault();

        //Такс со датой и временем
        int firstTaskId = manager.addNewTask(new Task(
                "TaskTitle",
                "Description",
                LocalDateTime.of(2020, 10, 01, 12, 0),
                Duration.ofMinutes(30)
        ));

        //Пересекающаяся задача
        Assertions.assertThrows(TaskCrossTimeException.class, () -> {
            manager.addNewTask(new Task(
                    "TaskTitle2",
                    "Description2",
                    LocalDateTime.of(2020, 10, 01, 11, 45),
                    Duration.ofMinutes(30)
            ));
        }, "Добавление задачи с пересекающимся временем исполнения должна приводить к исключению");

        // Не пересекающая задача
        Assertions.assertDoesNotThrow( () -> {
            manager.addNewTask(new Task(
                    "TaskTitle3",
                    "Description3",
                    LocalDateTime.of(2020,10,01,11,00),
                    Duration.ofMinutes(45)
            ));
        }, "Добавление задачи не должно вызывать исключения");
    }

}