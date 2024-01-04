import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.util.*;

public class TaskManager {
    private static TaskManager taskManager;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, SubTask> subTasks;

    private TaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public static TaskManager getTaskManager() {
        if (taskManager == null) {
            taskManager = new TaskManager();
        }
        return taskManager;
    }

    public List<Task> getListTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<EpicTask> getListEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public List<SubTask> getListSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTaskByType(TypeTask typeTask) {
        switch (typeTask) {
            case TASK:
                tasks.clear();
                break;
            case EPIC:
                epicTasks.clear();
                break;
            case SUBTASK:
                subTasks.clear();
                break;
            default:
                break;
        }
    }

    public Task getTaskByID(Integer id) {
        return tasks.getOrDefault(id, null);
    }

    public Task getEpicTaskByID(Integer id) {
        return epicTasks.getOrDefault(id, null);
    }
    public Task getSubTaskByID(Integer id) {
        return subTasks.getOrDefault(id, null);
    }

    public void addTask(Task task) {
        if (task != null && !tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task);
        }
    }
    public void addEpicTask(EpicTask epicTask) {
        if (epicTask != null && !epicTasks.containsKey(epicTask.getID())) {
            epicTasks.put(epicTask.getID(), epicTask);
        }
    }
    public void addSubTask(SubTask subTask) {
        if (subTask != null && !subTasks.containsKey(subTask.getID())) {
            subTasks.put(subTask.getID(), subTask);
            checkAndUpdateEpicTaskStatus(subTask);
        }
    }

    public void updateTaskByType(TypeTask typeTask, Task task) {
       if (task == null) {
        return;
       }
        switch (typeTask) {
            case TASK:
                tasks.put(task.getID(), task);
                break;
            case EPIC:
                epicTasks.put(task.getID(), (EpicTask) task);
                break;
            case SUBTASK:
                subTasks.put(task.getID(), (SubTask) task);

                ((SubTask) task).getRelatedEpicTask().addSubTask((SubTask) task); // updating related tasks
                checkAndUpdateEpicTaskStatus((SubTask) task);
                break;
            default:
                break;
        }
    }

    private void checkAndUpdateEpicTaskStatus(SubTask subTask) {
        EpicTask epicTask = subTask.getRelatedEpicTask();
        Map<Integer, SubTask> subTasksEpic = epicTask.getSubTasks();

        if (subTasksEpic.isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        Integer countSubTaskNEW = 0;
        int countSubTaskDONE = 0;


        for (SubTask elemSubTask : subTasksEpic.values()) {
            if (elemSubTask.getStatus() == TaskStatus.NEW) {
                countSubTaskNEW++;
            } else if (elemSubTask.getStatus() == TaskStatus.DONE) {
                countSubTaskDONE++;
            }
        }

        if (subTasksEpic.size() == countSubTaskNEW) {
            epicTask.setStatus(TaskStatus.NEW);
        } else if (subTasksEpic.size() == countSubTaskDONE) {
            epicTask.setStatus(TaskStatus.DONE);
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void deleteTaskByTypeAndID(TypeTask typeTask, Integer id) {
        switch (typeTask) {
            case TASK:
                tasks.remove(id);
                break;
            case EPIC:
                epicTasks.remove(id);
                break;
            case SUBTASK:
                SubTask subTask = (SubTask) getSubTaskByID(id);
                subTask.getRelatedEpicTask().deleteSubTaskToID(id); // delete related tasks
                checkAndUpdateEpicTaskStatus(subTask);
                subTasks.remove(id);
                break;
            default:
                break;
        }
    }

}
