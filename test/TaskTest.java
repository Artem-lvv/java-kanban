import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void shouldReturnEquals() {
        Task taskOne = new Task("Task test", "Описание Task test");
        assertEquals(taskOne, taskOne);
    }
}
