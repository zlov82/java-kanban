package ru.yandex.javacourse.kanban.manager;

import ru.yandex.javacourse.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyDb;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        this.historyDb = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int id = task.getId();
        remove(id); //удаляем из мапы и пересобираем связи между нодами
        historyDb.put(id, linkLast(task));

    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> removedNode = historyDb.remove(id);

        if (removedNode != null) {
            removeNode(removedNode);
        }

    }

    private void removeNode(Node<Task> node) {
        final Node<Task> prevNode = node.prev;
        final Node<Task> nextNode = node.next;

        // Изменяем ссылки внутри списка
        if (prevNode != null) {
            prevNode.next = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        }

        if (head == node) { // удаляемая нода является головой
            head = nextNode;
        }
        if (tail == node) { // удяляемая нода - хвост
            tail = prevNode;
        }

    }

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> oldHead = head;
        final Node<Task> newNode;

        if (oldTail != null) {
            newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            oldTail.next = newNode;
        } else {
            newNode = new Node<>(null, task, oldHead);
            head = newNode;
            if (oldHead == null) {
                tail = newNode;
            } else {
                oldHead.prev = newNode;
            }
        }
        return newNode;
    }

    private List<Task> getTasks() {
        Node<Task> node = head;
        List<Task> historyList = new ArrayList<>();

        for (int i = 0; i < historyDb.size(); i++) {
            historyList.add(node.data);
            node = node.next;
        }

        return historyList;
    }
}
