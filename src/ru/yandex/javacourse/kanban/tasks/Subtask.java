package ru.yandex.javacourse.kanban.tasks;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId) {
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = TaskStatus.NEW;
    }

    public Subtask(int id, String title, String description, int epicId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = TaskStatus.NEW;
    }

    public Subtask(int id, String title, String description, int epicId, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = TaskStatus.valueOf(status.toUpperCase());
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id)
                .append(",")
                .append(TaskTypes.SUBTASK)
                .append(",")
                .append(title)
                .append(",")
                .append(status)
                .append(",");

        if (description != null) {
            sb.append(description);
        }
        sb.append(",")
                .append(epicId);

        return sb.toString();
    }
}
