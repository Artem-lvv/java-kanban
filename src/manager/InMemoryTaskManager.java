package manager;

import exception.ValidationException;
import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class InMemoryTaskManager implements TaskManager {
    public static final int DEFAULT_WINDOW_TIME = 15;
    private final Map<Integer, Task> idToTaskMap;
    private final Map<Integer, EpicTask> idToEpicTaskMap;
    private final Map<Integer, SubTask> idToSubTaskMap;
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasksSet;
    private final Map<LocalDateTime, Task> temporaryTaskWindowMap;

    public InMemoryTaskManager() {
        idToTaskMap = new HashMap<>();
        idToEpicTaskMap = new HashMap<>();
        idToSubTaskMap = new HashMap<>();
        historyManager = Managers.newDefaultHistory();
        prioritizedTasksSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        temporaryTaskWindowMap = new HashMap<>();
    }

    @Override
    public List<Task> getListTasks() {
        return new ArrayList<>(idToTaskMap.values());
    }

    @Override
    public List<EpicTask> getListEpicTasks() {
        return new ArrayList<>(idToEpicTaskMap.values());
    }

    @Override
    public List<SubTask> getListSubTasks() {
        return new ArrayList<>(idToSubTaskMap.values());
    }

    @Override
    public void deleteAllTaskByType(TypeTask typeTask) {
        switch (typeTask) {
            case TASK:
                idToTaskMap.forEach((id, task) -> {
                    historyManager.remove(id);
                    prioritizedTasksSet.remove(task);
                });
                idToTaskMap.clear();
                break;
            case EPIC:
                idToEpicTaskMap.forEach((id, epicTask) -> {
                    historyManager.remove(id);
                    prioritizedTasksSet.remove(epicTask);
                });
                idToSubTaskMap.forEach((id, subTask) -> {
                    historyManager.remove(id);
                    prioritizedTasksSet.remove(subTask);
                });
                idToEpicTaskMap.clear();
                idToSubTaskMap.clear();
                break;
            case SUBTASK:
                idToEpicTaskMap.forEach((id, epicTask) -> {
                    epicTask.deleteAllSubTasksID();
                    epicTask.setStatus(TaskStatus.NEW);
                });
                idToSubTaskMap.forEach((id, subTask) -> {
                    historyManager.remove(id);
                    prioritizedTasksSet.remove(subTask);
                });
                idToSubTaskMap.clear();
                break;
            default:
                break;
        }
    }

    @Override
    public Optional<Task> getTaskByID(Integer id) {
        Task task = idToTaskMap.getOrDefault(id, null);
        historyManager.add(task);
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<EpicTask> getEpicTaskByID(Integer id) {
        EpicTask epicTask = idToEpicTaskMap.getOrDefault(id, null);
        historyManager.add(epicTask);
        return Optional.ofNullable(epicTask);
    }

    @Override
    public Optional<SubTask> getSubTaskByID(Integer id) {
        SubTask subTask = idToSubTaskMap.getOrDefault(id, null);
        historyManager.add(subTask);
        return Optional.ofNullable(subTask);
    }

    @Override
    public void addTask(Task task) throws ValidationException {
        if (task != null && !idToTaskMap.containsKey(task.getID())) {
            if (checkTaskIntersectionInTime(task, "add")) {
                throw new ValidationException("Задачи пересекается по времени. Задача не добавлена");
            }

            idToTaskMap.put(task.getID(), task);
            if (task.getStartTime() != null) {
                prioritizedTasksSet.add(task);
            }
        }
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        if (epicTask != null && !idToEpicTaskMap.containsKey(epicTask.getID())) {
            idToEpicTaskMap.put(epicTask.getID(), epicTask);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) throws ValidationException {
        if (subTask != null && !idToSubTaskMap.containsKey(subTask.getID())) {

            if (checkTaskIntersectionInTime(subTask, "add")) {
                throw new ValidationException("Задачи пересекаются по времени. Подзадача не добавлена.");
            }

            idToSubTaskMap.put(subTask.getID(), subTask);
            idToEpicTaskMap.getOrDefault(subTask.getIdRelatedEpicTask(), null).addSubTask(subTask.getID());

            checkAndUpdateFieldsEpicTask(subTask);
            if (subTask.getStartTime() != null) {
                prioritizedTasksSet.add(subTask);
            }

        }
    }

    @Override
    public void updateTask(Task task) throws ValidationException {
        if (task == null) {
            return;
        }

        final String statusUpdate = "update";

        switch (task.getTypeTask()) {
            case TASK:
                if (checkTaskIntersectionInTime(task, statusUpdate)) {
                    throw new ValidationException("Задача пересекается по времени. Обновление не выполнено.");
                }

                idToTaskMap.put(task.getID(), task);
                if (task.getStartTime() != null) {
                    prioritizedTasksSet.add(task);
                }
                break;
            case EPIC:
                idToEpicTaskMap.put(task.getID(), (EpicTask) task);
                break;
            case SUBTASK:
                if (checkTaskIntersectionInTime(task, statusUpdate)) {
                    throw new ValidationException("Задача пересекается по времени. Обновление не выполнено.");
                }

                idToSubTaskMap.put(task.getID(), (SubTask) task);
                EpicTask epicTask = idToEpicTaskMap
                        .getOrDefault((((SubTask) task).getIdRelatedEpicTask()), null);
                epicTask.addSubTask(task.getID()); // updating related tasks
                checkAndUpdateFieldsEpicTask((SubTask) task);
                if (task.getStartTime() != null) {
                    prioritizedTasksSet.add(task);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void deleteTaskByID(Integer id) {
        if (id == null) {
            return;
        }

        final String statusDelete = "delete";

        if (idToTaskMap.containsKey(id)) {
            historyManager.remove(id);

            if (idToTaskMap.get(id).getStartTime() != null) {
                checkTaskIntersectionInTime(idToTaskMap.get(id), statusDelete);
                prioritizedTasksSet.remove(idToTaskMap.get(id));
            }

            idToTaskMap.remove(id);
        } else if (idToEpicTaskMap.containsKey(id)) {
            EpicTask epicTask = idToEpicTaskMap.getOrDefault(id, null);
            List<Integer> subTasksIdEpicTask = epicTask.getIdSubTaskList();

            subTasksIdEpicTask.forEach(idSubTask -> {
                prioritizedTasksSet.remove(idToSubTaskMap.get(idSubTask));
                historyManager.remove(idSubTask);
                idToSubTaskMap.remove(idSubTask);
            });

            prioritizedTasksSet.remove(idToEpicTaskMap.get(id));
            historyManager.remove(id);
            idToEpicTaskMap.remove(id);
        } else if (idToSubTaskMap.containsKey(id)) {
            SubTask subTask = idToSubTaskMap.getOrDefault(id, null);

            if (subTask.getStartTime() != null) {
                checkTaskIntersectionInTime(idToSubTaskMap.get(id), statusDelete);
                prioritizedTasksSet.remove(idToSubTaskMap.get(id));
            }

            EpicTask epicTaskByID = idToEpicTaskMap.getOrDefault(subTask.getIdRelatedEpicTask(), null);
            epicTaskByID.deleteSubTaskToID(id);
            checkAndUpdateFieldsEpicTask(subTask);

            historyManager.remove(id);
            idToSubTaskMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        if (prioritizedTasksSet.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(prioritizedTasksSet);
    }

    public Map<LocalDateTime, Task> getTemporaryTaskWindowMap() {
        return temporaryTaskWindowMap;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void checkAndUpdateFieldsEpicTask(SubTask subTask) {
        EpicTask epicTask = idToEpicTaskMap.getOrDefault(subTask.getIdRelatedEpicTask(), null);
        List<Integer> subTasksID = epicTask.getIdSubTaskList();

        if (subTasksID.isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        Supplier<Stream<SubTask>> streamSubTask = () -> subTasksID.stream()
                .map(idToSubTaskMap::get)
                .filter(Objects::nonNull);

        updateStatusEpicTask(streamSubTask, subTasksID, epicTask);
        updateStartAndEndTimeEpicTask(streamSubTask, epicTask);
    }

    private void updateStartAndEndTimeEpicTask(Supplier<Stream<SubTask>> streamSubTask, EpicTask epicTask) {
        Optional<LocalDateTime> localDateTimeStart = streamSubTask.get()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());

        Optional<LocalDateTime> localDateTimeEnd = streamSubTask.get()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        localDateTimeStart.ifPresent(epicTask::setStartTime);
        localDateTimeEnd.ifPresent(epicTask::setEndTime);

        if (localDateTimeStart.isPresent() && localDateTimeEnd.isPresent()) {
            epicTask.setDuration(Duration.between(localDateTimeStart.get(), localDateTimeEnd.get()));
        }
    }

    private void updateStatusEpicTask(Supplier<Stream<SubTask>> streamSubTask,
                                      List<Integer> subTasksID, EpicTask epicTask) {
        int countSubTaskNEW = (int) streamSubTask.get()
                .filter(subTaskStream -> subTaskStream.getStatus() == TaskStatus.NEW)
                .count();

        int countSubTaskDONE = (int) streamSubTask.get()
                .filter(subTaskStream -> subTaskStream.getStatus() == TaskStatus.DONE)
                .count();

        if (subTasksID.size() == countSubTaskNEW) {
            epicTask.setStatus(TaskStatus.NEW);
        } else if (subTasksID.size() == countSubTaskDONE) {
            epicTask.setStatus(TaskStatus.DONE);
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private boolean checkTaskIntersectionInTime(Task task, String typeOperationString) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return false;
        }

        LocalDateTime startTimeTask = task.getStartTime();
        LocalDateTime endTimeTask = task.getStartTime().plusMinutes(task.getDuration().toMinutes());
        int startWindowMinutes = (startTimeTask.getMinute() / DEFAULT_WINDOW_TIME) * DEFAULT_WINDOW_TIME;
        LocalDateTime startWindow = startTimeTask.truncatedTo(ChronoUnit.HOURS).plusMinutes(startWindowMinutes);
        LocalDateTime changeableStartWindow = startWindow;
        int countWindow = (int) (task.getDuration().toMinutes() / DEFAULT_WINDOW_TIME)
                + (task.getDuration().toMinutes() % DEFAULT_WINDOW_TIME > 0 ? 1 : 0);

        LocalDateTime endWindow = startWindow.plusMinutes(countWindow * DEFAULT_WINDOW_TIME);

        List<LocalDateTime> listWindow = new ArrayList<>(countWindow);
        for (int i = 0; i < countWindow; i++) {
            listWindow.add(changeableStartWindow);
            changeableStartWindow = changeableStartWindow.plusMinutes(DEFAULT_WINDOW_TIME);
        }

        switch (typeOperationString) {
            case "add":
            case "update":
                if (!checkingWindowIntersectionsIDates(startWindow, endWindow, startTimeTask, endTimeTask)) {
                    listWindow.forEach(localDateTimeStream -> temporaryTaskWindowMap.put(localDateTimeStream, task));
                } else {
                    return true;
                }
                break;
            case "delete":
                Map<LocalDateTime, Task> copyTemporaryTaskWindowMap = new HashMap<>(temporaryTaskWindowMap.entrySet()
                        .stream()
                        .filter(v -> !v.getValue().equals(task))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

                temporaryTaskWindowMap.clear();
                temporaryTaskWindowMap.putAll(copyTemporaryTaskWindowMap);
                break;
            default:
                break;
        }
        return false;
    }

    private boolean checkingWindowIntersectionsIDates(LocalDateTime startWindow, LocalDateTime endWindow,
                                                      LocalDateTime startTimeTask, LocalDateTime endTimeTask) {
        Supplier<Stream<Task>> streamPrioritizedTasks = () -> getPrioritizedTasks().stream()
                .filter(taskStream -> taskStream.getDuration() != null);

        List<LocalDateTime> intersectionList = streamPrioritizedTasks.get()
                .map(Task::getStartTime)
                .filter(taskStream -> temporaryTaskWindowMap.containsKey(startWindow)
                        || temporaryTaskWindowMap.containsKey(endWindow))
                .sorted().toList();

        if (intersectionList.isEmpty()) {
            return false;
        }

        // Checking the exact intersection
        long intersectionCount = streamPrioritizedTasks.get()
                .filter(taskStream -> {
                    LocalDateTime taskStreamTimeStart = taskStream.getStartTime();
                    LocalDateTime taskStreamEnd = taskStreamTimeStart.plusMinutes(taskStream.getDuration().toMinutes());

                    if ((taskStreamTimeStart.isBefore(startTimeTask) && taskStreamEnd.isAfter(startTimeTask))
                            || (taskStreamTimeStart.isBefore(endTimeTask) && taskStreamEnd.isAfter(endTimeTask))
                            || (taskStreamTimeStart.equals(startTimeTask) || taskStreamEnd.equals(endTimeTask))
                    ) {
                        return true;
                    } else return false;
                }).count();

        if (intersectionCount > 0) {
            return true;
        } else {
            return false;
        }
    }
}
