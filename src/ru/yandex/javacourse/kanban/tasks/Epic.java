package ru.yandex.javacourse.kanban.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIdList = new ArrayList<>();

    // Общее время выполнения подзадач эпика (рассчитывается отдельно в менеджере)
    private Duration duration;
    // Минимальная дата старта из подзадач эпика (рассчитывается отдельно в менеджере)
    private LocalDateTime startTime;

    public Epic(String title) {
        this.title = title;
        this.status = TaskStatus.NEW;
    }

    public Epic(int id, String title) {
        this.id = id;
        this.title = title;
        this.status = TaskStatus.NEW;
    }

    public Epic(int id, String title, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public Epic(Epic original) { // Копирование класса для истории
        this.id = original.id;
        this.title = original.title;
        this.status = original.status;
        this.subtaskIdList = original.subtaskIdList;
    }

    public void addNewSubtask(int subtaskId) {
        subtaskIdList.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void deleteSubtask(int subtaskId) {
        int index = subtaskIdList.indexOf(subtaskId);
        subtaskIdList.remove(index);
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

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status + '\'' +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration + '\'' +
                ", subtaskIds=" + subtaskIdList +
                '}';
    }

    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }

}
