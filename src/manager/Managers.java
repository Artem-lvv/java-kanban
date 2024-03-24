package manager;

import api.in.HttpTaskServer;

public class Managers {
    public static TaskManager newInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager newDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager newFileBackedTaskManager(String pathFile) {
        return new FileBackedTaskManager(pathFile);
    }

    public static HttpTaskServer newHttpTaskServer(TaskManager taskManager) {
        return new HttpTaskServer(taskManager);
    }

}
