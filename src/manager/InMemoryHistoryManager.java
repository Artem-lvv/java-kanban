package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> historyViewTasks;

    public InMemoryHistoryManager() {
        this.historyViewTasks = new ArrayList<>(10);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyViewTasks.size() == 10) {
            historyViewTasks.remove(0);
        }
        historyViewTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyViewTasks;
    }
}
