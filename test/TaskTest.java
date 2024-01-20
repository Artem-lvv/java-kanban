import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;
import task.Task;
import task.TypeTask;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    private final TaskManager tm = Managers.getDefault();

    @Test
    void shouldReturnEquals() {
        Task taskOne = new Task("Task test", "Описание Task test");
        assertEquals(taskOne, taskOne);
    }



}
