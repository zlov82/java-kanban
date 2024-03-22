package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;
import ru.yandex.javacourse.kanban.tasks.TaskStatus;
import ru.yandex.javacourse.kanban.tasks.Epic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int uniqueId = 0;
    private final Map<Integer, Task> tasksDb;
    private final Map<Integer, Epic> epicsDb;
    private final Map<Integer, Subtask> subtaskDb;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasksDb = new HashMap<>();
        epicsDb = new HashMap<>();
        subtaskDb = new HashMap<>();
    }

    /********* МЕТОДЫ ОБЫЧНЫХ ЗАДАЧ *********/
    @Override
    public int addNewTask(Task task) {
        task.setId(getUniqueId());
        tasksDb.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task newTask) {
        int id = newTask.getId();
        Task savedTask = tasksDb.get(id);
        if (savedTask != null) {
            tasksDb.put(id, newTask);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasksDb.values());
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasksDb.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        tasksDb.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasksDb.clear();
    }

    /********* ЭПИКИ *********/
    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(getUniqueId());
        epicsDb.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        int savedEpicId = epic.getId();
        Epic savedEpic = epicsDb.get(savedEpicId);
        if (savedEpic != null) {
            savedEpic.setTitle(epic.getTitle());
            savedEpic.setDescription(epic.getDescription());
            // статус тут не изменяется (только при обновлении сабтасков)
            epicsDb.put(savedEpicId, savedEpic);
        }
    }

    @Override
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

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicsDb.values());
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epicsDb.get(epicId);
        historyManager.add(new Epic(epic)); //в историю помещается дубль эпика
        return epic;
    }

    /********* ПОДЗАДАЧИ *********/
    @Override
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

    @Override
    public Subtask getSubtaskById(int subTaskId) {
        Subtask subtask = subtaskDb.get(subTaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
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


    @Override
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

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask deletedSubtask = subtaskDb.remove(subtaskId);
        if (deletedSubtask != null) {
            Epic savedEpic = epicsDb.get(deletedSubtask.getEpicId());
            savedEpic.deleteSubtask(subtaskId);
            updateEpicStatus(savedEpic.getId());
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtaskDb.values());
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
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

            savedEpic.setStatus(epicStatus);
        }
    }

}
