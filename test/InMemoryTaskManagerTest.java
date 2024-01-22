import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private Task task;
    private EpicTask epicTask;
    private SubTask subTaskOne;
    private SubTask subTaskTwo;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("Task test", "Описание Task");
        epicTask = new EpicTask("EpicTask test", "Описание EpicTask test");
        subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTask);
        subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTask);

        taskManager.addEpicTask(epicTask);

        //epicTask.addSubTask(subTaskOne);

        taskManager.addSubTask(subTaskOne, epicTask);
        //epicTask.addSubTask(subTaskTwo);
        taskManager.addSubTask(subTaskTwo, epicTask);
        taskManager.addTask(task);
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
        taskManager.updateTask(subTaskOne);

        assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());

        subTaskOne.setStatus(TaskStatus.DONE);
        taskManager.deleteTaskByID(subTaskTwo.getID());

        assertEquals(TaskStatus.DONE, epicTask.getStatus());
    }

}
