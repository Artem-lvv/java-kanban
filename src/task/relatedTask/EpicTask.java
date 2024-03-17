package task.relatedTask;

import task.Task;
import task.TypeTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> idSubTaskList;
    private LocalDateTime endTime;

    public EpicTask(String name, String description) {
        super(name, description);
        this.idSubTaskList = new ArrayList<>();
        this.typeTask = TypeTask.EPIC;
    }

    public List<Integer> getIdSubTaskList() {
        return idSubTaskList;
    }

    public void addSubTask(Integer subTaskId) {
        if (!idSubTaskList.contains(subTaskId)) {
            idSubTaskList.add(subTaskId);
        }
    }

    public void deleteAllSubTasksID() {
        idSubTaskList.clear();
    }

    public void deleteSubTaskToID(Integer id) {
        idSubTaskList.remove(id);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksID=" + idSubTaskList +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", typeTask=" + typeTask +
                '}';
    }
}
