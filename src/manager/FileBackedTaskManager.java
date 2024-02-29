package manager;

import task.Task;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;
import util.CSVTaskFormatter;
import util.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path pathFile;
    private static final String DESCRIPTION_TASK_IN_FILE = "id,type,name,status,description,epic,subTask";
    private static final String DESCRIPTION_HISTORY_IN_FILE = "history";
    private final HashMap<Integer, Task> tasksFromFile = new HashMap<>();
    private final List<Integer> historyTasksFromFile = new ArrayList<>();

    public FileBackedTaskManager(String pathFileString) {
        super();
        this.pathFile = createPathToFileOrFile(pathFileString);
        loadFromFile(pathFile.toFile());
        loadTasksInTaskManager();
        loadHistoryInTaskManager();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void deleteAllTaskByType(TypeTask typeTask) {
        super.deleteAllTaskByType(typeTask);
        save();
    }

    @Override
    public Task getTaskByID(Integer id) {
        Task task = super.getTaskByID(id);
        save();
        return task;
    }

    @Override
    public EpicTask getEpicTaskByID(Integer id) {
        EpicTask epicTask = super.getEpicTaskByID(id);
        save();
        return epicTask;
    }

    @Override
    public SubTask getSubTaskByID(Integer id) {
        SubTask subTask = super.getSubTaskByID(id);
        save();
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskByID(Integer id) {
        super.deleteTaskByID(id);
        save();
    }

    private Path createPathToFileOrFile(String pathFileString) throws RuntimeException {
        Path path;

        try {
            path = Paths.get(pathFileString);
        } catch (InvalidPathException e) {
            throw new InvalidPathException(pathFileString, "Invalid path to file or directory");
        }

        if (Files.isRegularFile(path) && path.endsWith(".csv")) {
            return path;
        } else if (Files.isDirectory(path)) {
            try {
                path = Files.createFile(Paths.get(pathFileString, "Tasks.csv"));
            } catch (IOException exception) {
                throw new RuntimeException("Failed to create file. Tasks.csv exists in the directory - "
                        + pathFileString, exception);
            }
        }

        return path;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile.toFile(), StandardCharsets.UTF_8))) {
            writeAllTasks(writer);
            writeHistory(writer);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void writeAllTasks(BufferedWriter writer) throws ManagerSaveException {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getListTasks());
        allTasks.addAll(getListEpicTasks());
        allTasks.addAll(getListSubTasks());
        allTasks.sort(Comparator.comparingInt(Task::getID));

        try {
            writer.write(DESCRIPTION_TASK_IN_FILE);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        if (allTasks.isEmpty()) {
            return;
        }

        allTasks.forEach(task -> {
            try {
                writer.write("\n" + CSVTaskFormatter.taskToString(task));
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        });
    }

    private void writeHistory(BufferedWriter writer) throws ManagerSaveException {
        List<Task> historyList = getHistory();

        try {
            writer.write("\n" + DESCRIPTION_HISTORY_IN_FILE + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        if (historyList.isEmpty()) {
            return;
        }

        historyList.forEach(task -> {
            try {
                writer.write(task.getID() + ",");
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        });
    }

    private void loadFromFile(File file) throws RuntimeException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();

                if (line.equals(DESCRIPTION_HISTORY_IN_FILE)) {
                    line = reader.readLine();

                    historyTasksFromFile.addAll(Arrays.stream(line.split(","))
                            .map(Integer::valueOf).toList().reversed());

                    return;
                }

                if (line.equals(DESCRIPTION_TASK_IN_FILE)) {
                    line = reader.readLine();
                }

                Task task = CSVTaskFormatter.stringToTask(line);
                tasksFromFile.put(task.getID(), task);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadTasksInTaskManager() {
        if (tasksFromFile.isEmpty()) {
            return;
        }

        Supplier<Stream<Task>> streamTask = () -> tasksFromFile.values().stream();

        streamTask.get().filter(task -> task.getTypeTask() == TypeTask.TASK).forEach(super::addTask);
        streamTask.get().filter(task -> task.getTypeTask() == TypeTask.EPIC).map(EpicTask.class::cast)
                .forEach(super::addEpicTask);
        streamTask.get().filter(task -> task.getTypeTask() == TypeTask.SUBTASK).map(SubTask.class::cast)
                .forEach(super::addSubTask);
    }

    private void loadHistoryInTaskManager() {
        if (historyTasksFromFile.isEmpty()) {
            return;
        }

        HistoryManager historyManager = getHistoryManager();

        historyTasksFromFile.forEach(taskId -> historyManager.add(tasksFromFile.get(taskId)));
    }
}
