package api.in.handler.relatedTask;

import api.in.HttpTaskServer;
import com.google.gson.Gson;
import dto.relatedTask.EpicTaskDto;
import dto.relatedTask.SubTaskDto;
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
import util.VariablesConstant;

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

import static org.junit.jupiter.api.Assertions.*;

public class EpicTaskHandlerTest {
    public static final String PATH_ENDPOINT = "/epics";
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;
    private HttpResponse.BodyHandler<String> handler;
    private int codeTwoHundredFour;
    private int codeTwoHundred;
    private int codeTwoHundredOne;
    private int codeFourHundred;
    private int codeFourHundredFour;
    private int codeFourHundredSix;
    private String bodyEmpty;
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

        httpClient = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();

        codeTwoHundred = 200;
        codeTwoHundredOne = 201;
        codeTwoHundredFour = 204;
        codeFourHundred = 400;
        codeFourHundredFour = 404;
        codeFourHundredSix = 406;
        bodyEmpty = "";

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
    void getEpics() throws IOException, InterruptedException {
        List<EpicTask> tasksList = taskManager.getListEpicTasks();

        List<EpicTaskDto> taskDtoList = tasksList.stream().map(DTOMapping::mapToEpicTaskDto).toList();
        Gson gson = GsonUtil.newGsonDefault();
        String bodyJson = gson.toJson(taskDtoList);

        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseFirst = httpClient.send(request, handler);

        assertAll("getEpics handler - code 200",
                () -> assertEquals(codeTwoHundred, responseFirst.statusCode()),
                () -> assertEquals(bodyJson, responseFirst.body()));

        taskManager.deleteAllTaskByType(TypeTask.TASK);
        taskManager.deleteAllTaskByType(TypeTask.SUBTASK);
        taskManager.deleteAllTaskByType(TypeTask.EPIC);

        HttpResponse<String> responseSecond = httpClient.send(request, handler);
        String bodyEmpty = "";

        assertAll("getEpics handler empty - code 204",
                () -> assertEquals(codeTwoHundredFour, responseSecond.statusCode()),
                () -> assertEquals(bodyEmpty, responseSecond.body()));

        httpTaskServer.stop();
    }

    @Test
    void getEpicByID() throws IOException, InterruptedException {
        int taskID = epicTask.getID();

        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=555abc");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = httpClient.send(request, handler);

        assertAll("getEpicByID - code 400",
                () -> assertEquals(codeFourHundred, response.statusCode()),
                () -> assertEquals(VariablesConstant.INVALID_ID, response.body()));

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=" + Integer.MAX_VALUE);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseSecond = httpClient.send(request, handler);

        assertAll("getEpicByID - code 404",
                () -> assertEquals(codeFourHundredFour, responseSecond.statusCode()),
                () -> assertEquals(VariablesConstant.NOT_FOUND_TASK_BY_ID, responseSecond.body()));


        EpicTaskDto taskDto = DTOMapping.mapToEpicTaskDto(taskManager.getEpicTaskByID(taskID).get());
        String body = GsonUtil.newGsonDefault().toJson(taskDto);

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=" + taskID);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseThird = httpClient.send(request, handler);

        assertAll("getEpicByID - code 200",
                () -> assertEquals(codeTwoHundred, responseThird.statusCode()),
                () -> assertEquals(body, responseThird.body()));

        httpTaskServer.stop();
    }

    @Test
    void getSubTasksEpicByID() throws IOException, InterruptedException {
        int taskID = epicTask.getID();

        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=555abc");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = httpClient.send(request, handler);

        assertAll("getSubTasksEpicByID - code 400",
                () -> assertEquals(codeFourHundred, response.statusCode()),
                () -> assertEquals(VariablesConstant.INVALID_ID, response.body()));

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=" + Integer.MAX_VALUE);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseSecond = httpClient.send(request, handler);

        assertAll("getSubTasksEpicByID - code 404",
                () -> assertEquals(codeFourHundredFour, responseSecond.statusCode()),
                () -> assertEquals(VariablesConstant.NOT_FOUND_TASK_BY_ID, responseSecond.body()));


        EpicTaskDto taskDto = DTOMapping.mapToEpicTaskDto(taskManager.getEpicTaskByID(taskID).get());
        String body = GsonUtil.newGsonDefault().toJson(taskDto);

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=" + taskID + "/subtasks");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseThird = httpClient.send(request, handler);

        assertAll("getSubTasksEpicByID - code 200",
                () -> assertEquals(codeTwoHundred, responseThird.statusCode()));

        taskManager.deleteAllTaskByType(TypeTask.SUBTASK);

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=" + taskID + "/subtasks");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseFourth = httpClient.send(request, handler);

        assertAll("getSubTasksEpicByID - code 204",
                () -> assertEquals(codeTwoHundredFour, responseFourth.statusCode()));

        httpTaskServer.stop();
    }

    @Test
    void postEpicCreate() throws IOException, InterruptedException {
        String bodyUncorrected = "abcd";
        int taskID = epicTask.getID();

        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyUncorrected))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = httpClient.send(request, handler);

        assertAll("postEpicCreate - code 400",
                () -> assertEquals(codeFourHundred, response.statusCode()),
                () -> assertEquals(VariablesConstant.INCORRECT_DATA_FORMAT, response.body()));

        EpicTaskDto taskDto = DTOMapping.mapToEpicTaskDto(taskManager.getEpicTaskByID(taskID).get());
        String bodyCorrected = GsonUtil.newGsonDefault().toJson(taskDto);

        bodyUncorrected = bodyCorrected.replace("name", "abc"); // remove the name field

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyUncorrected))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseSecond = httpClient.send(request, handler);

        assertAll("postEpicCreate - code 400",
                () -> assertEquals(codeFourHundred, responseSecond.statusCode()),
                () -> assertEquals(VariablesConstant.INCORRECT_DATA_FORMAT, responseSecond.body()));

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyCorrected))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseThird = httpClient.send(request, handler);

        assertAll("postEpicCreate - code 200",
                () -> assertEquals(codeTwoHundred, responseThird.statusCode()));

        httpTaskServer.stop();
    }

    @Test
    void deleteEpicByID() throws IOException, InterruptedException {
        int taskID = epicTask.getID();

        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=" + "abc");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = httpClient.send(request, handler);

        assertAll("deleteSubTaskByID - code 400",
                () -> assertEquals(codeFourHundred, response.statusCode()),
                () -> assertEquals(VariablesConstant.INVALID_ID, response.body()));

        uri = URI.create("http://localhost:" + HttpTaskServer.PORT + PATH_ENDPOINT + "?id=" + taskID);
        HttpRequest requestSecond = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> responseSecond = httpClient.send(requestSecond, handler);

        assertAll("deleteSubTaskByID - code 200",
                () -> assertEquals(codeTwoHundred, responseSecond.statusCode()),
                () -> assertFalse(taskManager.getEpicTaskByID(taskID).isPresent()));

        httpTaskServer.stop();
    }
}
