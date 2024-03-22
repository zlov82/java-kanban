package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
