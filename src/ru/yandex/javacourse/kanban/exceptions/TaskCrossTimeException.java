package ru.yandex.javacourse.kanban.exceptions;

import ru.yandex.javacourse.kanban.tasks.Task;

public class TaskCrossTimeException extends RuntimeException{

    public TaskCrossTimeException(String message) {
        super(message);
    }

}
