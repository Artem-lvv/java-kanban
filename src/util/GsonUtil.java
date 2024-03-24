package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import task.TaskStatus;
import task.TypeTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonUtil {
    private GsonUtil() {
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static LocalDateTimeTypeAdapter newLocalDateTimeTypeAdapter() {
        return new LocalDateTimeTypeAdapter();
    }

    public static DurationTypeAdapter newDurationTypeAdapter() {
        return new DurationTypeAdapter();
    }

    public static TaskStatusTypeAdapter newTaskStatusTypeAdapter() {
        return new TaskStatusTypeAdapter();
    }

    public static TypeTaskTypeAdapter newTypeTaskTypeAdapter() {
        return new TypeTaskTypeAdapter();
    }

    public static Gson newGsonDefault() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, newLocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, newDurationTypeAdapter())
                .registerTypeAdapter(TaskStatus.class, newTaskStatusTypeAdapter())
                .registerTypeAdapter(TypeTask.class, newTypeTaskTypeAdapter())
                .serializeNulls()
                .create();
    }

    private static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String date = jsonReader.nextString();
            if (!date.equals("null")) {
                return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
            } else {
                return null;
            }
        }
    }

    private static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(duration.toSeconds());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            String duration = jsonReader.nextString();
            if (!duration.equals("null")) {
                return Duration.ofSeconds(Integer.valueOf(duration));
            } else {
                return null;
            }
        }
    }

    private static class TaskStatusTypeAdapter extends TypeAdapter<TaskStatus> {

        @Override
        public void write(JsonWriter jsonWriter, TaskStatus taskStatus) throws IOException {
            if (taskStatus == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(taskStatus.name());
            }
        }

        @Override
        public TaskStatus read(JsonReader jsonReader) throws IOException {
            String taskStatus = jsonReader.nextString();
            if (!taskStatus.equals("null")) {
                return TaskStatus.valueOf(taskStatus);
            } else {
                return null;
            }
        }
    }

    private static class TypeTaskTypeAdapter extends TypeAdapter<TypeTask> {
        @Override
        public void write(JsonWriter jsonWriter, TypeTask typeTask) throws IOException {
            if (typeTask == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(typeTask.name());
            }
        }

        @Override
        public TypeTask read(JsonReader jsonReader) throws IOException {
            String typeTask = jsonReader.nextString();
            if (!typeTask.equals("null")) {
                return TypeTask.valueOf(typeTask);
            } else {
                return null;
            }
        }
    }
}

