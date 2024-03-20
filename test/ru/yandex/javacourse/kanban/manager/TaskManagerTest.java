package ru.yandex.javacourse.kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;
import ru.yandex.javacourse.kanban.tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    static TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void beforeEach(){
        final int task1 = taskManager.addNewTask(new Task("Помыть посуду", "На этой неделе"));
        final int task2 = taskManager.addNewTask(new Task("Купить билеты на камчатку", "С 18 по 28 июня"));
        final int task3 = taskManager.addNewTask(new Task("Съездить к родителям", "Не забудь подарки"));
        final int task4 = taskManager.addNewTask(new Task("Постричься", "Совсем зарос"));

        final int epic1 = taskManager.addNewEpic(new Epic("Загран паспорт"));
        final int epic2 = taskManager.addNewEpic(new Epic("Обучение Java 5 спринт"));

        final int subtask1 = taskManager.addNewSubtask(new Subtask("Заявление на госуслугах", "Всполмнить пароль", epic1));
        final int subtask2 = taskManager.addNewSubtask(new Subtask("Госпошлина", "Оплатить",epic1));
        final int subtask3 = taskManager.addNewSubtask(new Subtask("Запись в МФЦ", "не профукай",epic1));

        final int subtask4 = taskManager.addNewSubtask(new Subtask("Госпошлина", "Оплатить",epic2));
        final int subtask5 = taskManager.addNewSubtask(new Subtask("Финальное задание спринта", "",epic2)); // 11
        final int subtask6 = taskManager.addNewSubtask(new Subtask("Покрыть всё тестами", "",epic2)); // 12
    }

    @Test
    void deleteTask() {
        final List<Task> savedTasks = taskManager.getAllTasks();
        int savedTasksCount = savedTasks.size();

        taskManager.deleteTaskById(savedTasks.get(0).getId());
        final List<Task> updatedTasks = taskManager.getAllTasks();

        assertEquals(updatedTasks.size(),savedTasksCount -1, "Таска не удалилась");
    }

    @Test
    void deleteSubtask() {
        final List<Subtask> savedSubtasks = taskManager.getAllSubTasks();
        int savedSubtasksCount = savedSubtasks.size();

        taskManager.deleteSubtaskById(savedSubtasks.get(0).getId());
        final List<Subtask> updatedSubtasks = taskManager.getAllSubTasks();

        assertEquals(savedSubtasksCount -1, updatedSubtasks.size(), "Подзадача не удалилась");
    }

    @Test
    void deleteEpic() {
        final List<Epic> savedEpics = taskManager.getAllEpics();
        final List<Subtask> savedSubtasks = taskManager.getAllSubTasks();
        final List<Subtask> savedEpicSubtask = taskManager.getAllTasksByEpic(savedEpics.get(0).getId());
        final int savedEpicsSize = savedEpics.size();
        int countDeletedSubtaskInEpic = savedEpicSubtask.size();

        assertTrue(countDeletedSubtaskInEpic > 0,"В эпике нет подзадач");

        taskManager.deleteEpicById(savedEpics.get(0).getId());

        final List<Epic> updatedEpics = taskManager.getAllEpics();
        final List<Subtask> updatedSubtask = taskManager.getAllSubTasks();

        assertEquals(updatedEpics.size(), savedEpicsSize -1 , "Эпик не удалён");
        assertEquals(savedSubtasks.size() - countDeletedSubtaskInEpic, updatedSubtask.size(), "Не удалены подзадачи");
    }


    @Test
    void updateEpicStatus() {
        final List<Epic> savedEpics = taskManager.getAllEpics();
        final Epic epic = savedEpics.get(0);

        assertTrue(epic.getStatus().equals(TaskStatus.NEW), "Статус эпика не Новый");

        final List<Subtask> savedSubtask = taskManager.getAllTasksByEpic(epic.getId());
        assertTrue(savedSubtask.size() > 0, "Эпик не содержит задач");

        for (Subtask subtask : savedSubtask) {
            Subtask updateSubtask = new Subtask(subtask.getId(), subtask.getTitle(), subtask.getDescription(),
                    subtask.getEpicId(),"done");
            taskManager.updateSubtask(updateSubtask);
        }

        final Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(updatedEpic.getStatus().equals(TaskStatus.DONE), "Эпик не перешёл на конечный статус");

    }

}