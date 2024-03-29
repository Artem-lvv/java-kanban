package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    private Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Task test", "Описание Task");
    }

    @Test
    void shouldReturnEquals() {
        TaskManager taskManager = Managers.newInMemoryTaskManager();
        taskManager.addTask(task);
        Optional<Task> taskFromTaskManager = taskManager.getTaskByID(task.getID());

        assertEquals(task, taskFromTaskManager.get());
    }

    @Test
    void settingFields() {
        task.setName("Set name");
        task.setDescription("Set description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertAll("set fields: ",
                () -> assertEquals("Set name", task.getName()),
                () -> assertEquals("Set description", task.getDescription()),
                () -> assertEquals(TaskStatus.IN_PROGRESS, task.getStatus()));
    }




}
