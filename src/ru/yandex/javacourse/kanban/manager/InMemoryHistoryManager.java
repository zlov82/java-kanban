package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyDb;
    private static final int MAX_HISTORY_COUNT = 10;

    public InMemoryHistoryManager() {
        this.historyDb = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyDb.size() == MAX_HISTORY_COUNT){
            historyDb.remove(0);
        }
        historyDb.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyDb);
    }

    @Override
    public void clearHistory() {
        historyDb.clear();
    }
}
