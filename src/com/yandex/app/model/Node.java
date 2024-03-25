package com.yandex.app.model;

import java.util.Objects;

public class Node {
    private Node prev;
    private final Task current;
    private Node next;

    public Node(Node prev, Task data, Node next) {
        this.prev = prev;
        this.current = data;
        this.next = next;
    }

    public Task getCurrent() {
        return current;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(getPrev(), node.getPrev()) &&
                Objects.equals(getCurrent(), node.getCurrent()) &&
                Objects.equals(getNext(), node.getNext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrev(), getCurrent(), getNext());
    }
}