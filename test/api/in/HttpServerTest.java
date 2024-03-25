package api.in;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {
    private TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    private Path pathFile;
    @Test
    void createHttpServer() throws IOException {
        pathFile = Files.createTempFile("TestFile", "Tasks.csv");

        taskManager = Managers.newFileBackedTaskManager(pathFile.toString());
        httpTaskServer = Managers.newHttpTaskServer(taskManager);
        httpTaskServer.start();

        assertAll("createHttpServer",
                () -> assertNotNull(httpTaskServer),
                () -> assertNotNull(httpTaskServer.getTaskManager()));

        httpTaskServer.stop();
    }

}
