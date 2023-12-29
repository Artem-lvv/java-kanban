package task.related_task;

import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasksList;
    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subTasksList = new ArrayList<>(3);
    }

    public Epic(String name, String description, TaskStatus status, SubTask subTask) {
        this(name, description, status);
        subTasksList.add(subTask);
    }

    public ArrayList<SubTask> getSubTasksList() {
        return subTasksList;
    }

    public void addSubTaskToList(SubTask subTask) {
       subTasksList.add(subTask);
    }
}
