package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Task;
import java.util.ArrayList;

public interface HistoryManager {

    public void add(Task task);
    public ArrayList<Task> getHistory();

}
