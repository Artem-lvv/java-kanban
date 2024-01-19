import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    @Override
    public List<Task> getListTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getListEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getListSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
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

    @Override
    public Task getTaskByID(Integer id) {
        return tasks.getOrDefault(id, null);
    }

    @Override
    public EpicTask getEpicTaskByID(Integer id) {
        return epicTasks.getOrDefault(id, null);
    }

    @Override
    public SubTask getSubTaskByID(Integer id) {
        return subTasks.getOrDefault(id, null);
    }

    @Override
    public void addTask(Task task) {
        if (task != null && !tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task);
        }
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        if (epicTask != null && !epicTasks.containsKey(epicTask.getID())) {
            epicTasks.put(epicTask.getID(), epicTask);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask != null && !subTasks.containsKey(subTask.getID())) {
            subTasks.put(subTask.getID(), subTask);
        }
    }

    @Override
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

    @Override
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
