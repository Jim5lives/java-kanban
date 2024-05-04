package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description,Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description,int id, Progress status, Integer epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, Progress status, Integer epicId,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }


    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return  getId() + "," + getType() + ","
                + getName() + "," + getStatus() + "," + getDescription() + "," + timeToString(getStartTime()) + ","
                + getDuration().toMinutes()  + "," + getEpicId();
    }
}
