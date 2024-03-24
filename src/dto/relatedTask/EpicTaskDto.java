package dto.relatedTask;

import task.TaskStatus;
import task.TypeTask;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicTaskDto implements Serializable {
    private String name;
    private String description;
    private Integer id;
    private TaskStatus status;
    private TypeTask typeTask;
    private LocalDateTime startTime;
    private Duration duration;
    private List<Integer> idSubTaskList;
    private LocalDateTime endTime;

    public EpicTaskDto(String name, String description, Integer id,
                       TaskStatus status, TypeTask typeTask,
                       LocalDateTime startTime, Duration duration,
                       List<Integer> idSubTaskList, LocalDateTime endTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.typeTask = typeTask;
        this.startTime = startTime;
        this.duration = duration;
        this.idSubTaskList = idSubTaskList;
        this.endTime = endTime;
    }
}
