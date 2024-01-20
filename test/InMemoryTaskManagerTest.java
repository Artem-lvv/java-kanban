import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    private final static TaskManager taskManager = Managers.getDefault();
    private final static Task task = new Task("Task test", "Описание Task");
    private final static EpicTask epicTask = new EpicTask("EpicTask test", "Описание EpicTask test");
    private final static SubTask subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTask);
    private final static SubTask subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTask);

    @BeforeEach
    void addTasksBeforeAll() {
        taskManager.deleteAllTaskByType(TypeTask.TASK);
        taskManager.deleteAllTaskByType(TypeTask.EPIC);
        taskManager.deleteAllTaskByType(TypeTask.SUBTASK);

        epicTask.addSubTask(subTaskOne);
        epicTask.addSubTask(subTaskTwo);

        taskManager.addTask(task);
        taskManager.addEpicTask(epicTask);
        taskManager.addSubTask(subTaskOne);
        taskManager.addSubTask(subTaskTwo);
    }

    @Test
    void addTasks() {
        assertAll("add tasks",
                () -> assertEquals(1, taskManager.getListTasks().size()),
                () -> assertEquals(1, taskManager.getListEpicTasks().size()),
                () -> assertEquals(2, taskManager.getListSubTasks().size()));
    }

    @Test
    void getTasks() {
        assertAll("get tasks",
                () -> assertEquals(task, taskManager.getTaskByID(task.getID())),
                () -> assertEquals(epicTask, taskManager.getEpicTaskByID(epicTask.getID())),
                () -> assertEquals(subTaskOne, taskManager.getSubTaskByID(subTaskOne.getID())),
                () -> assertEquals(subTaskTwo, taskManager.getSubTaskByID(subTaskTwo.getID())));
    }

    @Test
    void shiftAndUpdateEpicStatus() {
        assertEquals(TaskStatus.NEW, epicTask.getStatus());

        subTaskOne.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTaskByType(TypeTask.SUBTASK, subTaskOne);

        assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());

        subTaskOne.setStatus(TaskStatus.DONE);
        taskManager.deleteTaskByTypeAndID(TypeTask.SUBTASK, subTaskTwo.getID());

        assertEquals(TaskStatus.DONE, epicTask.getStatus());
    }

}
