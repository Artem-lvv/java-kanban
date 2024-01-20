import org.junit.jupiter.api.Test;
import task.Task;
import task.relatedTask.EpicTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTaskTest {
    private final EpicTask epicTask = new EpicTask("EpicTask test", "Описание EpicTask test");

    @Test
    void shouldReturnEquals() {
        assertEquals(epicTask, epicTask);
    }

  /*  @Test
    void epicTaskCannotContainEpicTask() {
        assertThrows(Exception.class, () -> {epicTask.addSubTask(epicTask)});
    }*/

}
