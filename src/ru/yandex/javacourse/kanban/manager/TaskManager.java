package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;
import ru.yandex.javacourse.kanban.tasks.TaskStatus;
import ru.yandex.javacourse.kanban.tasks.Epic;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int uniqueId = 0;
    private final HashMap<Integer, Task> tasksDb;
    private final HashMap<Integer, Epic> epicsDb;
    private final HashMap<Integer, Subtask> subtaskDb;

    public TaskManager() {
        tasksDb = new HashMap<>();
        epicsDb = new HashMap<>();
        subtaskDb = new HashMap<>();
    }

    /********* МЕТОДЫ ОБЫЧНЫХ ЗАДАЧ *********/
    public int addNewTask(Task task) {
        task.setId(getUniqueId());
        tasksDb.put(task.getId(), task);
        return task.getId();
    }

    public void updateTask(Task newTask) {
        int id = newTask.getId();
        Task savedTask = tasksDb.get(id);
        if (savedTask != null) {
            tasksDb.put(id, newTask);
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasksDb.values());
    }

    public Task getTaskById(int taskId) {
        return tasksDb.get(taskId);
    }

    public void deleteTaskById(int id) {
        tasksDb.remove(id);
    }

    public void deleteAllTasks() {
        tasksDb.clear();
    }

    /********* ЭПИКИ *********/
    public int addNewEpic(Epic epic) {
        epic.setId(getUniqueId());
        epicsDb.put(epic.getId(), epic);
        return epic.getId();
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        Epic savedEpic = epicsDb.get(id);
        if (savedEpic != null) {
            savedEpic.setTitle(epic.getTitle());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    public void deleteEpicById(int epicId) {
        Epic savedEpic = epicsDb.get(epicId);
        if (savedEpic != null) {
            ArrayList<Integer> subtaskIdList = savedEpic.getSubtaskIdList();
            for (int subtaskId : subtaskIdList) {
                subtaskDb.remove(subtaskId);
            }
            epicsDb.remove(savedEpic.getId());
        }
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicsDb.values());
    }

    public Epic getEpicById(int epicId) {
        return epicsDb.get(epicId);
    }




    /********* ПОДЗАДАЧИ *********/
    public Integer addNewSubtask(Subtask subtask) {
        Epic savedEpic = epicsDb.get(subtask.getEpicId());
        if (savedEpic != null) {
            final int subtaskId = getUniqueId();
            subtask.setId(subtaskId);
            subtaskDb.put(subtaskId, subtask);
            savedEpic.addNewSubtask(subtaskId);
            updateEpicStatus(savedEpic.getId());
            return subtaskId;
        }
        return null;
    }

    public Subtask getSubtaskById(int subTaskId) {
        return subtaskDb.get(subTaskId);
    }

    public ArrayList<Subtask> getAllTasksByEpic(int epicId) {
        Epic savedEpic = epicsDb.get(epicId);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (savedEpic != null) {
            ArrayList<Integer> subtasksIds = savedEpic.getSubtaskIdList();
            for (int subtaskId : subtasksIds) {
                subtasks.add(subtaskDb.get(subtaskId));
            }
        }
        return subtasks;
    }


    public void updateSubtask(Subtask subtask) {
        Epic savedEpic = epicsDb.get(subtask.getEpicId());
        if (savedEpic == null) {
            return;
        }
        Subtask savedSubtask = subtaskDb.get(subtask.getId());
        if (savedSubtask ==null) {
            return;
        }
        subtaskDb.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask deletedSubtask = subtaskDb.remove(subtaskId);
        if (deletedSubtask != null) {
            Epic savedEpic = epicsDb.get(subtaskId);
            savedEpic.deleteSubtask(subtaskId);
            subtaskDb.remove(subtaskId);
            updateEpicStatus(savedEpic.getId());
        }
    }

    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtaskDb.values());
    }

    private int getUniqueId() {
        return ++uniqueId;
    }

    private void updateEpicStatus(int epicId) {
        Epic savedEpic = epicsDb.get(epicId);
        if (savedEpic != null) {
            ArrayList<Integer> subtaskIds = savedEpic.getSubtaskIdList();

            TaskStatus epicStatus;
            boolean isNew = false;
            boolean isInProcess = false;
            boolean isDone = false;

            for (int subtaskId : subtaskIds) {
                Subtask subtask = subtaskDb.get(subtaskId);
                if (subtask.getStatus() == TaskStatus.NEW) {
                    isNew = true;
                } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                    isInProcess = true;
                } else {
                    isDone = true;
                }
            }

            // Вычисление статуса
            if (isDone && !isNew && !isInProcess) {
                epicStatus = TaskStatus.DONE;
            } else if (isInProcess || isDone) {
                epicStatus = TaskStatus.IN_PROGRESS;
            } else {
                epicStatus = TaskStatus.NEW;
            }

            // System.out.println(epicStatus); /*проверка выставления статуса эпика*/
            savedEpic.setStatus(epicStatus);
        }
    }
}
