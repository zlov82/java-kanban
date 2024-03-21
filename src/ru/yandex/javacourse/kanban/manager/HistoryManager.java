package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Task;
import java.util.List;

public interface HistoryManager {

    public void add(Task task);
    public List<Task> getHistory();
    public void clearHistory();

}
