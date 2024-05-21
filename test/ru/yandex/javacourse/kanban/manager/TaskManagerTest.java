package ru.yandex.javacourse.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.exceptions.TaskCrossTimeException;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;
import ru.yandex.javacourse.kanban.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;


    @Test
    void deleteSubtask() {
        addManyTasks();
        final List<Subtask> savedSubtasks = taskManager.getAllSubTasks();
        int savedSubtasksCount = savedSubtasks.size();

        taskManager.deleteSubtaskById(savedSubtasks.get(0).getId());
        final List<Subtask> updatedSubtasks = taskManager.getAllSubTasks();

        assertEquals(savedSubtasksCount - 1, updatedSubtasks.size(), "Подзадача не удалилась");
    }

    @Test
    void deleteEpic() {
        addManyTasks();
        final List<Epic> savedEpics = taskManager.getAllEpics();
        final List<Subtask> savedSubtasks = taskManager.getAllSubTasks();
        final List<Subtask> savedEpicSubtask = taskManager.getAllTasksByEpic(savedEpics.get(0).getId());
        final int savedEpicsSize = savedEpics.size();
        int countDeletedSubtaskInEpic = savedEpicSubtask.size();

        assertTrue(countDeletedSubtaskInEpic > 0, "В эпике нет подзадач");

        taskManager.deleteEpicById(savedEpics.get(0).getId());

        final List<Epic> updatedEpics = taskManager.getAllEpics();
        final List<Subtask> updatedSubtask = taskManager.getAllSubTasks();

        assertEquals(updatedEpics.size(), savedEpicsSize - 1, "Эпик не удалён");
        assertEquals(savedSubtasks.size() - countDeletedSubtaskInEpic, updatedSubtask.size(), "Не удалены подзадачи");
    }


    @Test
    void updateEpicStatus() {
        addManyTasks();
        final List<Epic> savedEpics = taskManager.getAllEpics();
        final Epic epic = savedEpics.get(0);

        assertTrue(epic.getStatus().equals(TaskStatus.NEW), "Статус эпика не Новый");

        final List<Subtask> savedSubtask = taskManager.getAllTasksByEpic(epic.getId());
        assertTrue(savedSubtask.size() > 0, "Эпик не содержит задач");

        for (Subtask subtask : savedSubtask) {
            Subtask updateSubtask = new Subtask(subtask.getId(), subtask.getTitle(), subtask.getDescription(),
                    subtask.getEpicId(), "done");
            taskManager.updateSubtask(updateSubtask);
        }

        final Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(updatedEpic.getStatus().equals(TaskStatus.DONE), "Эпик не перешёл на конечный статус");

    }


    @Test
    void deleteTask() {
        addManyTasks();
        final List<Task> savedTasks = taskManager.getAllTasks();
        int savedTasksCount = savedTasks.size();

        taskManager.deleteTaskById(savedTasks.get(0).getId());
        final List<Task> updatedTasks = taskManager.getAllTasks();

        assertEquals(updatedTasks.size(), savedTasksCount - 1, "Таска не удалилась");
    }

    @Test
    void sortedTaskTest() {

        //Таск без времени
        taskManager.addNewTask(new Task("TaskTitle", "Description"));

        //Такс со датой и временем (по времени будет самым последним в списке)
        int taskIdLastInSort = taskManager.addNewTask(new Task(
                "TaskTitle2",
                "Description2",
                LocalDateTime.now(),
                Duration.ofDays(1)
        ));

        //Обновляем таску (только статус)
        Task savedTask = taskManager.getTaskById(taskIdLastInSort);
        taskManager.updateTask(new Task(
                taskIdLastInSort,
                savedTask.getTitle(),
                savedTask.getDescription(),
                TaskStatus.IN_PROGRESS.toString(),
                savedTask.getStartTime(),
                savedTask.getDuration()
        ));


        int epicId = taskManager.addNewEpic(new Epic("EpicTitle"));

        //Подзадача с текущим временем (будет вторая в списке сортировки по времени)
        int taskIdMediumInSort = taskManager.addNewSubtask(new Subtask(
                "SubtaskTitle",
                "Description",
                epicId,
                LocalDateTime.now().minusDays(3),
                Duration.ofHours(1)
        ));

        //Подзадача, самая перая в списке
        int taskIdFirstInSearch = taskManager.addNewSubtask(new Subtask(
                "SubtaskTitle_2",
                "Description_2",
                epicId,
                LocalDateTime.now().minusDays(10),
                Duration.ofDays(3)
        ));

        //Подзадача без времени (не должна войти в список)
        taskManager.addNewSubtask(new Subtask(
                "SubtaskTitle_2",
                "Description_2",
                epicId
        ));

        List<Task> sortedTasks = taskManager.getPrioritizedTasks();

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

        //Такс со датой и временем
        int firstTaskId = taskManager.addNewTask(new Task(
                "TaskTitle",
                "Description",
                LocalDateTime.of(2020, 10, 01, 12, 0),
                Duration.ofMinutes(30)
        ));

        //Пересекающаяся задача
        Assertions.assertThrows(TaskCrossTimeException.class, () -> {
            taskManager.addNewTask(new Task(
                    "TaskTitle2",
                    "Description2",
                    LocalDateTime.of(2020, 10, 01, 11, 45),
                    Duration.ofMinutes(30)
            ));
        }, "Добавление задачи с пересекающимся временем исполнения должна приводить к исключению");

        // Не пересекающая задача
        Assertions.assertDoesNotThrow(() -> {
            taskManager.addNewTask(new Task(
                    "TaskTitle3",
                    "Description3",
                    LocalDateTime.of(2020, 10, 01, 11, 00),
                    Duration.ofMinutes(45)
            ));
        }, "Добавление задачи не должно вызывать исключения");
    }

    @Test
    void crossStartTimeTest2() {
        int task8_9 = taskManager.addNewTask(new Task(
                "TaskTitle2",
                "Description2",
                LocalDateTime.of(2024,05,21,8,0),
                Duration.ofMinutes(60)
        ));

        int task9_10 = taskManager.addNewTask(new Task(
                "TaskTitle2",
                "Description2",
                LocalDateTime.of(2024,05,21,9,0),
                Duration.ofMinutes(60)
        ));
    }

    void addManyTasks() {
        final int task1 = taskManager.addNewTask(new Task("Помыть посуду", "На этой неделе"));
        final int task2 = taskManager.addNewTask(new Task("Купить билеты на камчатку", "С 18 по 28 июня"));
        final int task3 = taskManager.addNewTask(new Task("Съездить к родителям", "Не забудь подарки"));
        final int task4 = taskManager.addNewTask(new Task("Постричься", "Совсем зарос"));

        final int epic1 = taskManager.addNewEpic(new Epic("Загран паспорт"));
        final int epic2 = taskManager.addNewEpic(new Epic("Обучение Java 5 спринт"));

        final int subtask1 = taskManager.addNewSubtask(new Subtask("Заявление на госуслугах", "Всполмнить пароль", epic1));
        final int subtask2 = taskManager.addNewSubtask(new Subtask("Госпошлина", "Оплатить", epic1));
        final int subtask3 = taskManager.addNewSubtask(new Subtask("Запись в МФЦ", "не профукай", epic1));

        final int subtask4 = taskManager.addNewSubtask(new Subtask("Госпошлина", "Оплатить", epic2));
        final int subtask5 = taskManager.addNewSubtask(new Subtask("Финальное задание спринта", "", epic2)); // 11
        final int subtask6 = taskManager.addNewSubtask(new Subtask("Покрыть всё тестами", "", epic2)); // 12
    }


}
