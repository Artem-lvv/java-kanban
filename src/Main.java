import task.Task;
import task.TaskStatus;
import task.TypeTask;
import task.relatedTask.EpicTask;
import task.relatedTask.SubTask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // debug
        TaskManager tm = new TaskManager();

        EpicTask epicOne = new EpicTask("Epic test 1", "Описание Epic test 1");
        SubTask subTaskOne = new SubTask("SubTask test 1", "Описание SubTask test 1", epicOne);
        SubTask subTaskTwo = new SubTask("SubTask test 2", "Описание SubTask test 2", epicOne);
        epicOne.addSubTask(subTaskOne);
        epicOne.addSubTask(subTaskTwo);

        tm.addEpicTask(epicOne);
        tm.addSubTask(subTaskOne);
        tm.addSubTask(subTaskTwo);

        System.out.println(tm.getListEpicTasks());
        System.out.println(tm.getListSubTasks());

        subTaskOne.setStatus(TaskStatus.IN_PROGRESS);

        tm.updateTaskByType(TypeTask.SUBTASK, subTaskOne);
        System.out.println(tm.getListEpicTasks());

        subTaskOne.setStatus(TaskStatus.DONE);
        tm.deleteTaskByTypeAndID(TypeTask.SUBTASK, 3);
        System.out.println(tm.getListEpicTasks());

        EpicTask epicTwo = new EpicTask("Epic test 2", "Описание Epic test 2");
        SubTask subTaskThree = new SubTask("SubTask test 3", "Описание SubTask test 3", epicTwo);
        epicTwo.addSubTask(subTaskThree);

        tm.addEpicTask(epicTwo);
        tm.addSubTask(subTaskThree);

        tm.deleteTaskByTypeAndID(TypeTask.EPIC, epicTwo.getID());


        Task taskOne = new Task("Task test 1", "Описание Task test 1");
        Task taskTwo = new Task("Task test 2", "Описание Task test 2");
        tm.addTask(taskOne);
        tm.addTask(taskTwo);

        System.out.println(tm.getListTasks());
        System.out.println(tm.getListEpicTasks());
        System.out.println(tm.getListSubTasks());

    }
}
