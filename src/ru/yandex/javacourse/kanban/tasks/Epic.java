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

    public Epic(Epic original) { // Копирование класса для истории
        this.id = original.id;
        this.title = original.title;
        this.status = original.status;
        this.subtaskIdList = original.subtaskIdList;
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
        StringBuilder sb = new StringBuilder();
        sb.append(id)
                .append(",")
                .append(TaskTypes.EPIC)
                .append(",")
                .append(title)
                .append(",")
                .append(status)
                .append(",");

        if (description != null){
            sb.append(description);
        }
               sb.append(",");

        return sb.toString();
    }

}
