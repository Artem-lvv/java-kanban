package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {

    private EpicTask epicTask;
    private SubTask subTask;

    @BeforeEach
    void beforeEach() {
        epicTask = new EpicTask("Epic task", "Описание EpicTask");
        subTask = new SubTask("SubTask test", "Описание SubTask test", epicTask.getID());
    }

    @Test
    void shouldReturnEquals() {
        TaskManager taskManager = Managers.newInMemoryTaskManager();
        taskManager.addEpicTask(epicTask);
        taskManager.addSubTask(subTask);
        Optional<SubTask> getSubTask = taskManager.getSubTaskByID(subTask.getID());

        assertEquals(subTask, getSubTask.get());
    }

    @Test
    void getStatus() {
        assertEquals(TaskStatus.NEW, subTask.getStatus());
    }

    @Test
    void setStatus() {
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, subTask.getStatus());
    }
}
