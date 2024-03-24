package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private List<Task> tasks;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.newDefaultHistory();
        tasks = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Task test " + i, "Описание Task test " + i);

            historyManager.add(task);
            tasks.add(task);
        }
    }

    @Test
    void addTask() {
        assertTrue(historyManager.getHistory().contains(tasks.get(4)));
    }

    @Test
    void sizeHistory() {
        assertEquals(5, historyManager.getHistory().size());
    }

    @Test
    void removeTask() {
        Task task = tasks.get(3);
        historyManager.remove(task.getID());

        assertAll("remove task",
                () -> assertEquals(4, historyManager.getHistory().size()),
                () -> assertFalse(historyManager.getHistory().contains(task)));
    }

    @Test
    void sequenceHistory() {
        List<Task> viewTasks = List.of(tasks.get(3),
                tasks.get(1),
                tasks.get(4),
                tasks.get(2),
                tasks.get(0));

        historyManager.add(tasks.get(1));
        historyManager.add(tasks.get(3));

        assertEquals(viewTasks, historyManager.getHistory());
    }

}
