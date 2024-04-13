package com.yandex.app.model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Progress status;

    public Task(String name, String description) {
        this.description = description;
        this.name = name;
        status = Progress.NEW;
    }

    public Task(String name, String description, int id, Progress status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    // getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public Progress getStatus() {
        return status;
    }

    public void setStatus(Progress status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return  id + "," + getClass().getSimpleName().toUpperCase() + ","
                + name + "," + status + "," + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return getId() == task.getId() && Objects.equals(getName(),
                task.getName()) && Objects.equals(getDescription(),
                task.getDescription()) && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDescription());
    }
}
