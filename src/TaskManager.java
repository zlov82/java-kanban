import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    protected static int uniqueId = 0;

    private final HashMap<Integer, Task> tasksDb;
    private final HashMap<Integer, Epic> epicsDb;
    private final HashMap<Integer, ArrayList<SubTask>> subTaskDb; // ключ= ID эпика ; значение = список Субтасков

    public TaskManager() {
        tasksDb = new HashMap<>();
        epicsDb = new HashMap<>();
        subTaskDb = new HashMap<>();
    }

    private int getUniqueId() {
        uniqueId++;
        return uniqueId;
    }

    /********* МЕТОДЫ ОБЫЧНЫХ ЗАДАЧ *********/
    public void addNewTask(Task task) {
        Task newTask = new Task(getUniqueId(), task.getTitle(), task.getDescription());
        tasksDb.put(newTask.getId(), newTask);
        System.out.println("Задача добавлена: " + newTask);
    }

    public void updateTask(Task newTask) {
        if (tasksDb.containsKey(newTask.getId())) {
            tasksDb.put(newTask.getId(), newTask);
            System.out.println("Задача обновлена: " + newTask);
        } else {
            System.out.println("Ошибка - попытка обновления несуществующей задачи id = " + newTask.getId());
        }
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (int taskId : tasksDb.keySet()) {
            taskList.add(tasksDb.get(taskId));
        }
        System.out.println("В список попали задачи: " + taskList.toString());
        return taskList;
    }

    public Task getTaskById(int taskId) {
        Task task = null;
        if (tasksDb.containsKey(taskId)) {
            task = tasksDb.get(taskId);
            System.out.println("Задача id = " + taskId + ": " + task);
        } else {
            System.out.println("Задачи с id =" + taskId + " не найдено");
        }
        return task;
    }

    public void deleteTaskById(int id) {
        if (tasksDb.containsKey(id)) {
            tasksDb.remove(id);
            System.out.println("Задача с ID = " + id + " удалена");
        } else {
            System.out.println("Ошибка - попытка удалить несуществующую задачу ID = " + id);
        }
    }

    public void deleteAllTasks() {
        if (!tasksDb.isEmpty()) {
            tasksDb.clear();
            System.out.println("Все задачачи удалены");
        } else {
            System.out.println("Список задач пуст - удалять нечего");
        }
    }

    /********* ЭПИКИ *********/
    public void addNewEpic(Epic epic) {
        Epic newEpic = new Epic(getUniqueId(), epic.getTitle());
        epicsDb.put(newEpic.getId(), newEpic);
        System.out.println("Новый эпик успешно создан: " + newEpic);
    }

    public void updateEpic(Epic epic) {
        if (epicsDb.containsKey(epic.getId())) {
            epicsDb.put(epic.getId(), epic);
            System.out.println("Эпик обновлен: " + epic);
        } else {
            System.out.println("Ошибка - попытка обновления несуществующего эпика id = " + epic.getId());
        }
    }

    public void deleteEpicById(int epicId) {
        if (epicsDb.containsKey(epicId)) {
            subTaskDb.remove(epicId); // Удаляем список задач
            epicsDb.remove(epicId); // Удаляем сам эпик
            System.out.println("Удалены эпик Id = " + epicId + " и его подзадачи");
        } else {
            System.out.println("Нет возможности удалить эпик с ID = " + epicId);
        }
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (int epicId : epicsDb.keySet()) {
            epicList.add(epicsDb.get(epicId));
        }
        System.out.println("Список полученных эпиков:" + epicList);
        return epicList;
    }

    public Epic getEpicById(int epicId) {
        Epic epic = null;
        if (epicsDb.containsKey(epicId)) {
            epic = epicsDb.get(epicId);
            System.out.println("Эпик id = " + epicId + ": " + epic);
        } else {
            System.out.println("Эпика с id =" + epicId + " не найдено");
        }
        return epic;
    }

    private TaskStatus calculateEpicStatus(int epicId) {
        ArrayList<SubTask> subtaskList = subTaskDb.get(epicId);
        TaskStatus epicStatus = TaskStatus.DONE;
        boolean isNew = false;
        boolean isInProcess = false;
        boolean isDone = false;


        for (int i = 0; i < subtaskList.size(); i++) {
            if (subtaskList.get(i).getStatus() == TaskStatus.NEW) {
                isNew = true;
            }
            if (subtaskList.get(i).getStatus() == TaskStatus.IN_PROGRESS) {
                isInProcess = true;
            }
            if (subtaskList.get(i).getStatus() == TaskStatus.DONE) {
                isDone = true;
            }
        }

        if (isDone && !isNew && !isInProcess) {
            epicStatus = TaskStatus.DONE;
        } else if (isInProcess || isDone) {
            epicStatus = TaskStatus.IN_PROGRESS;
        } else {
            epicStatus = TaskStatus.NEW;
        }
        //System.out.println("Статус эпика ID = " + epicId + " -> " + epicStatus);
        return epicStatus;
    }


    /********* ПОДЗАДАЧИ *********/
    public void addNewSubTask(SubTask subTask) {
        if (epicsDb.containsKey(subTask.getEpicId())) {
            ArrayList subTaskList;

            if (subTaskDb.containsKey(subTask.getEpicId())) {
                subTaskList = subTaskDb.get(subTask.getEpicId());
            } else {
                subTaskList = new ArrayList<>();
            }

            SubTask newSubTask = new SubTask(getUniqueId(), subTask.getTitle(), subTask.getDescription()
                    , subTask.getEpicId());

            subTaskList.add(newSubTask);
            subTaskDb.put(newSubTask.getEpicId(), subTaskList);
            System.out.println("Создана подзадача:" + newSubTask + " и привязана к эпику id = "
                    + newSubTask.getEpicId());

        } else {
            System.out.println("ОШИБКА при добавлении подзадачи!" +
                    " Не найдено эпика с номером " + subTask.getEpicId());
        }
    }

    public SubTask getSubtaskById(int subTaskId) {
        ArrayList<SubTask> subTasksList;
        SubTask subTask;
        for (int epicId : subTaskDb.keySet()) {
            subTasksList = subTaskDb.get(epicId);
            for (int index = 0; index < subTasksList.size(); index++) {
                subTask = subTasksList.get(index);
                if (subTask.getId() == subTaskId) {
                    //System.out.println("По поиску найдена подзазача:" + subTask);
                    return subTask;
                }
            }
        }
        return null;
    }

    public ArrayList<SubTask> getAllTasksByEpic(int epicId) {
        ArrayList<SubTask> subtaskIntList = new ArrayList<>();
        if (epicsDb.containsKey(epicId)) {
            subtaskIntList = subTaskDb.get(epicId);
        }
        return subtaskIntList;
    }


    public void updateSubtask(SubTask subTask) {
        if (epicsDb.containsKey(subTask.getEpicId())) {
            ArrayList<SubTask> subTaskList = subTaskDb.get(subTask.getEpicId());

            for (int index = 0; index < subTaskList.size(); index++) {
                if (subTaskList.get(index).getId() == subTask.getId()) {
                    subTaskList.set(index, subTask);
                    System.out.println("Подзадача ID = " + subTask.getId() + " обновлена: " + subTask);

                    Epic epic = epicsDb.get(subTask.getEpicId());
                    Epic newEpic = new Epic(epic.getId(), epic.getTitle(), calculateEpicStatus(subTask.getEpicId()));
                    updateEpic(newEpic);

                    return;
                }
            }
        } else {
            System.out.println("ОШИБКА при обновлении подзадачи. Не найден эпик ID = " + subTask.getEpicId());
        }

    }

    public void deleteSubTaskById(int subTaskId) {
        ArrayList<SubTask> subTasksList;
        SubTask subTask;
        for (int epicId : subTaskDb.keySet()) {
            subTasksList = subTaskDb.get(epicId);
            for (int index = 0; index < subTasksList.size(); index++) {
                subTask = subTasksList.get(index);
                if (subTask.getId() == subTaskId) {
                    subTasksList.remove(index);
                    System.out.println("Удалена задача :" + subTask);
                    return;
                }
            }
        }
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> epicSubTaskList;
        ArrayList<SubTask> subtaskList = new ArrayList<>();
        if (!epicsDb.isEmpty()) {
            for (int index : subTaskDb.keySet()) {
                epicSubTaskList = subTaskDb.get(index);
                for (int i = 0; i < epicSubTaskList.size(); i++) {
                    subtaskList.add(epicSubTaskList.get(i));
                }
            }

        } else {
            System.out.println("Сейчас нет ни одного эпика для вывода подзадач");
        }

        return subtaskList;
    }


}
