package ru.yandex.javacourse.kanban.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.manager.Managers;
import ru.yandex.javacourse.kanban.manager.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    TaskManager taskManager = Managers.getDefault();


    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        assertEquals(TaskStatus.NEW,savedTask.getStatus(), "У новой задачи статус != NEW");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        // обновляем таску - новое описание, новый статус
        final String newDescription = "new_description";

        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        Task updatedTask = new Task(savedTask.getId(),savedTask.title, newDescription, "done");
        taskManager.updateTask(updatedTask);

        final Task updatedSavedTask = taskManager.getTaskById(taskId);

        assertEquals(TaskStatus.DONE, updatedSavedTask.getStatus(), "Не совпадает обновленный статус");
        assertEquals(newDescription, updatedSavedTask.getDescription(), "Не верное описание у новой");
    }



}