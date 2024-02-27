import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        Path path = Files.createTempFile("TestFile", "Tasks.csv");

        TaskManager tm = Managers.getFileBackedTaskManager(path.toString());

        EpicTask epicTaskFirst = new EpicTask("Epic test 1", "Описание Epic test 1");

        SubTask subTaskFirst = new SubTask("SubTask test 1", "Описание SubTask test 1",
                epicTaskFirst.getID());
        SubTask subTaskSecond = new SubTask("SubTask test 2", "Описание SubTask test 2",
                epicTaskFirst.getID());

        EpicTask epicTaskSecond = new EpicTask("Epic test 2", "Описание Epic test 2");

        tm.addEpicTask(epicTaskFirst);
        tm.addSubTask(subTaskFirst);
        tm.addSubTask(subTaskSecond);

        epicTaskFirst.addSubTask(subTaskFirst.getID());
        epicTaskFirst.addSubTask(subTaskSecond.getID());

        Task taskFirst = new Task("Task test 1", "Description task test 1");
        tm.addTask(taskFirst);

        tm.addEpicTask(epicTaskSecond);

        tm.getTaskByID(taskFirst.getID());
        tm.getEpicTaskByID(epicTaskFirst.getID());

        System.out.println(tm.getHistory());

        /*TaskManager tm = Managers.getDefault();

        Task taskFirst = new Task("Task test 1", "Description task test 1");
        Task taskSecond = new Task("Task test 2", "Description task test 2");

        tm.addTask(taskFirst);
        tm.addTask(taskSecond);

        EpicTask epicTaskFirst = new EpicTask("Epic test 1", "Описание Epic test 1");
        SubTask subTaskFirst = new SubTask("SubTask test 1", "Описание SubTask test 1", epicTaskFirst);
        SubTask subTaskSecond = new SubTask("SubTask test 2", "Описание SubTask test 2", epicTaskFirst);
        SubTask subTaskThird = new SubTask("SubTask test 3", "Описание SubTask test 3", epicTaskFirst);

        tm.addEpicTask(epicTaskFirst);
        tm.addSubTask(subTaskFirst);
        tm.addSubTask(subTaskSecond);
        tm.addSubTask(subTaskThird);

        EpicTask epicTaskSecond = new EpicTask("Epic test 2", "Описание Epic test 2");

        tm.addEpicTask(epicTaskSecond);

        tm.getSubTaskByID(subTaskThird.getID());
        tm.getTaskByID(taskFirst.getID());
        tm.getSubTaskByID(subTaskSecond.getID());
        tm.getEpicTaskByID(epicTaskFirst.getID());
        tm.getEpicTaskByID(epicTaskSecond.getID());
        tm.getSubTaskByID(subTaskSecond.getID());

        tm.getHistory().forEach(System.out::println);
        System.out.println();

        tm.deleteTaskByID(taskFirst.getID());

        tm.getHistory().forEach(System.out::println);
        System.out.println();

        tm.deleteTaskByID(epicTaskFirst.getID());

        tm.getHistory().forEach(System.out::println);*/
    }
}
