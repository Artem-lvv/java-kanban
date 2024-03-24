package api.in.handler.task;

import api.in.Endpoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dto.task.TaskDto;
import exception.ManagerSaveException;
import exception.ValidationException;
import manager.TaskManager;
import task.Task;
import util.Api;
import util.GsonUtil;
import util.DTOMapping;
import util.VariablesConstant;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TasksHandler implements HttpHandler {
    private final TaskManager taskManager;

    private HttpExchange httpExchange;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.httpExchange = exchange;

        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=utf-8");

        Endpoint endpoint = Api.getEndpoint(httpExchange.getRequestURI(), httpExchange.getRequestMethod(), exchange);

        switch (endpoint) {
            case GET_TASKS:
                getTasks();
                break;
            case GET_TASK_BY_ID:
                getTaskByID();
                break;
            case POST_TASK_CREATE:
                postTaskCreate();
                break;
            case POST_TASK_UPDATE_BY_ID:
                postTaskUpdateByID();
                break;
            case DELETE_TASK_BY_ID:
                deleteTaskByID();
                break;
            default:
                break;
        }
    }

    private void getTasks() throws IOException {
        List<Task> tasksList = taskManager.getListTasks();

        if (tasksList.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.LIST_TASKS_EMPTY, 204);
        } else {
            List<TaskDto> taskDtoList = tasksList.stream().map(DTOMapping::mapToTaskDto).toList();
            Gson gson = GsonUtil.newGsonDefault();
            String body = gson.toJson(taskDtoList);
            Api.writeResponse(httpExchange, body, 200);
        }
    }

    private void getTaskByID() throws IOException {
        Optional<Integer> taskId = Api.getIDTaskFromQueryParameterString(httpExchange);

        if (taskId.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.INVALID_ID, 400);
            return;
        }

        Optional<Task> taskOptional = taskManager.getTaskByID(taskId.get());

        if (taskOptional.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.NOT_FOUND_TASK_BY_ID, 404);
            return;
        }

        TaskDto taskDto = DTOMapping.mapToTaskDto(taskOptional.get());
        String body = GsonUtil.newGsonDefault().toJson(taskDto);

        Api.writeResponse(httpExchange, body, 200);
    }

    private void postTaskCreate() throws IOException {
        String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), Api.DEFAULT_CHARSET);
        JsonElement jsonElement = JsonParser.parseString(bodyRequest);

        if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
            Api.writeResponse(httpExchange, VariablesConstant.INCORRECT_DATA_FORMAT, 400);
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if ((jsonObject.get("name") == null || jsonObject.get("name").getAsString().isEmpty())
                || (jsonObject.get("description") == null || jsonObject.get("description").getAsString().isEmpty())) {

            Api.writeResponse(httpExchange, VariablesConstant.INCORRECT_DATA_FORMAT, 400);
            return;
        }

        Task task = new Task(jsonObject.get("name").getAsString(), jsonObject.get("description").getAsString());

        try {
            taskManager.addTask(task);
        } catch (ValidationException e) {
            Api.writeResponse(httpExchange, VariablesConstant.TASKS_CROSS_IN_TIME, 406);
            return;
        } catch (ManagerSaveException e) {
            Api.writeResponse(httpExchange, VariablesConstant.ERROR_SAVING_DATA_SERVER, 500);
            return;
        }

        Api.writeResponse(httpExchange, "", 200);
    }

    private void postTaskUpdateByID() throws IOException {
        Optional<Integer> taskId = Api.getIDTaskFromQueryParameterString(httpExchange);

        if (taskId.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.INVALID_ID, 400);
            return;
        }

        String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), Api.DEFAULT_CHARSET);
        Task task = GsonUtil.newGsonDefault().fromJson(bodyRequest, Task.class);

        try {
            taskManager.updateTask(task);
        } catch (ValidationException e) {
            Api.writeResponse(httpExchange, VariablesConstant.TASKS_CROSS_IN_TIME, 406);
            return;
        } catch (ManagerSaveException e) {
            Api.writeResponse(httpExchange, VariablesConstant.ERROR_SAVING_DATA_SERVER, 500);
            return;
        }

        Api.writeResponse(httpExchange, "", 201);
    }

    private void deleteTaskByID() throws IOException {
        Optional<Integer> taskId = Api.getIDTaskFromQueryParameterString(httpExchange);

        if (taskId.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.INVALID_ID, 400);
            return;
        }

        try {
            taskManager.deleteTaskByID(taskId.get());
        } catch (ManagerSaveException e) {
            Api.writeResponse(httpExchange, VariablesConstant.ERROR_SAVING_DATA_SERVER, 500);
            return;
        }

        Api.writeResponse(httpExchange, "", 200);
    }

}
