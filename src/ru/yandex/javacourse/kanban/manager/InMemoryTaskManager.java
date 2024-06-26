package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.exceptions.TaskCrossTimeException;
import ru.yandex.javacourse.kanban.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int uniqueId = 0;
    protected final Map<Integer, Task> tasksDb;
    protected final Map<Integer, Epic> epicsDb;
    protected final Map<Integer, Subtask> subtaskDb;

    //сортированные по времени начала задачи и подзадачи (без эпиков)
    protected final Set<Task> prioritizedTasks;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasksDb = new HashMap<>();
        epicsDb = new HashMap<>();
        subtaskDb = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    /********* МЕТОДЫ ОБЫЧНЫХ ЗАДАЧ *********/
    @Override
    public int addNewTask(Task task) {
        if (task.getId() == 0) {
            task.setId(getUniqueId());
        }

        //Проверка на пересечение по времени
        if (isCrossedTimeTask(task)) {
            throw new TaskCrossTimeException("Задача " + task + "не добавлена. Пересечение времени");
        }

        tasksDb.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        return task.getId();
    }

    @Override
    public void updateTask(Task newTask) {
        int id = newTask.getId();
        Task savedTask = tasksDb.get(id);
        if (savedTask != null) {
            tasksDb.put(id, newTask);

            if (isCrossedTimeTask(newTask)) {
                throw new TaskCrossTimeException("Задача " + newTask + "не добавлена. Пересечение времени");
            }

            if (newTask.getStartTime() != null) {
                prioritizedTasks.remove(savedTask);
                prioritizedTasks.add(newTask);
            }
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
        Task deletedTask = tasksDb.remove(id);
        prioritizedTasks.remove(deletedTask);

    }

    @Override
    public void deleteAllTasks() {
        tasksDb.clear();
        prioritizedTasks.stream()
                .filter(task -> task.getType() == TaskTypes.TASK)
                .forEach(prioritizedTasks::remove);
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
            // статус тут не изменяется (только при обновлении подзадач)
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
            prioritizedTasks.stream()
                    .filter(subtask -> subtask.getType() == TaskTypes.SUBTASK)
                    .filter(subtask -> subtaskIdList.contains(subtask.getId()))
                    .forEach(prioritizedTasks::remove);
        }
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicsDb.values());
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epicsDb.get(epicId);
        if (epic != null) {
            historyManager.add(new Epic(epic)); //в историю помещается дубль эпика
        }
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

            if (isCrossedTimeTask(subtask)) {
                throw new TaskCrossTimeException("Невозможно добавить подзадачу " + subtask + " Есть пересечение по времени");
            }

            subtaskDb.put(subtaskId, subtask);
            savedEpic.addNewSubtask(subtaskId);
            if (updateEpicStatus) {
                updateEpicsAttributes(savedEpic.getId());
            }

            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
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
        if (savedEpic == null) return;

        Subtask savedSubtask = subtaskDb.get(subtask.getId());
        if (savedSubtask == null) return;

        if (isCrossedTimeTask(subtask)) {
            throw new TaskCrossTimeException("Невозможно обновление задачи. Пересечение по времени\n" + subtask);
        }

        subtaskDb.put(subtask.getId(), subtask);

        updateEpicsAttributes(subtask.getEpicId());

        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(savedSubtask);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask deletedSubtask = subtaskDb.remove(subtaskId);
        if (deletedSubtask != null) {
            Epic savedEpic = epicsDb.get(deletedSubtask.getEpicId());
            savedEpic.deleteSubtask(subtaskId);
            updateEpicsAttributes(savedEpic.getId());
            prioritizedTasks.remove(deletedSubtask);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private int getUniqueId() {
        return ++uniqueId;
    }

    private void updateEpicStatus(Epic savedEpic) {
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

    protected void updateEpicDataAndDuration(Epic savedEpic) {
        ArrayList<Integer> subtaskIds = savedEpic.getSubtaskIdList();

        if (!subtaskIds.isEmpty()) { // У эпика есть подзадачи

            LocalDateTime epicStartTime;
            if (savedEpic.getStartTime() != null) {
                epicStartTime = savedEpic.getStartTime();
            } else {
                epicStartTime = LocalDateTime.MAX;
            }
            LocalDateTime epicEndTime = LocalDateTime.MIN;

            Duration epicDuration = Duration.ZERO;
            for (int subtaskId : subtaskIds) {
                Subtask subtask = subtaskDb.get(subtaskId);
                Optional<LocalDateTime> startTime = Optional.ofNullable(subtask.getStartTime());
                Optional<LocalDateTime> endTime = Optional.ofNullable(subtask.getEndTime());
                Optional<Duration> duration = Optional.ofNullable(subtask.getDuration());

                if (startTime.isPresent()) {
                    if (startTime.get().isBefore(epicStartTime)) {
                        epicStartTime = startTime.get();
                    }
                }
                if (endTime.isPresent()) {
                    if (endTime.get().isAfter(epicEndTime)) {
                        epicEndTime = endTime.get();
                    }
                }
                if (duration.isPresent()) {
                    epicDuration = epicDuration.plus(duration.get());
                }
            }

            if (epicStartTime.isAfter(LocalDateTime.MIN) && epicStartTime.compareTo(LocalDateTime.MAX) != 0) {
                savedEpic.setStartTime(epicStartTime);
            }
            if (epicEndTime.isAfter(LocalDateTime.MIN)) {
                savedEpic.setEndTime(epicEndTime);
            }
            if (epicDuration.compareTo(Duration.ZERO) != 0) savedEpic.setDuration(epicDuration);
        } else { // У эпика нет подзадач
            savedEpic.setDuration(null);
            savedEpic.setStartTime(null);
        }
    }

    private boolean isCrossStartTimeTasks(Task newTask, Task oldTask) {
        boolean isCrossed = false;

        // Проверяемая задача содержит дату начала и время исполнения
        if (newTask.getStartTime() != null && newTask.getDuration() != null) {
            /*
            https://protocoder.ru/alg/datescrossing
                Отрезки не пересекаются startA---endA  startB---endB  startA---endA
                формула не пересечения: startA > endB OR endA < startB
                формула пересечения: NOT(startA > endB OR endA < startB)
                раскрываем: startA < endB AND endA > startB
             */
            LocalDateTime startA = newTask.getStartTime();
            LocalDateTime endA = startA.plus(newTask.getDuration());
            LocalDateTime startB = oldTask.getStartTime();
            LocalDateTime endB = startB.plus(oldTask.getDuration());

            if (startA.isBefore(endB) && endA.isAfter(startB)) {
                isCrossed = true;
            }
        }
        return isCrossed;
    }

    protected boolean isCrossedTimeTask(Task task) {
        Optional<Task> crossTask;
        crossTask = prioritizedTasks.stream()
                .filter(sortedTask -> sortedTask.getId() != task.getId())
                .filter(sortedTask -> isCrossStartTimeTasks(task, sortedTask))
                .findFirst();

        if (crossTask.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private void updateEpicsAttributes(int epicId) {
        Epic savedEpic = epicsDb.get(epicId);
        if (savedEpic != null) {
            updateEpicDataAndDuration(savedEpic);
            updateEpicStatus(savedEpic);
        }
    }

}
