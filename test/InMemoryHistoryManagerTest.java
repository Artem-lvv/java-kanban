import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    private final TaskManager taskManager = Managers.getDefault();

    @Test
    void history() {
        for (int i = 0; i <= 14; i++) {
            Task task = new Task("Task test " + i, "Описание Task test " + i);
            taskManager.addTask(task);
            taskManager.getTaskByID(task.getID());
        }
        assertEquals(10, taskManager.getHistory().size());
    }

}
