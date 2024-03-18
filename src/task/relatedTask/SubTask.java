package task.relatedTask;

import task.Task;
import task.TypeTask;

public class SubTask extends Task {
    private final Integer idRelatedEpicTask;

    public SubTask(String name, String description, Integer epicTaskId) {
        super(name, description);
        this.idRelatedEpicTask = epicTaskId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public Integer getIdRelatedEpicTask() {
        return idRelatedEpicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "relatedEpicTaskID=" + idRelatedEpicTask +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", typeTask=" + typeTask +
                '}';
    }
}
