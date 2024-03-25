package api.in.handler.task.relatedTask;

import api.in.Endpoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dto.relatedTask.SubTaskDto;
import exception.ManagerSaveException;
import exception.ValidationException;
import manager.TaskManager;
import task.relatedTask.SubTask;
import util.DTOMapping;
import util.GsonUtil;
import util.Api;
import util.VariablesConstant;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubTasksHandler implements HttpHandler {
    private final TaskManager taskManager;
    private HttpExchange httpExchange;

    public SubTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.httpExchange = exchange;

        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=utf-8");

        Endpoint endpoint = Api.getEndpoint(httpExchange.getRequestURI(), httpExchange.getRequestMethod(), exchange);

        switch (endpoint) {
            case GET_SUBTASKS:
                getSubTasks();
                break;
            case GET_SUBTASK_BY_ID:
                getSubTaskByID();
                break;
            case POST_SUBTASK_CREATE:
                postSubTaskCreate();
                break;
            case POST_SUBTASK_UPDATE_BY_ID:
                postSubTaskUpdateByID();
                break;
            case DELETE_SUBTASK_BY_ID:
                deleteSubTaskByID();
                break;
            default:
                break;
        }
    }

    private void getSubTasks() throws IOException {
        List<SubTask> tasksList = taskManager.getListSubTasks();

        if (tasksList.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.LIST_TASKS_EMPTY, 204);
        } else {
            List<SubTaskDto> taskDtoList = tasksList.stream().map(DTOMapping::mapToSubTaskDto).toList();
            Gson gson = GsonUtil.newGsonDefault();
            String body = gson.toJson(taskDtoList);
            Api.writeResponse(httpExchange, body, 200);
        }
    }

    private void getSubTaskByID() throws IOException {
        Optional<Integer> taskId = Api.getIDTaskFromQueryParameterString(httpExchange);

        if (taskId.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.INVALID_ID, 400);
            return;
        }

        Optional<SubTask> taskOptional = taskManager.getSubTaskByID(taskId.get());

        if (taskOptional.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.NOT_FOUND_TASK_BY_ID, 404);
            return;
        }

        SubTaskDto subTaskDto = DTOMapping.mapToSubTaskDto(taskOptional.get());
        String body = GsonUtil.newGsonDefault().toJson(subTaskDto);

        Api.writeResponse(httpExchange, body, 200);
    }

    private void postSubTaskCreate() throws IOException {
        String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), Api.DEFAULT_CHARSET);
        JsonElement jsonElement = JsonParser.parseString(bodyRequest);

        if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
            Api.writeResponse(httpExchange, VariablesConstant.INCORRECT_DATA_FORMAT, 400);
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if ((jsonObject.get("name") == null || jsonObject.get("name").getAsString().isEmpty())
                || (jsonObject.get("description") == null || jsonObject.get("description").getAsString().isEmpty())
                || (jsonObject.get("idRelatedEpicTask") == null
                        || jsonObject.get("idRelatedEpicTask").getAsString().isEmpty())) {
            Api.writeResponse(httpExchange, VariablesConstant.INCORRECT_DATA_FORMAT, 400);
            return;
        }

        SubTask task = new SubTask(jsonObject.get("name").getAsString(),
                jsonObject.get("description").getAsString(),
                Integer.valueOf(jsonObject.get("idRelatedEpicTask").getAsString()));

        try {
            taskManager.addSubTask(task);
        } catch (ValidationException e) {
            Api.writeResponse(httpExchange, VariablesConstant.TASKS_CROSS_IN_TIME, 406);
            return;
        } catch (ManagerSaveException e) {
            Api.writeResponse(httpExchange, VariablesConstant.ERROR_SAVING_DATA_SERVER, 500);
            return;
        }

        Api.writeResponse(httpExchange, "", 200);
    }

    private void postSubTaskUpdateByID() throws IOException {
        Optional<Integer> taskId = Api.getIDTaskFromQueryParameterString(httpExchange);

        if (taskId.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.INVALID_ID, 400);
            return;
        }

        String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), Api.DEFAULT_CHARSET);
        SubTask task = GsonUtil.newGsonDefault().fromJson(bodyRequest, SubTask.class);

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

    private void deleteSubTaskByID() throws IOException {
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
