import org.junit.jupiter.api.Test;
import task.TaskStatus;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {

    private final EpicTask epicTask = new EpicTask("Epic task", "Описание EpicTask");
    private final SubTask subTask = new SubTask("SubTask test", "Описание SubTask test", epicTask);

    @Test
    void shouldReturnEquals() {
        assertEquals(subTask, subTask);
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
