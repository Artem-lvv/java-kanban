package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.util.Optional;

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
        Optional<EpicTask> getEpicTask = taskManager.getEpicTaskByID(epicTask.getID());
        assertEquals(epicTask, getEpicTask.get());
    }

    @Test
    void addSubTaskInEpicTask() {
        SubTask subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTask.getID());
        SubTask subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTask.getID());
        epicTask.addSubTask(subTaskOne.getID());
        epicTask.addSubTask(subTaskTwo.getID());

        assertAll("add two SubTask in EpicTask",
                () -> assertEquals(epicTask.getID(), subTaskOne.getIdRelatedEpicTask()),
                () -> assertEquals(epicTask.getID(), subTaskTwo.getIdRelatedEpicTask()),
                () -> assertEquals(2, epicTask.getIdSubTaskList().size()),
                () -> assertEquals(subTaskOne.getID(), epicTask.getIdSubTaskList().get(0)),
                () -> assertEquals(subTaskTwo.getID(), epicTask.getIdSubTaskList().get(1)));
    }


}
