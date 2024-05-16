package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.exceptions.ManagerSaveException;
import ru.yandex.javacourse.kanban.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


public class FileBackedTaskManager extends InMemoryTaskManager {
    protected final File pathToFileDb;

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            final String fileInString = Files.readString(file.toPath());

            if (!fileInString.isEmpty()) {
                String[] tasksLines = fileInString.split(System.lineSeparator());
                for (String line : tasksLines) {
                    Task task = taskFromString(line);

                    int taskId = task.getId();
                    if (taskId > manager.uniqueId) {
                        manager.uniqueId = taskId;
                    }
                    manager.addTaskToManager(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно открыть файл для чтения");
        }

        return manager;
    }


    public FileBackedTaskManager(File file) {
        super();
        this.pathToFileDb = file;
    }

    @Override
    public int addNewTask(Task task) {
        int taskId = super.addNewTask(task);
        save();
        return taskId;
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Integer subtaskId = super.addNewSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    protected void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFileDb))) {
            for (Task task : super.tasksDb.values()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Task task : super.epicsDb.values()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Task task : super.subtaskDb.values()) {
                writer.write(toString(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно записать файл");
        }
    }

    private static Task taskFromString(String fileLine) {
        Task task = new Task();
        String[] lines = fileLine.split(",");

        final int taskId = Integer.parseInt(lines[0]);

        switch (TaskTypes.valueOf(lines[1].toUpperCase())) {
            case TaskTypes.TASK:
                if (lines.length > 5) { //заполнены все поля
                    LocalDateTime startTime = LocalDateTime.parse(lines[6]);
                    Duration duration = Duration.ofMinutes(Integer.parseInt(lines[7]));
                    task = new Task(
                            taskId,
                            lines[2],
                            lines[4],
                            TaskStatus.valueOf(lines[3]).toString(),
                            startTime,
                            duration);
                } else {   // нет временных меток
                    task = new Task(
                            taskId,
                            lines[2],
                            lines[4],
                            TaskStatus.valueOf(lines[3]).toString());
                }
                return task;
            case TaskTypes.EPIC:
                task = new Epic(taskId, lines[2], TaskStatus.valueOf(lines[3]));
                return task;
            case TaskTypes.SUBTASK:
                if (lines.length > 6) { // все поля
                    LocalDateTime startTime = LocalDateTime.parse(lines[6]);
                    Duration duration = Duration.ofMinutes(Integer.parseInt(lines[7]));
                    task = new Subtask(
                            taskId,
                            lines[2],
                            lines[4],
                            Integer.parseInt(lines[5]),
                            TaskStatus.valueOf(lines[3]).toString(),
                            startTime,
                            duration);
                } else {
                    task = new Subtask(taskId, lines[2], lines[4], Integer.parseInt(lines[5]),
                            TaskStatus.valueOf(lines[3]).toString());
                }
                return task;
        }
        return task;
    }

    private void addTaskToManager(Task task) {
        final int id = task.getId();
        switch (task.getType()) {
            case TaskTypes.TASK:
                super.tasksDb.put(id, task);
                break;
            case TaskTypes.EPIC:
                super.epicsDb.put(id, (Epic) task);
                break;
            case TaskTypes.SUBTASK:
                Subtask subtask = (Subtask) task;
                final int epicId = subtask.getEpicId();
                super.subtaskDb.put(id, subtask);

                //Добавление подзадачи во внутренний список эпика
                Epic epic = epicsDb.get(epicId);
                epic.addNewSubtask(id);
                updateEpicDataAndDuration(epic); //обновление временных меток эпика

                break;
        }
    }

    public static String toString(Task task) {
        // id, тип, заголовок, описание, подтип задачи (если эпик), дата старта, время выполнения
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId())
                .append(",")
                .append(task.getType())
                .append(",")
                .append(task.getTitle())
                .append(",")
                .append(task.getStatus())
                .append(",");

        String description = task.getDescription();
        if (description != null) {
            sb.append(description);
        }
        sb.append(",");

        if (task.getType() == TaskTypes.SUBTASK) {
            Subtask subtask = (Subtask) task;
            Integer epicId = subtask.getEpicId();
            sb.append(epicId);
        }
        sb.append(",");

        //дата старта
        Optional<LocalDateTime> startTime = Optional.ofNullable(task.getStartTime());
        if (startTime.isPresent()) {
            sb.append(startTime.get());
        }
        sb.append(",");

        //время выполнения
        Optional<Duration> duration = Optional.ofNullable(task.getDuration());
        if (duration.isPresent()) {
            sb.append(duration.get().toMinutes());
        }

        return sb.toString();
    }
}
