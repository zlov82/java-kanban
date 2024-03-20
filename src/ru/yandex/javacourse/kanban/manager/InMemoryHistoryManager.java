package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    private final ArrayList<Task> historyDb;
    private final int MAX_HISTORY_COUNT = 10;

    public InMemoryHistoryManager() {
        this.historyDb = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (historyDb.size() == MAX_HISTORY_COUNT){
            historyDb.remove(0);
        }
        historyDb.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        list.addAll(historyDb);
        return list;
    }

}
