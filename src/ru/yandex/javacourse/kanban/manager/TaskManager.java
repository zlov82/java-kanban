package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;

import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);

    void updateTask(Task newTask);

    List<Task> getAllTasks();

    Task getTaskById(int taskId);

    void deleteTaskById(int id);

    void deleteAllTasks();

    int addNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    List<Epic> getAllEpics();

    Epic getEpicById(int epicId);

    Integer addNewSubtask(Subtask subtask);

    Subtask getSubtaskById(int subTaskId);

    List<Subtask> getAllTasksByEpic(int epicId);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int subtaskId);

    List<Subtask> getAllSubTasks();

    List<Task> getHistory();

}
