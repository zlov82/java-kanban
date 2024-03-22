package ru.yandex.javacourse.kanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;
import ru.yandex.javacourse.kanban.tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    @Test
    void shouldBe10History() {
        TaskManager taskManager = Managers.getDefault();
        addManyTasks(taskManager);

        List<Task> tasks = taskManager.getAllTasks();
        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllSubTasks();

        final int countAllTasks = tasks.size() + epics.size() + subtasks.size();
        assertTrue(countAllTasks > 10, "Всего меньше 10 задач");

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
        assertTrue(history.size() == 10, "История не равна 10 задачам");
    }


    @Test
    void inHistoryShouldNotChangeTask() {
        TaskManager taskManager = Managers.getDefault();

        final String title = "TITLE";
        final String description = "DESCRIPTION";

        final int taskId = taskManager.addNewTask(new Task(title, description));
        taskManager.getTaskById(taskId); //в историю попал таск с title = "TITLE"

        Task updatedTask = new Task(taskId, "NEW_TITLE", "NEW_DESCRIPTION");
        taskManager.updateTask(updatedTask);
        updatedTask = taskManager.getTaskById(taskId);

        assertEquals("NEW_TITLE", updatedTask.getTitle(), "Задача не обновилась");

        List<Task> historyList = taskManager.getHistory();
        Task historyTask = historyList.get(0);

        assertEquals(title, historyTask.getTitle(), "В историю попала обновленная задача");

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