package task.relatedTask;

import task.Task;

public class SubTask extends Task {
    private EpicTask relatedEpicTask;

    public SubTask(String name, String description, EpicTask epicTask) {
        super(name, description);
        this.relatedEpicTask = epicTask;
    }

    public EpicTask getRelatedEpicTask() {
        return relatedEpicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "relatedEpicTask=" + relatedEpicTask +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
