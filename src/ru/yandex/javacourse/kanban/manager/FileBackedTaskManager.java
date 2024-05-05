package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.exceptions.ManagerSaveException;
import ru.yandex.javacourse.kanban.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try {
            String fileInString = Files.readString(file.toPath());
            if (!fileInString.isEmpty()) {
                String[] tasksLines = fileInString.split("\n");
                for (String line: tasksLines) {
                    fromString(line, fileBackedTaskManager);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

        return fileBackedTaskManager;
    }

    private final String pathToFileDb;

    public FileBackedTaskManager(File file) {
        super();
        this.pathToFileDb = file.toString();
    }

    @Override
    public int addNewTask(Task task) {
        int ret = super.addNewTask(task);
        save();
        return ret;
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public Task getTaskById(int taskId) {
        return super.getTaskById(taskId);
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
        int ret = super.addNewEpic(epic);
        save();
        return ret;
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
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public Epic getEpicById(int epicId) {
        return super.getEpicById(epicId);
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Integer ret = super.addNewSubtask(subtask);
        save();
        return ret;
    }

    @Override
    public Subtask getSubtaskById(int subTaskId) {
        return super.getSubtaskById(subTaskId);
    }

    @Override
    public ArrayList<Subtask> getAllTasksByEpic(int epicId) {
        return super.getAllTasksByEpic(epicId);
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

    @Override
    public ArrayList<Subtask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    private void save() {
        StringBuilder stringToFile = new StringBuilder();

        for (Task task : super.tasksDb.values()) {
            stringToFile.append(task.toString()).append("\n");
        }

        for (Task task : super.epicsDb.values()) {
            stringToFile.append(task.toString()).append("\n");
        }

        for (Task task : super.subtaskDb.values()) {
            stringToFile.append(task.toString()).append("\n");
        }

        try (Writer writer = new FileWriter(pathToFileDb)){
            writer.write(stringToFile.toString());
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

        switch (TaskTypes.valueOf(lines[1].toUpperCase())){
            case TaskTypes.TASK:
                task = new Task(taskId, lines[2], lines[4], TaskStatus.valueOf(lines[3]).toString());
                manager.addNewTask(task);
                break;
            case TaskTypes.EPIC:
                task = new Epic(taskId, lines[2], TaskStatus.valueOf(lines[3]));
                manager.addNewEpic((Epic) task);
                break;
            case TaskTypes.SUBTASK:
                task = new Subtask(taskId,lines[2], lines[4],Integer.parseInt(lines[5]) ,TaskStatus.valueOf(lines[3]).toString());
                manager.addNewSubtask((Subtask) task);
                break;
        }
    }
}
