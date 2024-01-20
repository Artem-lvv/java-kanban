import org.junit.jupiter.api.Test;
import task.Task;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubTaskTest {

    @Test
    void shouldReturnEquals() {
        EpicTask epicTask = new EpicTask("Epic task", "Описание EpicTask");
        SubTask subTask = new SubTask("SubTask test", "Описание SubTask test", epicTask);
        assertEquals(subTask, subTask);
    }

}
