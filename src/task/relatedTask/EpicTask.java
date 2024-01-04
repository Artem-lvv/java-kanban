package task.relatedTask;

import task.Task;

import java.util.HashMap;
import java.util.Map;

public class EpicTask extends Task {
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    public EpicTask(String name, String description) {
        super(name, description);
    }

    public EpicTask(String name, String description, SubTask subTask) {
        this(name, description);
        subTasks.put(subTask.getID(), subTask);
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
       subTasks.put(subTask.getID(), subTask);
    }

    public void deleteSubTaskToID(Integer id) {
        subTasks.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks.size()=" + subTasks.size() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
