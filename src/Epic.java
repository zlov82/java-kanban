public class Epic extends Task {

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

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}
