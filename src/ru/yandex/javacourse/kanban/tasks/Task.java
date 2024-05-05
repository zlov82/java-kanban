package ru.yandex.javacourse.kanban.tasks;

import java.util.Objects;

public class Task {

    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status;

    //Конструктор с полями по умолчанию
    public Task() {

    }

    public Task(String title, String description) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String title, String description, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.valueOf(status.toUpperCase());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id)
                .append(",")
                .append(TaskTypes.TASK)
                .append(",")
                .append(title)
                .append(",")
                .append(status)
                .append(",");
        if (description != null) {
            sb.append(description);
        }
        sb.append(",");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }
}
