package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Epic;
import ru.yandex.javacourse.kanban.tasks.Subtask;
import ru.yandex.javacourse.kanban.tasks.Task;
import ru.yandex.javacourse.kanban.tasks.TaskStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int uniqueId = 0;
    protected final Map<Integer, Task> tasksDb;
    protected final Map<Integer, Epic> epicsDb;
    protected final Map<Integer, Subtask> subtaskDb;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasksDb = new HashMap<>();
        epicsDb = new HashMap<>();
        subtaskDb = new HashMap<>();
    }

    /********* МЕТОДЫ ОБЫЧНЫХ ЗАДАЧ *********/
    @Override
    public int addNewTask(Task task) {
        if (task.getId() == 0) {
            task.setId(getUniqueId());
        }
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
        if (epic.getId() == 0) {
            epic.setId(getUniqueId());
        }
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
            int subtaskId;
            boolean updateEpicStatus = false;
            if (subtask.getId() == 0) {
                subtaskId = getUniqueId();
                updateEpicStatus = true;
            } else {
                subtaskId = subtask.getId();
            }
            subtask.setId(subtaskId);
            subtaskDb.put(subtaskId, subtask);
            savedEpic.addNewSubtask(subtaskId);
            if (updateEpicStatus) {
                updateEpicStatus(savedEpic.getId());
                updateEpicDataAndDuration(savedEpic.getId());
            }
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
        if (savedSubtask == null) {
            return;
        }
        subtaskDb.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        updateEpicDataAndDuration(subtask.getEpicId());
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask deletedSubtask = subtaskDb.remove(subtaskId);
        if (deletedSubtask != null) {
            Epic savedEpic = epicsDb.get(deletedSubtask.getEpicId());
            savedEpic.deleteSubtask(subtaskId);
            updateEpicStatus(savedEpic.getId());
            updateEpicDataAndDuration(savedEpic.getId());
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

            /* Расчет статуса эпика */
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

    protected void updateEpicDataAndDuration(int epicId) {
        Epic savedEpic = epicsDb.get(epicId);
        if (savedEpic != null) {
            ArrayList<Integer> subtaskIds = savedEpic.getSubtaskIdList();

            LocalDateTime epicStartTime;
            if (savedEpic.getStartTime() != null) {
                epicStartTime = savedEpic.getStartTime();
            } else {
                epicStartTime = LocalDateTime.MAX;
            }

            Duration epicDuration = Duration.ZERO;
            for (int subtaskId : subtaskIds) {
                Subtask subtask = subtaskDb.get(subtaskId);
                Optional<LocalDateTime> startTime = Optional.ofNullable(subtask.getStartTime());
                Optional<Duration> duration = Optional.ofNullable(subtask.getDuration());
                if (startTime.isPresent()) {
                    if (startTime.get().isBefore(epicStartTime)) {
                        epicStartTime = startTime.get();
                    }
                }
                if (duration.isPresent()) {
                     epicDuration = epicDuration.plus(duration.get());
                }
            }
            if (epicStartTime.isAfter(LocalDateTime.MIN)){
                savedEpic.setStartTime(epicStartTime);
            }
            if (epicDuration.compareTo(Duration.ZERO) != 0) {
                savedEpic.setDuration(epicDuration);
            }
        }
    }

}
