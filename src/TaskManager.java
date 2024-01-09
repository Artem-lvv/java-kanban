import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
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
                subTasks.clear();
                break;
            case SUBTASK:
                epicTasks.forEach((id, epicTask) -> epicTask.deleteAllSubTasksID());
                subTasks.clear();
                break;
            default:
                break;
        }
    }

    public Task getTaskByID(Integer id) {
        return tasks.getOrDefault(id, null);
    }

    public EpicTask getEpicTaskByID(Integer id) {
        return epicTasks.getOrDefault(id, null);
    }

    public SubTask getSubTaskByID(Integer id) {
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

                EpicTask epicTask = getEpicTaskByID((((SubTask) task).getRelatedEpicTaskID()));
                epicTask.addSubTask((SubTask) task); // updating related tasks
                checkAndUpdateEpicTaskStatus((SubTask) task);
                break;
            default:
                break;
        }
    }

    public void deleteTaskByTypeAndID(TypeTask typeTask, Integer id) {
        switch (typeTask) {
            case TASK:
                tasks.remove(id);
                break;
            case EPIC:
                // delete related SubTasks
                EpicTask epicTask = getEpicTaskByID(id);
                List<Integer> subTasksIdEpicTask = epicTask.getSubTasksID();
                subTasksIdEpicTask.forEach(subTasks::remove);

                epicTasks.remove(id);
                break;
            case SUBTASK:
                // delete related tasks
                SubTask subTask = getSubTaskByID(id);
                EpicTask epicTaskByID = getEpicTaskByID(subTask.getRelatedEpicTaskID());
                epicTaskByID.deleteSubTaskToID(id);

                checkAndUpdateEpicTaskStatus(subTask);
                subTasks.remove(id);
                break;
            default:
                break;
        }
    }

    private void checkAndUpdateEpicTaskStatus(SubTask subTask) {
        EpicTask epicTask = getEpicTaskByID(subTask.getRelatedEpicTaskID());
        List<Integer> subTasksID = epicTask.getSubTasksID();

        if (subTasksID.isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        int countSubTaskNEW = 0;
        int countSubTaskDONE = 0;

        for (Integer subTaskID : subTasksID) {
            SubTask subTaskByID = getSubTaskByID(subTaskID);

            if (subTaskByID.getStatus() == TaskStatus.NEW) {
                countSubTaskNEW++;
            } else if (subTaskByID.getStatus() == TaskStatus.DONE) {
                countSubTaskDONE++;
            }
        }

        if (subTasksID.size() == countSubTaskNEW) {
            epicTask.setStatus(TaskStatus.NEW);
        } else if (subTasksID.size() == countSubTaskDONE) {
            epicTask.setStatus(TaskStatus.DONE);
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

}
