package util;

import dto.relatedTask.EpicTaskDto;
import dto.relatedTask.SubTaskDto;
import dto.task.TaskDto;
import task.Task;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

public class DTOMapping {
    private DTOMapping() {
    }

    public static TaskDto mapToTaskDto(Task entity) {
        return new TaskDto(entity.getName(),
                entity.getDescription(),
                entity.getID(),
                entity.getStatus(),
                entity.getTypeTask(),
                entity.getStartTime(),
                entity.getDuration());
    }

    public static SubTaskDto mapToSubTaskDto(SubTask entity) {
        return new SubTaskDto(entity.getName(),
                entity.getDescription(),
                entity.getID(),
                entity.getStatus(),
                entity.getTypeTask(),
                entity.getStartTime(),
                entity.getDuration(),
                entity.getIdRelatedEpicTask());
    }

    public static EpicTaskDto mapToEpicTaskDto(EpicTask entity) {
        return new EpicTaskDto(entity.getName(),
                entity.getDescription(),
                entity.getID(),
                entity.getStatus(),
                entity.getTypeTask(),
                entity.getStartTime(),
                entity.getDuration(),
                entity.getIdSubTaskList(),
                entity.getEndTime());
    }
}
