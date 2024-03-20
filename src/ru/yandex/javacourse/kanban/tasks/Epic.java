package ru.yandex.javacourse.kanban.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String title) {
        this.title = title;
        this.status = TaskStatus.NEW;
    }

    public Epic(int id, String title) {
        this.id = id;
        this.title = title;
        this.status = TaskStatus.NEW;
    }

    public Epic(int id, String title, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public void addNewSubtask(int subtaskId) {
        subtaskIdList.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void deleteSubtask(int subtaskId) {
        int index = subtaskIdList.indexOf(subtaskId);
        subtaskIdList.remove(index);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }

}
