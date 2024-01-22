import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest {

    private EpicTask epicTask;

    @BeforeEach
    void beforeEach() {
        epicTask = new EpicTask("EpicTask test", "Описание EpicTask test");
    }

    @Test
    void shouldReturnEquals() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addEpicTask(epicTask);
        EpicTask getEpicTask = taskManager.getEpicTaskByID(epicTask.getID());
        assertEquals(epicTask, getEpicTask);
    }

    @Test
    void addSubTaskInEpicTask() {
        SubTask subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTask);
        SubTask subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTask);
        epicTask.addSubTask(subTaskOne);
        epicTask.addSubTask(subTaskTwo);

        assertAll("add two SubTask in EpicTask",
                () -> assertEquals(epicTask.getID(), subTaskOne.getRelatedEpicTaskID()),
                () -> assertEquals(epicTask.getID(), subTaskTwo.getRelatedEpicTaskID()),
                () -> assertEquals(2, epicTask.getSubTasksID().size()),
                () -> assertEquals(subTaskOne.getID(), epicTask.getSubTasksID().get(0)),
                () -> assertEquals(subTaskTwo.getID(), epicTask.getSubTasksID().get(1)));
    }
}
