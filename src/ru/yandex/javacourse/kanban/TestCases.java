package ru.yandex.javacourse.kanban;

import ru.yandex.javacourse.kanban.manager.TaskManager;
import ru.yandex.javacourse.kanban.tasks.*;

import java.util.ArrayList;

public class TestCases {

    TaskManager manager;

    public TestCases(TaskManager manager){
        this.manager = manager;
        addDefaultTasks();
    }

    /* ЗАДАЧИ */

    public int addNewTask(String title, String description) {
        return manager.addNewTask(new Task(title,description));
    }

    public void updateTask(int taskId, String description, String status){
        Task task = manager.getTaskById(taskId);
        Task newTask = new Task(task.getId(),task.getTitle(),description, status);
        manager.updateTask(newTask);
    }

    public  void deleteTask(int taskId) {
        manager.deleteTaskById(taskId);
    }


    public void printAllTasks(){
        System.out.println("Список задач:");
        System.out.println(manager.getAllTasks());
    }

    public void printTaskById(int id){
        System.out.println(manager.getTaskById(id));
    }


    /* Подзадачи */

    public Integer addNewSubtask(String title, String description, int epicId) {
        return manager.addNewSubtask(new Subtask(title,description,epicId));
    }

    public void updateSubtask(int subtaskId, String status) {
        Subtask subTask = manager.getSubtaskById(subtaskId);
        Subtask newSubtask = new Subtask(subTask.getId(),subTask.getTitle(),subTask.getDescription(),subTask.getEpicId(),status);
        manager.updateSubtask(newSubtask);
    }


    public void printSubtaskById(int id) {
        System.out.println("Подзадача id = "+id);
        System.out.println(manager.getSubtaskById(id));
    }

    public void printAllEpicSubtasks(int epicId) {
        System.out.println("Список задач в эпике " + epicId);
        System.out.println(manager.getAllTasksByEpic(epicId));
    }

    public void printAllSubTasks() {
        System.out.println("Список подзадач:");
        System.out.println(manager.getAllSubTasks());
    }

    /* Эпики */
    public int addNewEpic(String title){
        return manager.addNewEpic(new Epic(title));
    }

    public void updateEpic(int epicId, String newTitle) {
        Epic savedEpic = manager.getEpicById(epicId);
        Epic newEpic = new Epic(savedEpic.getId(), newTitle);
        manager.updateEpic(newEpic);
    }

    public void printAllEpics(){
        System.out.println("Список эпиков:");
        System.out.println(manager.getAllEpics());
    }

    public void printEpicById(int epicId) {
        System.out.println(manager.getEpicById(epicId));
    }



    private void addDefaultTasks(){

        System.out.println("==========ЗАПОЛЕННЕИЕ ТЕСТСОВЫМИ ДАННЫМИ==========");
        int task1 = manager.addNewTask(new Task("Помыть посуду", "На этой неделе"));
        int task2 = manager.addNewTask(new Task("Купить билеты на камчатку", "С 18 по 28 июня"));
        int task3 = manager.addNewTask(new Task("Съездить к родителям", "Не забудь подарки"));
        int task4 = manager.addNewTask(new Task("Постричься", "Совсем зарос"));

        int epic1 = manager.addNewEpic(new Epic("Загран паспорт"));
        int epic2 = manager.addNewEpic(new Epic("Обучение Java 4 спринт"));

        int subtask1 = manager.addNewSubtask(new Subtask("Заявление на госуслугах", "Всполмнить пароль", epic1));
        int subtask2 = manager.addNewSubtask(new Subtask("Госпошлина", "Оплатить",epic1));
        int subtask3 = manager.addNewSubtask(new Subtask("Запись в МФЦ", "не профукай",epic1));

        int subtask4 = manager.addNewSubtask(new Subtask("Госпошлина", "Оплатить",epic2));
        int subtask5 = manager.addNewSubtask(new Subtask("Финальное задание спринта", "",epic2)); // 11

        printAllTasks();
        printAllEpics();
        printAllSubTasks();

    }

}
