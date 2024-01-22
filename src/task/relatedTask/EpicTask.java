package task.relatedTask;

import task.Task;
import task.TypeTask;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final ArrayList<Integer> subTasksID;

    public EpicTask(String name, String description) {
        super(name, description);
        this.subTasksID = new ArrayList<>();
        this.typeTask = TypeTask.EPIC;
    }

    public List<Integer> getSubTasksID() {
        return subTasksID;
    }

    public void addSubTask(SubTask subTask) {
        if (!subTasksID.contains(subTask.getID())) {
            subTasksID.add(subTask.getID());
        }
    }

    public void deleteAllSubTasksID() {
        subTasksID.clear();
    }

    public void deleteSubTaskToID(Integer id) {
        subTasksID.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksID=" + subTasksID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
