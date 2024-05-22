package ru.yandex.javacourse.kanban.httpServer;

import java.time.Duration;
import java.time.LocalDateTime;

public class JsonTaskModel {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime startTime;
    private Duration duration;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }
}
