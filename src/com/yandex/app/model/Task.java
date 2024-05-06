package com.yandex.app.model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Progress status;
    private Instant startTime;
    private Duration duration;

    public Task(String name, String description) {
        this.description = description;
        this.name = name;
        status = Progress.NEW;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.description = description;
        this.name = name;
        status = Progress.NEW;
        this.startTime = startTime.atZone(ZoneId.systemDefault()).toInstant();
        this.duration = duration;
    }

    public Task(String name, String description, int id, Progress status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, int id, Progress status, LocalDateTime startTime, Duration duration) {
        this.description = description;
        this.name = name;
        this.id = id;
        this.status = status;
        this.startTime = startTime.atZone(ZoneId.systemDefault()).toInstant();
        this.duration = duration;
    }

    //метод для получения времени завершения задачи
    public Instant getEndTime() {
        return startTime.plus(duration);
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

    public Instant getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
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

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    //метод для получения типа задачи
    public TaskType getType() {
        return TaskType.TASK;
    }

    protected String timeToString(Instant startTime) {
        ZonedDateTime time = startTime.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.HH:mm");
        return time.format(formatter);
    }

    @Override
    public String toString() {
        if (getDuration() == null || getStartTime() == null) {
            return  id + "," + getType() + ","
                    + name + "," + status + "," + description;
        }
        return  id + "," + getType() + ","
                + name + "," + status + "," + description + "," + timeToString(startTime) + "," + duration.toMinutes();
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
