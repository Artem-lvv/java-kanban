package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_SIZE_HISTORY = 10;
    private final ArrayList<Task> historyViewTasks = new ArrayList<>(MAX_SIZE_HISTORY);

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyViewTasks.size() == MAX_SIZE_HISTORY) {
            historyViewTasks.remove(0);
        }
        historyViewTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyViewTasks);
    }
}
