package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        removeNode(historyMap.get(task.getId()));
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(tail, task, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
        historyMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node previousNode = node.getPrev();
            Node nextNode = node.getNext();
            if (previousNode == null && nextNode == null) {
                head = null;
                tail = null;
                return;
            }
            if (previousNode == null) {
                nextNode.setPrev(null);
                head = nextNode;
            } else if (nextNode == null) {
                previousNode.setNext(null);
                tail = previousNode;
            } else {
                previousNode.setNext(nextNode);
                nextNode.setPrev(previousNode);
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> history = new LinkedList<>();
        Node current = head;
        while (current != null) {
            history.add(current.getCurrent());
            current = current.getNext();
        }
        return history;
    }
}
