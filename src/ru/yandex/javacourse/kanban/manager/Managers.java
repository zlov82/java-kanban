package ru.yandex.javacourse.kanban.manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        //return new InMemoryTaskManager();
        return new FileBackedTaskManager(new File("resources/task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
