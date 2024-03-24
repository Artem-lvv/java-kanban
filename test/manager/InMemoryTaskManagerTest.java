package manager;

import exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private LocalDateTime defaultStartTime;
    private int thirtyMinutes;
    private int fourtyMinutes;
    private Duration durationThirtyThreeMinutes;
    private Duration durationThirtyMinutes;
    private Duration durationTwentyMinutes;
    private TaskManager taskManager;
    private Task task;
    private EpicTask epicTask;
    private SubTask subTaskOne;
    private SubTask subTaskTwo;
    private SubTask subTaskThree;
    private SubTask subTaskFour;
    private Duration durationTenMinutes;


    @BeforeEach
    void beforeEach() {
        defaultStartTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        thirtyMinutes = 30;
        fourtyMinutes = 40;
        durationThirtyThreeMinutes = Duration.ofMinutes(33);
        durationThirtyMinutes = Duration.ofMinutes(30);
        durationTwentyMinutes = Duration.ofMinutes(30);
        durationTenMinutes = Duration.ofMinutes(10);

        taskManager = Managers.newInMemoryTaskManager();
        task = new Task("Task test", "Описание Task");
        epicTask = new EpicTask("EpicTask test", "Описание EpicTask test");
        subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTask.getID());
        subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTask.getID());
        subTaskThree = new SubTask("SubTask test 3", "Описание SubTask test 3", epicTask.getID());
        subTaskFour = new SubTask("SubTask test 4", "Описание SubTask test 4", epicTask.getID());

        taskManager.addEpicTask(epicTask);

        taskManager.addSubTask(subTaskOne);
        taskManager.addSubTask(subTaskTwo);
        taskManager.addTask(task);

        // add history
        taskManager.getEpicTaskByID(epicTask.getID());
        taskManager.getSubTaskByID(subTaskOne.getID());
        taskManager.getTaskByID(task.getID());
        taskManager.getSubTaskByID(subTaskTwo.getID());
    }

    @Test
    void addTasks() {
        assertAll("add tasks",
                () -> assertEquals(1, taskManager.getListTasks().size()),
                () -> assertEquals(1, taskManager.getListEpicTasks().size()),
                () -> assertEquals(2, taskManager.getListSubTasks().size()));
    }

    @Test
    void getTasks() {
        assertAll("get tasks",
                () -> assertEquals(task, taskManager.getTaskByID(task.getID()).get()),
                () -> assertEquals(epicTask, taskManager.getEpicTaskByID(epicTask.getID()).get()),
                () -> assertEquals(subTaskOne, taskManager.getSubTaskByID(subTaskOne.getID()).get()),
                () -> assertEquals(subTaskTwo, taskManager.getSubTaskByID(subTaskTwo.getID()).get()));
    }

    @Test
    void deleteTask() {
        taskManager.deleteTaskByID(subTaskOne.getID());

        assertAll("delete task",
                () -> assertEquals(1, epicTask.getIdSubTaskList().size()),
                () -> assertEquals(subTaskTwo.getID(), epicTask.getIdSubTaskList().get(0)));
    }

    @Test
    void shiftAndUpdateEpicStatus() {
        assertEquals(TaskStatus.NEW, epicTask.getStatus());

        subTaskOne.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTaskOne);

        assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());

        subTaskOne.setStatus(TaskStatus.DONE);
        taskManager.deleteTaskByID(subTaskTwo.getID());

        assertEquals(TaskStatus.DONE, epicTask.getStatus());
    }

    @Test
    void history() {
        assertAll("get history",
                () -> assertEquals(4, taskManager.getHistory().size()),
                () -> assertEquals(subTaskTwo, taskManager.getHistory().get(0)));

        taskManager.deleteTaskByID(epicTask.getID());

        assertAll("delete history",
                () -> assertEquals(1, taskManager.getHistory().size()),
                () -> assertEquals(task, taskManager.getHistory().get(0)));

    }

    @Test
    void updateTimeStartAndDurationTaskAndSubTask() {
        Duration durationFiveDays = Duration.ofDays(5);
        LocalDateTime startTimePlusThreeDays = defaultStartTime.plusDays(3);

        task.setStartTime(defaultStartTime);
        task.setDuration(Duration.ofMinutes(thirtyMinutes));

        subTaskOne.setStartTime(defaultStartTime.plusHours(1));
        subTaskOne.setDuration(Duration.ofMinutes(fourtyMinutes));

        subTaskThree.setStartTime(defaultStartTime);
        subTaskThree.setDuration(durationFiveDays); // long time task

        subTaskFour.setStartTime(startTimePlusThreeDays);
        subTaskFour.setDuration(durationTenMinutes);

        assertAll("adding start time and duration to Task and Subtask, and get end time task",
                () -> assertEquals(defaultStartTime, task.getStartTime()),
                () -> assertEquals(thirtyMinutes, task.getDuration().toMinutes()),
                () -> assertEquals(defaultStartTime.plusMinutes(thirtyMinutes), task.getEndTime()),
                () -> assertEquals(defaultStartTime.plusHours(1), subTaskOne.getStartTime()),
                () -> assertEquals(fourtyMinutes, subTaskOne.getDuration().toMinutes()),
                () -> assertEquals(defaultStartTime.plusMinutes(fourtyMinutes).plusHours(1),
                        subTaskOne.getEndTime()));

        taskManager.updateTask(subTaskOne);
        taskManager.updateTask(subTaskTwo); // there are empty time fields in the subtask

        assertAll("EpicTask update and get startTime, endTime and duration // equals SubTaskOne",
                () -> assertEquals(subTaskOne.getStartTime(), epicTask.getStartTime()),
                () -> assertEquals(subTaskOne.getEndTime(), epicTask.getEndTime()),
                () -> assertEquals(subTaskOne.getDuration(), epicTask.getDuration()));

        taskManager.addSubTask(subTaskThree); // long time task

        assertAll("EpicTask update and get startTime, endTime and duration // equals subTaskThree",
                () -> assertEquals(subTaskThree.getStartTime(), epicTask.getStartTime()),
                () -> assertEquals(subTaskThree.getEndTime(), epicTask.getEndTime()),
                () -> assertEquals(subTaskThree.getDuration(), epicTask.getDuration()));

    }

    @Test
    void getPrioritizedTasks() {
        LocalDateTime startTimePlusOneDay = defaultStartTime.plusDays(1);
        LocalDateTime startTimePlusFourDay = defaultStartTime.plusDays(4);

        task.setStartTime(defaultStartTime);
        task.setDuration(Duration.ofMinutes(thirtyMinutes));
        subTaskOne.setStartTime(startTimePlusFourDay);
        subTaskOne.setDuration(durationTwentyMinutes);

        subTaskFour.setStartTime(startTimePlusOneDay);
        subTaskFour.setDuration(durationTenMinutes);

        taskManager.updateTask(task);
        taskManager.updateTask(subTaskOne);
        taskManager.updateTask(subTaskTwo); // empty time task // update
        taskManager.addSubTask(subTaskThree); // empty time task // addSubTask
        taskManager.addSubTask(subTaskFour);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> correctSequence = List.of(task, subTaskFour, subTaskOne);

        assertAll("no tasks with empty start date",
                () -> assertFalse(prioritizedTasks.contains(subTaskTwo)),
                () -> assertFalse(prioritizedTasks.contains(subTaskThree)));

        assertAll("getPrioritizedTasks returns the correct sequence",
                () -> assertEquals(correctSequence, prioritizedTasks));
    }

    @Test
    void checkTaskIntersectionInTime() {
        subTaskFour.setStartTime(defaultStartTime);
        subTaskFour.setDuration(durationThirtyThreeMinutes);
        taskManager.addSubTask(subTaskFour);

        LocalDateTime startTimePlusSixtyFourMinutes = defaultStartTime.plusMinutes(64);
        task.setStartTime(startTimePlusSixtyFourMinutes);
        task.setDuration(durationThirtyMinutes);
        taskManager.updateTask(task);

        subTaskThree.setStartTime(defaultStartTime);
        subTaskThree.setDuration(durationTwentyMinutes);

        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) taskManager;

        assertAll("add task in variable temporaryTaskWindowMap",
                () -> assertTrue(inMemoryTaskManager.getTemporaryTaskWindowMap()
                        .containsKey(defaultStartTime)),
                () -> assertTrue(inMemoryTaskManager.getTemporaryTaskWindowMap()
                        .containsKey(defaultStartTime.plusMinutes(InMemoryTaskManager.DEFAULT_WINDOW_TIME))),
                () -> assertTrue(inMemoryTaskManager.getTemporaryTaskWindowMap()
                        .containsKey(defaultStartTime.plusMinutes(InMemoryTaskManager.DEFAULT_WINDOW_TIME * 2))),
                () -> assertEquals(subTaskFour, inMemoryTaskManager.getTemporaryTaskWindowMap()
                        .get(defaultStartTime)),
                () -> assertEquals(subTaskFour, inMemoryTaskManager.getTemporaryTaskWindowMap()
                        .get(defaultStartTime.plusMinutes(InMemoryTaskManager.DEFAULT_WINDOW_TIME))),
                () -> assertEquals(subTaskFour, inMemoryTaskManager.getTemporaryTaskWindowMap()
                        .get(defaultStartTime.plusMinutes(InMemoryTaskManager.DEFAULT_WINDOW_TIME * 2))));

        assertAll("сorrect quantity",
                () -> assertEquals(2, inMemoryTaskManager.getTemporaryTaskWindowMap().values().stream()
                .filter(taskStream -> taskStream.equals(task)).toList().size()));

        taskManager.deleteTaskByID(task.getID());

        assertAll("delete",
                () -> assertEquals(0, inMemoryTaskManager.getTemporaryTaskWindowMap().values().stream()
                        .filter(taskStream -> taskStream.equals(task)).toList().size()));

        assertAll("intersection of tasks",
                () -> assertThrows(ValidationException.class, () -> taskManager.addSubTask(subTaskThree)));

    }


}
