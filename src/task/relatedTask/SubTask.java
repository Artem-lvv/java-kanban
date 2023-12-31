package task.relatedTask;

import task.Task;

public class SubTask extends Task {
    private final Integer relatedEpicTaskID;

    public SubTask(String name, String description, EpicTask epicTask) {
        super(name, description);
        this.relatedEpicTaskID = epicTask.getID();
    }

    public Integer getRelatedEpicTaskID() {
        return relatedEpicTaskID;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "relatedEpicTaskID=" + relatedEpicTaskID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
