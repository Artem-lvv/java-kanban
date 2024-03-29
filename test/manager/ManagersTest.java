package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    void initializationTaskManager() {
        TaskManager taskManager = Managers.newInMemoryTaskManager();
        assertAll("Checking initialization of TaskManager fields",
                () -> assertEquals(0, taskManager.getListTasks().size()),
                () -> assertEquals(0, taskManager.getListEpicTasks().size()),
                () -> assertEquals(0, taskManager.getListEpicTasks().size()),
                () -> assertEquals(0, taskManager.getHistory().size()));
    }

    @Test
    void initializationHistoryManager() {
        HistoryManager hm = Managers.newDefaultHistory();
        assertEquals(0, hm.getHistory().size());
    }
}
