package api.in.handler;

import api.in.HttpTaskServer;
import com.google.gson.Gson;
import dto.task.TaskDto;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;
import util.DTOMapping;
import util.GsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private Task task;
    private EpicTask epicTask;
    private SubTask subTaskOne;
    private SubTask subTaskTwo;
    private Path pathFile;
    LocalDateTime timeDefault;
    Duration durationDefault;

    @BeforeEach
    void beforeEach() throws IOException {

        timeDefault = LocalDateTime.of(2024, 1, 1, 0, 0);
        durationDefault = Duration.ofMinutes(30);

        pathFile = Files.createTempFile("TestFile", "Tasks.csv");

        taskManager = Managers.newFileBackedTaskManager(pathFile.toString());
        httpTaskServer = Managers.newHttpTaskServer(taskManager);
        httpTaskServer.start();

        task = new Task("Task test", "Описание Task");
        task.setId(1);
        task.setStartTime(timeDefault);
        task.setDuration(durationDefault);

        epicTask = new EpicTask("EpicTask test", "Описание EpicTask test");
        epicTask.setId(2);
        subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTask.getID());
        subTaskOne.setId(3);
        subTaskOne.setStartTime(timeDefault.plus(durationDefault));
        subTaskOne.setDuration(durationDefault);
        subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTask.getID());
        subTaskTwo.setId(4);
        subTaskTwo.setStartTime(timeDefault.plus(durationDefault).plus(durationDefault)); // plus hours
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
    }

    @Test
    void prioritizedTasks() throws IOException, InterruptedException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        List<TaskDto> taskDtoList = prioritizedTasks.stream().map(DTOMapping::mapToTaskDto).toList();
        Gson gson = GsonUtil.newGsonDefault();
        String bodyJson = gson.toJson(taskDtoList);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);

        int codeTwoHundred = 200;

        assertAll("history handler - code 200",
                () -> assertEquals(codeTwoHundred, response.statusCode()),
                () -> assertEquals(bodyJson, response.body()));

        httpTaskServer.stop();
    }

    @Test
    void prioritizedIsEmpty() throws IOException, InterruptedException {
        taskManager.deleteAllTaskByType(TypeTask.TASK);
        taskManager.deleteAllTaskByType(TypeTask.SUBTASK);
        taskManager.deleteAllTaskByType(TypeTask.EPIC);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);

        int codeTwoHundredFour = 204;
        String bodyEmpty = "";

        assertAll("history handler empty - code 204",
                () -> assertEquals(codeTwoHundredFour, response.statusCode()),
                () -> assertEquals(bodyEmpty, response.body()));

        httpTaskServer.stop();
    }

}
