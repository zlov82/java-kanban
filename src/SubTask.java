public class SubTask extends Task {

    private int epicId;


    public SubTask(String title, String description, int epicId) {
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = TaskStatus.NEW;
    }

    public SubTask(int id, String title, String description, int epicId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = TaskStatus.NEW;
    }

    public SubTask(int id, String title, String description, int epicId, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.epicId = epicId;
        this.status = TaskStatus.valueOf(status.toUpperCase());
    }


    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ". epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
