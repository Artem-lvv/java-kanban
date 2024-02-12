package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyViewTasks = new HashMap<>();
    private Node lastNode;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(historyViewTasks.get(id));
        historyViewTasks.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

        private Node getPrev() {
            return prev;
        }
    }

    private List<Task> getTasks() {
        if (lastNode == null) {
            return Collections.emptyList();
        }

        List<Task> tasks = new ArrayList<>();
        for (Node node = lastNode; node != null; node = node.getPrev()) {
            tasks.add(node.item);
        }

        return tasks;
    }

    private void linkLast(Task task) {
        final Node oldLastNode = lastNode;
        final Node newNode = new Node(oldLastNode, task, null);
        lastNode = newNode;

        if (oldLastNode != null) {
            oldLastNode.next = newNode;
        }

        remove(task.getID()); // don't duplicate history
        historyViewTasks.put(task.getID(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        final Node prev = node.prev;
        final Node next = node.next;

        if (prev != null) {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            lastNode = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
    }

}
