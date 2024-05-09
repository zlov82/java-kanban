package ru.yandex.javacourse.kanban.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    private Duration duration;
    private LocalDateTime startTime;

    public Subtask(String title, String description, int epicId) {
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = TaskStatus.NEW;
    }

    public Subtask(String title, String description, int epicId, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.startTime = startTime;
        this.duration = duration;
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ". epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", status=" + status +
                '}';
    }

    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }
}
