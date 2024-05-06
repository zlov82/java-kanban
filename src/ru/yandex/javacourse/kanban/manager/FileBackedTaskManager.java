package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.exceptions.ManagerSaveException;
import ru.yandex.javacourse.kanban.tasks.*;

import java.io.*;
import java.nio.file.Files;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File pathToFileDb;

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try {
            String fileInString = Files.readString(file.toPath());
            if (!fileInString.isEmpty()) {
                String[] tasksLines = fileInString.split(System.lineSeparator());
                for (String line : tasksLines) {
                    fromString(line, fileBackedTaskManager);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

        return fileBackedTaskManager;
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFileDb))){
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
            throw new ManagerSaveException(e);
        }
    }

    private static void fromString(String fileLine, FileBackedTaskManager manager) {
        Task task;
        String[] lines = fileLine.split(",");

            /*
            0 - id
            1 - тип задачи
            2 - title
            3 - status
            4 - description
            5 (если тип subtask) - epicId
             */

        int taskId = Integer.parseInt(lines[0]);
        if (manager.uniqueId < taskId) {
            manager.uniqueId = taskId;
        }

        switch (TaskTypes.valueOf(lines[1].toUpperCase())) {
            case TaskTypes.TASK:
                task = new Task(taskId, lines[2], lines[4], TaskStatus.valueOf(lines[3]).toString());
                manager.addNewTask(task);
                break;
            case TaskTypes.EPIC:
                task = new Epic(taskId, lines[2], TaskStatus.valueOf(lines[3]));
                manager.addNewEpic((Epic) task);
                break;
            case TaskTypes.SUBTASK:
                task = new Subtask(taskId, lines[2], lines[4], Integer.parseInt(lines[5]), TaskStatus.valueOf(lines[3]).toString());
                manager.addNewSubtask((Subtask) task);
                break;
        }
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
                break;
        }
    }

    public static String toString(Task task) {
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
        return sb.toString();
    }
}
