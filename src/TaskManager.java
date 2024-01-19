import task.Task;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.util.List;

public interface TaskManager {
    List<Task> getListTasks();

    List<EpicTask> getListEpicTasks();

    List<SubTask> getListSubTasks();

    void deleteAllTaskByType(TypeTask typeTask);

    Task getTaskByID(Integer id);

    EpicTask getEpicTaskByID(Integer id);

    SubTask getSubTaskByID(Integer id);

    void addTask(Task task);

    void addEpicTask(EpicTask epicTask);

    void addSubTask(SubTask subTask);

    void updateTaskByType(TypeTask typeTask, Task task);

    void deleteTaskByTypeAndID(TypeTask typeTask, Integer id);

    List<Task> getHistory();
}
