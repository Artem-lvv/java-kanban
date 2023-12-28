package task.related_task;

import task.Task;
import task.TaskStatus;

public class SubTask extends Task {
    private Epic epicTask;
    public SubTask(String name, String description, TaskStatus status, Epic epic) {
        super(name, description, status);
        epicTask = epic;
    }
}
