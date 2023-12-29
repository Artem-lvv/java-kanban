import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Task task = new Task("test", "описание", TaskStatus.NEW);
        task.hashCode();
    }
}
