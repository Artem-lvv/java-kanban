package dto.relatedTask;

import task.TaskStatus;
import task.TypeTask;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTaskDto implements Serializable {
    private String name;
    private String description;
    private Integer id;
    private TaskStatus status;
    private TypeTask typeTask;
    private LocalDateTime startTime;
    private Duration duration;
    private Integer idRelatedEpicTask;

    public SubTaskDto(String name, String description, Integer id,
                      TaskStatus status, TypeTask typeTask,
                      LocalDateTime startTime, Duration duration,
                      Integer idRelatedEpicTask) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.typeTask = typeTask;
        this.startTime = startTime;
        this.duration = duration;
        this.idRelatedEpicTask = idRelatedEpicTask;
    }
}
