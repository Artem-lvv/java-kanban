package api.in.handler.task.relatedTask;

import api.in.Endpoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dto.relatedTask.EpicTaskDto;
import dto.relatedTask.SubTaskDto;
import exception.ManagerSaveException;
import exception.ValidationException;
import manager.TaskManager;
import task.relatedTask.EpicTask;
import util.Api;
import util.GsonUtil;
import util.DTOMapping;
import util.VariablesConstant;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicTaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private HttpExchange httpExchange;

    public EpicTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.httpExchange = exchange;

        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=utf-8");

        Endpoint endpoint = Api.getEndpoint(httpExchange.getRequestURI(), httpExchange.getRequestMethod(), exchange);

        switch (endpoint) {
            case GET_EPICS:
                getEpics();
                break;
            case GET_EPIC_BY_ID:
                getEpicByID();
                break;
            case GET_SUBTASKS_EPIC_BY_ID:
                getSubTasksEpicByID();
                break;
            case POST_EPIC_CREATE:
                postEpicCreate();
                break;
            case DELETE_EPIC_BY_ID:
                deleteEpicByID();
                break;
            default:
                break;
        }
    }

    private void getEpics() throws IOException {
        List<EpicTask> tasksList = taskManager.getListEpicTasks();

        if (tasksList.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.LIST_TASKS_EMPTY, 204);
        } else {
            List<EpicTaskDto> taskDtoList = tasksList.stream().map(DTOMapping::mapToEpicTaskDto).toList();
            Gson gson = GsonUtil.newGsonDefault();
            String body = gson.toJson(taskDtoList);
            Api.writeResponse(httpExchange, body, 200);
        }
    }

    private void getEpicByID() throws IOException {
        Optional<Integer> taskId = Api.getIDTaskFromQueryParameterString(httpExchange);

        if (taskId.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.INVALID_ID, 400);
            return;
        }

        Optional<EpicTask> taskOptional = taskManager.getEpicTaskByID(taskId.get());

        if (taskOptional.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.NOT_FOUND_TASK_BY_ID, 404);
            return;
        }

        EpicTaskDto epicTaskDto = DTOMapping.mapToEpicTaskDto(taskOptional.get());
        String body = GsonUtil.newGsonDefault().toJson(epicTaskDto);

        Api.writeResponse(httpExchange, body, 200);
    }

    private void getSubTasksEpicByID() throws IOException {
        Optional<Integer> taskId = Api.getIDTaskFromQueryParameterString(httpExchange);

        if (taskId.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.INVALID_ID, 400);
            return;
        }

        Optional<EpicTask> taskOptional = taskManager.getEpicTaskByID(taskId.get());

        if (taskOptional.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.NOT_FOUND_TASK_BY_ID, 404);
            return;
        }

        List<SubTaskDto> subTaskDtoList = taskOptional.get().getIdSubTaskList()
                .stream()
                .map(id -> taskManager.getSubTaskByID(id).get())
                .map(DTOMapping::mapToSubTaskDto).toList();

        if (subTaskDtoList.isEmpty()) {
            Api.writeResponse(httpExchange, VariablesConstant.LIST_TASKS_EMPTY, 204);
            return;
        }

        String body = GsonUtil.newGsonDefault().toJson(subTaskDtoList);
        Api.writeResponse(httpExchange, body, 200);
    }

    private void postEpicCreate() throws IOException {
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

        EpicTask task = new EpicTask(jsonObject.get("name").getAsString(), jsonObject.get("description").getAsString());

        try {
            taskManager.addEpicTask(task);
        } catch (ValidationException e) {
            Api.writeResponse(httpExchange, VariablesConstant.TASKS_CROSS_IN_TIME, 406);
            return;
        } catch (ManagerSaveException e) {
            Api.writeResponse(httpExchange, VariablesConstant.ERROR_SAVING_DATA_SERVER, 500);
            return;
        }

        Api.writeResponse(httpExchange, "", 200);
    }

    private void deleteEpicByID() throws IOException {
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
