package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(tail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        historyMap.put(task.getId(), newNode);
    }

    public void removeNode(Node node) {
        if (historyMap.containsValue(node)) {
            Node previousNode = node.getPrev();
            Node nextNode = node.getNext();
            if (previousNode == null && nextNode == null) {
                return;
            } else if (previousNode == null) {
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

    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        history.add(head.getCurrent());
        Node currentNode = head;
        for (int i = 1; i < historyMap.size(); i++) {
            Node nextNode = currentNode.getNext();
            history.add(nextNode.getCurrent());
            currentNode = nextNode;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        removeNode(node);
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }
}
