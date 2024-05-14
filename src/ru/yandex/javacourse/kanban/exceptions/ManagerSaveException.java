package ru.yandex.javacourse.kanban.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(Throwable cause) {
        super(cause);
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}
