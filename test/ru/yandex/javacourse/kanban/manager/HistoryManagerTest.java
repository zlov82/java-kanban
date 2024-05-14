package ru.yandex.javacourse.kanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;
import ru.yandex.javacourse.kanban.tasks.TaskStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    @Test
    void shouldBeHistory() {
        TaskManager taskManager = Managers.getDefault();
        addManyTasks(taskManager);

        List<Task> tasks = taskManager.getAllTasks();
        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllSubTasks();

        final int countAllTasks = tasks.size() + epics.size() + subtasks.size();

        HistoryManager historyManager = Managers.getDefaultHistory();

        for (Task task : tasks) {
            historyManager.add(task);
        }
        for (Epic epic : epics) {
            historyManager.add(epic);
        }
        for (Subtask subtask : subtasks) {
            historyManager.add(subtask);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(countAllTasks, history.size(), "История не равна количеству запрошенных задач");
    }


    @Test
    void inHistoryShouldChangeTask() {
        TaskManager taskManager = Managers.getDefault();

        final String title = "TITLE";
        final String updatedTitle = "NEW_TITLE";
        final String description = "DESCRIPTION";

        final int taskId = taskManager.addNewTask(new Task(title, description));
        taskManager.getTaskById(taskId); //в историю попал таск с title = "TITLE"

        List<Task> historyList = taskManager.getHistory();
        Task historyTask = historyList.get(0);
        assertEquals(title, historyTask.getTitle(), "Ожидалось другое название задачи");

        Task updatedTask = new Task(taskId, updatedTitle, "NEW_DESCRIPTION");
        taskManager.updateTask(updatedTask);
        updatedTask = taskManager.getTaskById(taskId);

        assertEquals("NEW_TITLE", updatedTask.getTitle(), "Задача не обновилась");

        historyList.clear();
        historyList = taskManager.getHistory();

        assertTrue(historyList.size() == 1, "Старая история не удалилась");

        historyTask = historyList.get(0);

        assertEquals(updatedTitle, historyTask.getTitle(), "В историю попала обновленная задача");

    }

    @Test
    void inHistoryShouldNotChangeEpicAndSubtask() {
        TaskManager taskManager = Managers.getDefault();

        final String titleEpic = "TITLE_EPIC";
        final String titleSubtask = "TITLE_SUBTASK";
        final String description = "DESCRIPTION";

        final int epicId = taskManager.addNewEpic(new Epic(titleEpic));
        final int subtaskId = taskManager.addNewSubtask(new Subtask(titleSubtask, description, epicId));

        taskManager.getEpicById(epicId);        //Эпик попал в историю
        taskManager.getSubtaskById(subtaskId);  //Подзадача эпика попала в историю

        Epic updatedEpic = new Epic(epicId, "NEW_TITLE_EPIC");
        taskManager.updateEpic(updatedEpic);
        Subtask updatedSubtask = new Subtask(subtaskId, "NEW_TITLE,SUBTASK", "NEW_TITLE,DESCRIPTION"
                , epicId, TaskStatus.IN_PROGRESS.toString());
        taskManager.updateSubtask(updatedSubtask);

        final List<Task> history = taskManager.getHistory();

        assertEquals(titleEpic, history.get(0).getTitle(), "В истории обновленный эпик");
        assertEquals(titleSubtask, history.get(1).getTitle(), "В истории обновленная подзадача");
    }

    @Test
    void ShouldBeUniqueIdsInHistory() {
        TaskManager taskManager = Managers.getDefault();
        addManyTasks(taskManager);

        HistoryManager historyManager = Managers.getDefaultHistory();
        List<Task> tasks = taskManager.getAllTasks();

        for (int i = 0; i < 2; i++) { // запрашиваем все таски два раза
            for (Task task : tasks) {
                historyManager.add(task);
            }
        }

        List<Task> historyList = historyManager.getHistory();

        assertTrue(tasks.size() == historyList.size(), "Количество задач не совпадает с количеством" +
                " в истории");

        Set<Integer> taskNumbers = new HashSet<>();
        for (Task task : historyList) {
            taskNumbers.add(task.getId());
        }

        assertTrue(historyList.size() == taskNumbers.size(), "Провалена проверка на уникальность" +
                " идентификаторов в истории");
    }


    void addManyTasks(TaskManager taskManager) {
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