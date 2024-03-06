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

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ". epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
