package util;

import api.in.Endpoint;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Api {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private Api() {
    }

    public static Endpoint getEndpoint(URI uri, String requestMethod, HttpExchange httpExchange) {
        String[] pathParts = uri.getPath().split("/");
        String pathEndpoint = pathParts[1];

        switch (pathEndpoint) {
            case "tasks":
                switch (requestMethod) {
                    case "GET":
                        return uri.getQuery() == null ? Endpoint.GET_TASKS : Endpoint.GET_TASK_BY_ID;
                    case "POST":
                        return uri.getQuery() == null ? Endpoint.POST_TASK_CREATE : Endpoint.POST_TASK_UPDATE_BY_ID;
                    case "DELETE":
                        return Endpoint.DELETE_TASK_BY_ID;
                    default:
                        break;
                }
                break;
            case "subtasks":
                switch (requestMethod) {
                    case "GET":
                        return uri.getQuery() == null ? Endpoint.GET_SUBTASKS : Endpoint.GET_SUBTASK_BY_ID;
                    case "POST":
                        return uri.getQuery() == null ?
                                Endpoint.POST_SUBTASK_CREATE : Endpoint.POST_SUBTASK_UPDATE_BY_ID;
                    case "DELETE":
                        return Endpoint.DELETE_SUBTASK_BY_ID;
                    default:
                        break;
                }
                break;
            case "epics":
                switch (requestMethod) {
                    case "GET":
                        if (uri.getQuery() == null) {
                            return Endpoint.GET_EPICS;
                        } else {
                            String[] querySplit = uri.getQuery().split("/");
                            Map<String, String> parametersQuery = Api.getQueryStringParameters(httpExchange);
                            if (querySplit.length == 1 && parametersQuery.containsKey("id")) {
                                return Endpoint.GET_EPIC_BY_ID;
                            } else if (querySplit.length == 2 && querySplit[1].equals("subtasks")) {
                                return Endpoint.GET_SUBTASKS_EPIC_BY_ID;
                            }
                        }
                        break;
                    case "POST":
                        return Endpoint.POST_EPIC_CREATE;
                    case "DELETE":
                        return Endpoint.DELETE_EPIC_BY_ID;
                    default:
                        break;
                }
                break;
            case "history":
                break;
            case "prioritized":
                break;
            default:
                break;
        }

        return Endpoint.UNKNOWN;
    }

    public static void writeResponse(HttpExchange exchange,
                                     String responseString,
                                     int responseCode) throws IOException {

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            if (!responseString.isBlank()) {
                os.write(responseString.getBytes(DEFAULT_CHARSET));
            }
        }
        exchange.close();
    }

    public static Map<String, String> getQueryStringParameters(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getQuery().split("/");

        if (pathParts.length == 0) {
            return Collections.emptyMap();
        }

        return Arrays.stream(pathParts)
                .filter(str -> str.contains("="))
                .collect(Collectors.toMap(strKey -> strKey.substring(0, strKey.indexOf("=")),
                        strValue -> strValue.substring(strValue.indexOf("=") + 1)));
    }

    public static Optional<Integer> getIDTaskFromQueryParameterString(HttpExchange exchange) {
        Map<String, String> mapQueryParameters = Api.getQueryStringParameters(exchange);

        if (!mapQueryParameters.containsKey("id")) {
            return Optional.empty();
        }

        int taskId;

        try {
            taskId = Integer.parseInt(mapQueryParameters.get("id"));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        return Optional.of(taskId);
    }
}
