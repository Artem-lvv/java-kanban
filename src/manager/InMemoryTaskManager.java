package manager;

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
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
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
                tasks.forEach((id, epicTask) -> historyManager.remove(id));
                tasks.clear();
                break;
            case EPIC:
                epicTasks.forEach((id, epicTask) -> historyManager.remove(id));
                subTasks.forEach((id, epicTask) -> historyManager.remove(id));
                epicTasks.clear();
                subTasks.clear();
                break;
            case SUBTASK:
                epicTasks.forEach((id, epicTask) -> epicTask.deleteAllSubTasksID());
                epicTasks.forEach((id, epicTask) -> epicTask.setStatus(TaskStatus.NEW));
                subTasks.forEach((id, epicTask) -> historyManager.remove(id));
                subTasks.clear();
                break;
            default:
                break;
        }
    }

    @Override
    public Task getTaskByID(Integer id) {
        Task task = tasks.getOrDefault(id, null);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTaskByID(Integer id) {
        EpicTask epicTask = epicTasks.getOrDefault(id, null);
        historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTaskByID(Integer id) {
        SubTask subTask = subTasks.getOrDefault(id, null);
        historyManager.add(subTask);
        return subTask;
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
            epicTasks.getOrDefault(subTask.getRelatedEpicTaskID(), null).addSubTask(subTask.getID());

            checkAndUpdateEpicTaskStatus(subTask);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        switch (task.getTypeTask()) {
            case TASK:
                tasks.put(task.getID(), task);
                break;
            case EPIC:
                epicTasks.put(task.getID(), (EpicTask) task);
                break;
            case SUBTASK:
                subTasks.put(task.getID(), (SubTask) task);
                EpicTask epicTask = epicTasks.getOrDefault((((SubTask) task).getRelatedEpicTaskID()), null);
                epicTask.addSubTask(task.getID()); // updating related tasks
                checkAndUpdateEpicTaskStatus((SubTask) task);
                break;
            default:
                break;
        }
    }

    @Override
    public void deleteTaskByID(Integer id) {
        if (id == null) {
            return;
        }
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            EpicTask epicTask = epicTasks.getOrDefault(id, null);
            List<Integer> subTasksIdEpicTask = epicTask.getSubTasksID();
            for (Integer idSubTask : subTasksIdEpicTask) {
                historyManager.remove(idSubTask);
                subTasks.remove(idSubTask);
            }
            historyManager.remove(id);
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.getOrDefault(id, null);
            EpicTask epicTaskByID = epicTasks.getOrDefault(subTask.getRelatedEpicTaskID(), null);
            epicTaskByID.deleteSubTaskToID(id);

            checkAndUpdateEpicTaskStatus(subTask);
            historyManager.remove(id);
            subTasks.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void checkAndUpdateEpicTaskStatus(SubTask subTask) {
        EpicTask epicTask = epicTasks.getOrDefault(subTask.getRelatedEpicTaskID(), null);
        List<Integer> subTasksID = epicTask.getSubTasksID();

        if (subTasksID.isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        int countSubTaskNEW = 0;
        int countSubTaskDONE = 0;

        for (Integer subTaskID : subTasksID) {
            SubTask subTaskByID = subTasks.getOrDefault(subTaskID, null);

            if (subTaskByID == null) {
                continue;
            }

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
