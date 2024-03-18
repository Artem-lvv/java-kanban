package util;

import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskFormatter {

    private CSVTaskFormatter() {
    }

    public static String taskToString(Task task) {

        String subTasks = "null";

        if (task instanceof EpicTask epicTask && !epicTask.getIdSubTaskList().isEmpty()) {
            subTasks = epicTask.getIdSubTaskList().stream().map(String::valueOf).collect(Collectors.joining("-"));
        }

        return String.join(",",
                task.getID().toString(),
                task.getTypeTask().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                task instanceof SubTask subTask ? subTask.getIdRelatedEpicTask().toString() : "null",
                subTasks,
                task.getStartTime() != null ? task.getStartTime().toString() : "null",
                task.getEndTime() != null ? task.getEndTime().toString() : "null",
                task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "null");
    }

    public static Task stringToTask(String stringTask) {
        String[] splitTask = stringTask.split(",");

        Integer id = Integer.valueOf(splitTask[0]);
        TypeTask typeTask = TypeTask.valueOf(splitTask[1]);
        String name = splitTask[2];
        TaskStatus taskStatus = TaskStatus.valueOf(splitTask[3]);
        String description = splitTask[4];
        Integer epicTaskIdForSubTask = !splitTask[5].equals("null") ? Integer.valueOf(splitTask[5]) : null;
        List<Integer> subTaskIdForEpicTask = !splitTask[6].equals("null")
                ? Arrays.stream(splitTask[6].split("-")).map(Integer::parseInt).toList()
                : Collections.emptyList();
        LocalDateTime startTimeTask = !splitTask[7].equals("null") ? LocalDateTime.parse(splitTask[7]) : null;
        LocalDateTime endTimeTask = !splitTask[8].equals("null") ? LocalDateTime.parse(splitTask[8]) : null;
        Duration durationTask = !splitTask[9].equals("null") ? Duration.ofMinutes(Integer.parseInt(splitTask[9])) : null;

        Task newTask = null;

        if (typeTask == TypeTask.TASK) {
            newTask = new Task(name, description);
        } else if (typeTask == TypeTask.SUBTASK) {
            newTask = new SubTask(name, description, epicTaskIdForSubTask);
        } else {
            EpicTask epicTask = new EpicTask(name, description);
            if (!subTaskIdForEpicTask.isEmpty()) {
                subTaskIdForEpicTask.forEach(epicTask::addSubTask);
            }
            epicTask.setEndTime(endTimeTask);
            newTask = epicTask;
        }

        newTask.setId(id);
        newTask.setStatus(taskStatus);
        newTask.setStartTime(startTimeTask);
        newTask.setDuration(durationTask);

        return newTask;
    }

}
