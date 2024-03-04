import java.util.ArrayList;

public class TestCases {

    TaskManager manager;

    public TestCases(TaskManager manager){
        this.manager = manager;
        addDefaultTasks();
    }

    public void addNewTask(String title, String description) {
        Task task = new Task(title,description);
        manager.addNewTask(task);
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
        ArrayList<Task> taskList = manager.getAllTasks();
        System.out.println("Вывод списка:");
        for (Task task : taskList) {
            System.out.println(task);
        }
    }

    public void printTaskById(int id){
        Task task = manager.getTaskById(id);
    }


    public void updateSubtask(int subtaskId, String status) {
        SubTask subTask = manager.getSubtaskById(subtaskId);
        SubTask newSubTask = new SubTask(subTask.getId(),subTask.getTitle(),subTask.getDescription(),subTask.getEpicId(),status);
        manager.updateSubtask(newSubTask);
    }

    public void printSubtaskById(int id) {
        SubTask subTask = manager.getSubtaskById(id);
        System.out.println(subTask);
    }

    public void printAllEpicSubtasks(int epicId) {
        Epic epic = manager.getEpicById(epicId);
        ArrayList<SubTask> subTasks = manager.getAllTasksByEpic(epicId);
        for (SubTask subTask : subTasks){
            System.out.println(subTask);
        }
    }

    public void printAllSubTasks() {
        ArrayList<SubTask> subTasks= manager.getAllSubTasks();
        for (SubTask subTask : subTasks){
            System.out.println(subTask);
        }
    }


    private void addDefaultTasks(){

        System.out.println("==========ЗАПОЛЕННЕИЕ ТЕСТСОВЫМИ ДАННЫМИ==========");
        Task task = new Task("Помыть посуду", "На этой неделе");  //1
        Task task2 = new Task("Купить билеты на камчатку", "С 18 по 28 июня"); //2
        Task task3 = new Task("Съездить к родителям", "Не забудь подарки"); //3
        Task task4 = new Task("Постричься", "Совсем зарос"); //4

        Epic epic1 = new Epic("Загран паспорт"); //5
        Epic epic2 = new Epic("Обучение Java 4 спринт"); //6

        SubTask stask1 = new SubTask("Заявление на госуслугах", "Всполмнить пароль", 5); //7
        SubTask stask2 = new SubTask("Госпошлина", "Оплатить",5); // 8
        SubTask stask3 = new SubTask("Запись в МФЦ", "не профукай",5); // 9

        SubTask stask4 = new SubTask("Госпошлина", "Оплатить",6); // 10
        SubTask stask5 = new SubTask("Финальное задание спринта", "",6); // 11

        manager.addNewTask(task);
        manager.addNewTask(task2);
        manager.addNewTask(task3);
        manager.addNewTask(task4);

        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);

        manager.addNewSubTask(stask1);
        manager.addNewSubTask(stask2);
        manager.addNewSubTask(stask3);
        manager.addNewSubTask(stask4);
        manager.addNewSubTask(stask5);
    }

}
