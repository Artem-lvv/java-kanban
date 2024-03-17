package manager;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    private TaskManager taskManager;
    private Task task;
    private EpicTask epicTask;
    private SubTask subTaskOne;
    private SubTask subTaskTwo;
    Path pathFile;
    String fileData;

    @BeforeEach
    void beforeEach() throws IOException {

        LocalDateTime timeNowDefault = LocalDateTime.now();
        Duration durationDefault = Duration.ofMinutes(30);
        String defaultTimeString = String.format("%s,%s,%d", timeNowDefault.toString(),
                timeNowDefault.plus(durationDefault).toString(), durationDefault.toMinutes());

        pathFile = Files.createTempFile("TestFile", "Tasks.csv");

        taskManager = Managers.getFileBackedTaskManager(pathFile.toString());

        task = new Task("Task test", "Описание Task");
        task.setId(1);
        task.setStartTime(timeNowDefault);
        task.setDuration(durationDefault);

        epicTask = new EpicTask("EpicTask test", "Описание EpicTask test");
        epicTask.setId(2);
        subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTask.getID());
        subTaskOne.setId(3);
        subTaskOne.setStartTime(timeNowDefault);
        subTaskOne.setDuration(durationDefault);
        subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTask.getID());
        subTaskTwo.setId(4);
        subTaskTwo.setStartTime(timeNowDefault);
        subTaskTwo.setDuration(durationDefault);

        taskManager.addEpicTask(epicTask);

        taskManager.addSubTask(subTaskOne);
        taskManager.addSubTask(subTaskTwo);
        taskManager.addTask(task);

        // add history
        taskManager.getEpicTaskByID(epicTask.getID());
        taskManager.getSubTaskByID(subTaskOne.getID());
        taskManager.getTaskByID(task.getID());
        taskManager.getSubTaskByID(subTaskTwo.getID());

        fileData = "id,type,name,status,description,epic,subTask\n" +
                "1,TASK,Task test,NEW,Описание Task,null,null,"
                + defaultTimeString + "\n"
                + "2,EPIC,EpicTask test,NEW,Описание EpicTask test,null,3-4,"
                + defaultTimeString + "\n"
                + "3,SUBTASK,SubTask test 1,NEW,Описание SubTask test 1,2,null,"
                + defaultTimeString + "\n"
                + "4,SUBTASK,SubTask test 2,NEW,Описание SubTask test 2,2,null,"
                + defaultTimeString + "\n"
                + "history\n" +
                "4,1,3,2,\n";
    }

    @Test
    void saveTaskManagerToFile() {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathFile.toFile()))) {

            while (reader.ready()) {
                stringBuilder.append(reader.readLine()).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(fileData, stringBuilder.toString());
    }

    @Test
    void loadTaskManagerFromFile() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile.toFile()))) {
            writer.write(fileData);
        }

        TaskManager newTaskManager = Managers.getFileBackedTaskManager(pathFile.toString());

        assertAll("load Task manager from file",
                () -> assertEquals(1, newTaskManager.getListTasks().size()),
                () -> assertEquals(1, newTaskManager.getListEpicTasks().size()),
                () -> assertEquals(2, newTaskManager.getListSubTasks().size()),
                () -> assertEquals(4, newTaskManager.getHistory().size()),
                () -> assertEquals(subTaskTwo, newTaskManager.getHistory().get(0)));
    }

}
