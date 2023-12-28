package task.related_task;

import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;
    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subTasks = new ArrayList<>();
    }
}
