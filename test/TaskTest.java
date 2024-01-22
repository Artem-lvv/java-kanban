import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void shouldReturnEquals() {
        Task task = new Task("Task test", "Описание Task test");
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        Task getTask = taskManager.getTaskByID(task.getID());

        assertEquals(task, getTask);
    }
}
