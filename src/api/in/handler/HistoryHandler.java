package api.in.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dto.task.TaskDto;
import manager.TaskManager;
import task.Task;
import util.Api;
import util.DTOMapping;
import util.GsonUtil;

import java.io.IOException;
import java.util.List;

public class HistoryHandler implements HttpHandler {
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=utf-8");

        List<Task> historyList = taskManager.getHistory();

        if (historyList.isEmpty()) {
            Api.writeResponse(exchange, "", 204);
        } else {
            List<TaskDto> taskDtoList = historyList.stream().map(DTOMapping::mapToTaskDto).toList();
            Gson gson = GsonUtil.newGsonDefault();
            String body = gson.toJson(taskDtoList);
            Api.writeResponse(exchange, body, 200);
        }
    }
}
