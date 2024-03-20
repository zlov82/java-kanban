package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    int addNewTask(Task task);

    void updateTask(Task newTask);

    ArrayList<Task> getAllTasks();

    Task getTaskById(int taskId);

    void deleteTaskById(int id);

    void deleteAllTasks();

    int addNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    ArrayList<Epic> getAllEpics();

    Epic getEpicById(int epicId);

    Integer addNewSubtask(Subtask subtask);

    Subtask getSubtaskById(int subTaskId);

    ArrayList<Subtask> getAllTasksByEpic(int epicId);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int subtaskId);

    ArrayList<Subtask> getAllSubTasks();

    HistoryManager getHistory();

}
