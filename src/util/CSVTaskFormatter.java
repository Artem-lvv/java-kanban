package util;

import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskFormatter {

    private CSVTaskFormatter() {
    }

    public static String taskToString(Task task) {

        String subTasks = "null";

        if (task instanceof EpicTask epicTask && !epicTask.getSubTasksID().isEmpty()) {
            subTasks = epicTask.getSubTasksID().stream().map(String::valueOf).collect(Collectors.joining("-"));
        }

        return  String.join(",",
                task.getID().toString(), task.getTypeTask().toString(),
                task.getName(), task.getStatus().toString(), task.getDescription(),
                task instanceof SubTask subTask ? subTask.getRelatedEpicTaskID().toString() : "null",
                subTasks);
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
            newTask = epicTask;
        }

        newTask.setId(id);
        newTask.setStatus(taskStatus);

        return newTask;
    }

}
