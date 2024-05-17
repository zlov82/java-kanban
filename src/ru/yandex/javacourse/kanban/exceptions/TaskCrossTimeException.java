package ru.yandex.javacourse.kanban.exceptions;

public class TaskCrossTimeException extends RuntimeException {

    public TaskCrossTimeException(String message) {
        super(message);
    }
}
