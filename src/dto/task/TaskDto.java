package dto.task;

import task.TaskStatus;
import task.TypeTask;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDto implements Serializable {
    private String name;
    private String description;
    private Integer id;
    private TaskStatus status;
    private TypeTask typeTask;
    private LocalDateTime startTime;
    private Duration duration;

    public TaskDto(String name, String description, Integer id,
                   TaskStatus status, TypeTask typeTask,
                   LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.typeTask = typeTask;
        this.startTime = startTime;
        this.duration = duration;
    }
}
